import GRPCCore
import GRPCNIOTransportHTTP2

public typealias News = Demo_News_V1_News
public typealias Topic = Demo_News_V1_Topic

extension Topic {
    /// Topics a news item can be published in.
    public static let publishable: [Topic] = [.tech, .sport, .economy]

    /// "Tech", "Sport", "Economy" (and "All" for `.unspecified`).
    public var label: String {
        switch self {
        case .unspecified: "All"
        case .tech: "Tech"
        case .sport: "Sport"
        case .economy: "Economy"
        case .UNRECOGNIZED(let value): "Topic \(value)"
        }
    }
}

public protocol NewsRepository: Sendable {
    /// News of `topic`, or of every topic for `.unspecified`.
    func getNews(topic: Topic) async throws -> [News]

    /// News published from now on in `topic` (every topic for `.unspecified`).
    func subscribe(topic: Topic) -> AsyncThrowingStream<News, any Error>

    func postNews(title: String, description: String, imageURL: String, topic: Topic) async throws -> News
}

/// `NewsRepository` backed by the gRPC NewsService, through the grpc-swift 2 generated client.
public final class GrpcNewsRepository: NewsRepository {
    private let grpcClient: GRPCClient<HTTP2ClientTransport.Posix>
    private let client: Demo_News_V1_NewsService.Client<HTTP2ClientTransport.Posix>
    private let connections: Task<Void, Never>

    /// Opens a plaintext HTTP/2 connection to the server at `host:port`.
    public init(host: String, port: Int) throws {
        var config = HTTP2ClientTransport.Posix.Config.defaults
        // While a call is open, ping the server: a Subscribe stream only reads, and a connection
        // dropped by a NAT or a lost network would otherwise look alive forever.
        config.connection.keepalive = .init(time: .seconds(20), timeout: .seconds(5), allowWithoutCalls: false)
        let transport = try HTTP2ClientTransport.Posix(
            target: .dns(host: host, port: port),
            transportSecurity: .plaintext,
            config: config
        )
        grpcClient = GRPCClient(transport: transport)
        client = Demo_News_V1_NewsService.Client(wrapping: grpcClient)
        connections = Task { [grpcClient] in
            try? await grpcClient.runConnections()
        }
    }

    deinit {
        grpcClient.beginGracefulShutdown()
    }

    public func getNews(topic: Topic) async throws -> [News] {
        try await client.getNews(.with { $0.topic = topic }, options: Self.callOptions).news
    }

    public func subscribe(topic: Topic) -> AsyncThrowingStream<News, any Error> {
        AsyncThrowingStream { continuation in
            let call = Task { [client] in
                do {
                    try await client.subscribe(.with { $0.topic = topic }) { response in
                        for try await message in response.messages {
                            continuation.yield(message.news)
                        }
                    }
                    continuation.finish()
                } catch {
                    continuation.finish(throwing: error)
                }
            }
            continuation.onTermination = { _ in call.cancel() }
        }
    }

    public func postNews(title: String, description: String, imageURL: String, topic: Topic) async throws -> News {
        let request = Demo_News_V1_PostNewsRequest.with {
            $0.title = title
            $0.description_p = description
            $0.imageURL = imageURL
            $0.topic = topic
        }
        return try await client.postNews(request, options: Self.callOptions).news
    }

    private static let callOptions: CallOptions = {
        var options = CallOptions.defaults
        options.timeout = .seconds(10)
        return options
    }()
}

/// A readable message for a failed call: the server's description, or the reason the server was not reached.
public func errorMessage(_ error: any Error) -> String {
    guard let rpcError = error as? RPCError else { return error.localizedDescription }
    switch rpcError.code {
    case .unavailable: return "the server is unreachable"
    case .deadlineExceeded: return "the server did not answer in time"
    default: return rpcError.message.isEmpty ? String(describing: rpcError.code) : rpcError.message
    }
}

import Foundation
import GRPCCore
import Synchronization
import Testing
@testable import NewsKit

@MainActor
struct NewsViewModelTests {
    let repository = FakeNewsRepository()

    @Test func showsTheNewsOfEveryTopic() async throws {
        let viewModel = NewsViewModel(repository: repository)
        viewModel.select(.unspecified)

        try await eventually { !viewModel.loading }
        #expect(viewModel.news.map(\.id) == ["tech-0", "sport-0"])
        #expect(viewModel.error == nil)
    }

    @Test func followsOnlyTheSelectedTopic() async throws {
        let viewModel = NewsViewModel(repository: repository)
        viewModel.select(.unspecified)
        try await eventually { repository.subscriberCount == 1 }

        viewModel.select(.sport)
        try await eventually { !viewModel.loading && repository.subscriberCount == 1 } // the "All" stream ended
        repository.publish(news("tech-1", .tech))
        repository.publish(news("sport-1", .sport))

        try await eventually { viewModel.news.count == 2 }
        #expect(viewModel.news.map(\.id) == ["sport-0", "sport-1"])
        #expect(viewModel.breakingNews?.id == "sport-1")
    }

    @Test func reportsAFailedLoadAndRetryRecovers() async throws {
        repository.getNewsError = RPCError(code: .unavailable, message: "connection refused")
        let viewModel = NewsViewModel(repository: repository)
        viewModel.select(.tech)

        try await eventually { !viewModel.loading }
        #expect(viewModel.error == "Could not load the news: the server is unreachable")

        repository.getNewsError = nil
        viewModel.retry()
        try await eventually { !viewModel.loading }
        #expect(viewModel.error == nil)
        #expect(viewModel.news.map(\.id) == ["tech-0"])
    }

    @Test func postReturnsTheServerMessageOnFailure() async throws {
        let viewModel = NewsViewModel(repository: repository)
        viewModel.select(.tech)
        try await eventually { repository.subscriberCount == 1 }

        #expect(await viewModel.post(title: "Hello", description: "From iOS", topic: .tech) == nil)
        try await eventually { viewModel.news.last?.title == "Hello" } // received through the stream
        #expect(await viewModel.post(title: " ", description: "", topic: .tech) == "title must not be blank")
    }
}

/// Runs against a real server when NEWS_SERVER_PORT is set, e.g. `NEWS_SERVER_PORT=6565 swift test`.
@Suite(.enabled(if: ProcessInfo.processInfo.environment["NEWS_SERVER_PORT"] != nil))
@MainActor
struct GrpcNewsRepositoryTests {
    @Test func getSubscribeAndPostAgainstTheServer() async throws {
        let port = Int(ProcessInfo.processInfo.environment["NEWS_SERVER_PORT"]!)!
        let repository = try GrpcNewsRepository(host: "localhost", port: port)
        let viewModel = NewsViewModel(repository: repository)

        viewModel.select(.economy)
        try await eventually { !viewModel.loading }
        #expect(viewModel.error == nil)
        #expect(viewModel.news.allSatisfy { $0.topic == .economy })
        let loaded = viewModel.news.count

        // The Subscribe stream may need a moment to reach the server: post until it is received.
        let title = "From Swift \(UUID().uuidString.prefix(8))"
        try await eventually(timeout: .seconds(10)) {
            if viewModel.news.contains(where: { $0.title == title }) { return true }
            _ = await viewModel.post(title: title, description: "", topic: .economy)
            return false
        }
        #expect(viewModel.news.count > loaded)
        #expect(await viewModel.post(title: "", description: "", topic: .economy) == "title must not be blank")
    }
}

/// Polls `condition` until it holds, or fails the test after `timeout`.
@MainActor
func eventually(
    timeout: Duration = .seconds(5),
    _ condition: @MainActor () async -> Bool,
    sourceLocation: SourceLocation = #_sourceLocation
) async throws {
    let deadline = ContinuousClock.now + timeout
    while !(await condition()) {
        guard ContinuousClock.now < deadline else {
            Issue.record("Condition not met within \(timeout)", sourceLocation: sourceLocation)
            return
        }
        try await Task.sleep(for: .milliseconds(20))
    }
}

func news(_ id: String, _ topic: Topic, title: String? = nil) -> News {
    .with {
        $0.id = id
        $0.title = title ?? id
        $0.topic = topic
    }
}

final class FakeNewsRepository: NewsRepository {
    private struct State {
        var stored = [news("tech-0", .tech), news("sport-0", .sport)]
        var subscribers: [UUID: (topic: Topic, continuation: AsyncThrowingStream<News, any Error>.Continuation)] = [:]
        var getNewsError: (any Error)?
    }

    private let state = Mutex(State())

    var subscriberCount: Int { state.withLock { $0.subscribers.count } }

    var getNewsError: (any Error)? {
        get { state.withLock { $0.getNewsError } }
        set { state.withLock { $0.getNewsError = newValue } }
    }

    func publish(_ item: News) {
        let continuations = state.withLock { state in
            state.subscribers.values.filter { $0.topic == .unspecified || $0.topic == item.topic }.map(\.continuation)
        }
        continuations.forEach { $0.yield(item) }
    }

    func getNews(topic: Topic) async throws -> [News] {
        try state.withLock { state in
            if let error = state.getNewsError { throw error }
            return state.stored.filter { topic == .unspecified || $0.topic == topic }
        }
    }

    func subscribe(topic: Topic) -> AsyncThrowingStream<News, any Error> {
        let (stream, continuation) = AsyncThrowingStream<News, any Error>.makeStream()
        let id = UUID()
        state.withLock { $0.subscribers[id] = (topic, continuation) }
        continuation.onTermination = { [weak self] _ in
            _ = self?.state.withLock { $0.subscribers.removeValue(forKey: id) }
        }
        return stream
    }

    func postNews(title: String, description: String, imageURL: String, topic: Topic) async throws -> News {
        guard !title.trimmingCharacters(in: .whitespaces).isEmpty else {
            throw RPCError(code: .invalidArgument, message: "title must not be blank")
        }
        let created = news("\(topic.label.lowercased())-\(state.withLock { $0.stored.count })", topic, title: title)
        state.withLock { $0.stored.append(created) }
        publish(created)
        return created
    }
}

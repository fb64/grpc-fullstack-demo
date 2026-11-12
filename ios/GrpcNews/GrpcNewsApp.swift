import NewsKit
import SwiftUI

@main
struct GrpcNewsApp: App {
    @State private var viewModel = NewsViewModel(repository: Self.makeRepository())

    var body: some Scene {
        WindowGroup {
            ContentView(viewModel: viewModel)
        }
    }

    /// Server address from Info.plist (NEWS_SERVER_HOST / NEWS_SERVER_PORT build settings in project.yml).
    /// The simulator shares the Mac's network, so the default `localhost:6565` reaches a server running on the Mac.
    private static func makeRepository() -> GrpcNewsRepository {
        let host = Bundle.main.object(forInfoDictionaryKey: "NewsServerHost") as? String ?? "localhost"
        let port = Int(Bundle.main.object(forInfoDictionaryKey: "NewsServerPort") as? String ?? "") ?? 6565
        do {
            return try GrpcNewsRepository(host: host, port: port)
        } catch {
            fatalError("Invalid news server address \(host):\(port): \(error)")
        }
    }
}

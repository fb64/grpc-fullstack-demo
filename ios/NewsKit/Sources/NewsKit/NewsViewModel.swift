import Foundation
import Observation

/// News of the selected topic, kept up to date by a Subscribe stream.
@MainActor
@Observable
public final class NewsViewModel {
    public private(set) var topic: Topic = .unspecified
    public private(set) var news: [News] = []
    public private(set) var loading = false
    public private(set) var error: String?
    /// Latest news received live, until `dismissBreakingNews()`.
    public private(set) var breakingNews: News?

    private let repository: any NewsRepository
    private var topicTask: Task<Void, Never>?

    public init(repository: any NewsRepository) {
        self.repository = repository
    }

    /// Shows the news of `topic` and follows the new ones, replacing the previous topic's stream.
    public func select(_ topic: Topic) {
        topicTask?.cancel()
        self.topic = topic
        news = []
        loading = true
        error = nil
        topicTask = Task {
            // Subscribe and load concurrently, so nothing published while GetNews runs is missed.
            async let following: Void = follow(topic)
            await load(topic)
            await following
        }
    }

    public func retry() {
        select(topic)
    }

    public func dismissBreakingNews() {
        breakingNews = nil
    }

    /// Publishes a news item; returns an error message, or nil on success.
    public func post(title: String, description: String, topic: Topic) async -> String? {
        do {
            let imageURL = "https://picsum.photos/seed/\(UUID().uuidString)/400/400"
            _ = try await repository.postNews(title: title, description: description, imageURL: imageURL, topic: topic)
            return nil
        } catch {
            return errorMessage(error)
        }
    }

    private func load(_ topic: Topic) async {
        do {
            let loaded = try await repository.getNews(topic: topic)
            guard !Task.isCancelled else { return }
            let loadedIDs = Set(loaded.map(\.id))
            news = loaded + news.filter { !loadedIDs.contains($0.id) }
        } catch {
            guard !Task.isCancelled else { return }
            self.error = "Could not load the news: \(errorMessage(error))"
        }
        loading = false
    }

    private func follow(_ topic: Topic) async {
        do {
            for try await item in repository.subscribe(topic: topic) {
                news.append(item)
                breakingNews = item
            }
            guard !Task.isCancelled else { return }
            error = "Live updates ended"
        } catch {
            guard !Task.isCancelled else { return }
            self.error = "Live updates stopped: \(errorMessage(error))"
        }
    }
}

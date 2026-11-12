import NewsKit
import SwiftUI

struct TopicTab: Hashable {
    let topic: Topic
    let systemImage: String
}

struct ContentView: View {
    let viewModel: NewsViewModel
    @State private var topic: Topic = .unspecified

    private static let tabs = [
        TopicTab(topic: .unspecified, systemImage: "list.bullet"),
        TopicTab(topic: .tech, systemImage: "lightbulb"),
        TopicTab(topic: .sport, systemImage: "figure.run"),
        TopicTab(topic: .economy, systemImage: "dollarsign.circle"),
    ]

    var body: some View {
        TabView(selection: $topic) {
            ForEach(Self.tabs, id: \.topic) { tab in
                Tab(tab.topic.label, systemImage: tab.systemImage, value: tab.topic) {
                    NewsListView(viewModel: viewModel)
                }
            }
        }
        .onChange(of: topic, initial: true) {
            viewModel.select(topic)
        }
    }
}

/// The news of the selected topic, with the error banner, the breaking news banner and the publish sheet.
struct NewsListView: View {
    let viewModel: NewsViewModel
    @State private var publishing = false

    var body: some View {
        NavigationStack {
            ScrollViewReader { proxy in
                List(viewModel.news, id: \.id) { news in
                    NewsRow(news: news)
                }
                .listStyle(.plain)
                .overlay {
                    if viewModel.loading && viewModel.news.isEmpty {
                        ProgressView()
                    }
                }
                .safeAreaInset(edge: .top) {
                    if let error = viewModel.error {
                        ErrorBanner(message: error, retry: viewModel.retry)
                    }
                }
                .overlay(alignment: .bottom) {
                    if let news = viewModel.breakingNews {
                        BreakingNewsBanner(news: news) {
                            withAnimation { proxy.scrollTo(news.id) }
                            viewModel.dismissBreakingNews()
                        }
                        .task(id: news.id) {
                            try? await Task.sleep(for: .seconds(5))
                            viewModel.dismissBreakingNews()
                        }
                        .transition(.move(edge: .bottom).combined(with: .opacity))
                    }
                }
                .animation(.default, value: viewModel.breakingNews?.id)
            }
            .navigationTitle(viewModel.topic == .unspecified ? "All news" : "\(viewModel.topic.label) news")
            .toolbar {
                Button("Publish", systemImage: "plus") { publishing = true }
            }
            .sheet(isPresented: $publishing) {
                PostNewsView(viewModel: viewModel)
            }
        }
    }
}

struct NewsRow: View {
    let news: News

    var body: some View {
        HStack(alignment: .top, spacing: 12) {
            AsyncImage(url: URL(string: news.imageURL)) { image in
                image.resizable().scaledToFill()
            } placeholder: {
                Color.secondary.opacity(0.15)
            }
            .frame(width: 80, height: 80)
            .clipShape(RoundedRectangle(cornerRadius: 12))

            VStack(alignment: .leading, spacing: 4) {
                Text(news.title).font(.headline)
                if !news.description_p.isEmpty {
                    Text(news.description_p).font(.subheadline).foregroundStyle(.secondary)
                }
                Text(news.topic.label).font(.caption).foregroundStyle(.tint)
            }
        }
        .padding(.vertical, 4)
    }
}

struct ErrorBanner: View {
    let message: String
    let retry: () -> Void

    var body: some View {
        HStack {
            Text(message).font(.subheadline)
            Spacer()
            Button("Retry", action: retry)
        }
        .padding()
        .background(.red.opacity(0.15), in: RoundedRectangle(cornerRadius: 12))
        .padding(.horizontal)
    }
}

struct BreakingNewsBanner: View {
    let news: News
    let show: () -> Void

    var body: some View {
        HStack {
            Text("Breaking news: \(news.title)").font(.subheadline.weight(.semibold)).lineLimit(2)
            Spacer()
            Button("Show", action: show).buttonStyle(.borderedProminent)
        }
        .padding()
        .background(.regularMaterial, in: RoundedRectangle(cornerRadius: 16))
        .shadow(radius: 8)
        .padding()
    }
}

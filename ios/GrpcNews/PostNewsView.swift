import NewsKit
import SwiftUI

/// Sheet to publish a news item with PostNews.
struct PostNewsView: View {
    let viewModel: NewsViewModel
    @Environment(\.dismiss) private var dismiss
    @State private var title = ""
    @State private var description = ""
    @State private var topic: Topic = .tech
    @State private var sending = false
    @State private var error: String?

    var body: some View {
        NavigationStack {
            Form {
                TextField("Title", text: $title)
                TextField("Description", text: $description, axis: .vertical)
                    .lineLimit(2...5)
                Picker("Topic", selection: $topic) {
                    ForEach(Topic.publishable, id: \.self) { Text($0.label).tag($0) }
                }
                .pickerStyle(.segmented)
                if let error {
                    Text(error).foregroundStyle(.red)
                }
            }
            .navigationTitle("Publish a news item")
            .navigationBarTitleDisplayMode(.inline)
            .toolbar {
                ToolbarItem(placement: .cancellationAction) {
                    Button("Cancel") { dismiss() }
                }
                ToolbarItem(placement: .confirmationAction) {
                    Button("Publish") {
                        Task {
                            sending = true
                            error = await viewModel.post(title: title, description: description, topic: topic)
                            sending = false
                            if error == nil { dismiss() }
                        }
                    }
                    .disabled(title.trimmingCharacters(in: .whitespaces).isEmpty || sending)
                }
            }
            .onAppear {
                if Topic.publishable.contains(viewModel.topic) { topic = viewModel.topic }
            }
        }
        .presentationDetents([.medium, .large])
    }
}

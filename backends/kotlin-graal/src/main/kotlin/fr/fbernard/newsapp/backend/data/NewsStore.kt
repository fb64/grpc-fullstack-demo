package fr.fbernard.newsapp.backend.data

import fr.fbernard.grpc.demo.news.v1.News
import fr.fbernard.grpc.demo.news.v1.Topic
import fr.fbernard.grpc.demo.news.v1.news

/** Topics a news item can actually belong to (excludes the "match all" and unknown values). */
val PUBLISHABLE_TOPICS = Topic.entries.filter { it != Topic.TOPIC_UNSPECIFIED && it != Topic.UNRECOGNIZED }

/** In-memory, thread-safe news storage, seeded with [seedPerTopic] items for each topic. */
class NewsStore(seedPerTopic: Int = 10) {

    private val newsByTopic: Map<Topic, MutableList<News>> =
        PUBLISHABLE_TOPICS.associateWith { topic ->
            MutableList(seedPerTopic) { index ->
                news {
                    id = newsId(topic, index)
                    this.topic = topic
                    title = "${topic.label} Title $index"
                    description = "${topic.label.lowercase()} description $index"
                    imageUrl = "https://picsum.photos/400/400/?${newsId(topic, index)}"
                }
            }
        }

    /** Returns the stored news for [topic], or for every topic when it is [Topic.TOPIC_UNSPECIFIED]. */
    fun byTopic(topic: Topic): List<News> = synchronized(this) {
        if (topic == Topic.TOPIC_UNSPECIFIED) {
            newsByTopic.values.flatten()
        } else {
            newsByTopic[topic].orEmpty().toList()
        }
    }

    /** Stores [news] under a new server-assigned id and returns the stored copy. */
    fun add(news: News): News = synchronized(this) {
        val list = requireNotNull(newsByTopic[news.topic]) { "Cannot store news with topic ${news.topic}" }
        news.toBuilder().setId(newsId(news.topic, list.size)).build().also(list::add)
    }

    private fun newsId(topic: Topic, index: Int) = "${topic.label.lowercase()}-$index"

    private val Topic.label get() = name.removePrefix("TOPIC_")
}

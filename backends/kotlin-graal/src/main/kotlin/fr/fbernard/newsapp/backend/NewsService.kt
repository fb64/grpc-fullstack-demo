package fr.fbernard.newsapp.backend

import fr.fbernard.grpc.demo.news.v1.GetNewsRequest
import fr.fbernard.grpc.demo.news.v1.GetNewsResponse
import fr.fbernard.grpc.demo.news.v1.News
import fr.fbernard.grpc.demo.news.v1.NewsServiceGrpcKt
import fr.fbernard.grpc.demo.news.v1.PostNewsRequest
import fr.fbernard.grpc.demo.news.v1.PostNewsResponse
import fr.fbernard.grpc.demo.news.v1.SubscribeRequest
import fr.fbernard.grpc.demo.news.v1.SubscribeResponse
import fr.fbernard.grpc.demo.news.v1.Topic
import fr.fbernard.grpc.demo.news.v1.getNewsResponse
import fr.fbernard.grpc.demo.news.v1.news
import fr.fbernard.grpc.demo.news.v1.postNewsResponse
import fr.fbernard.grpc.demo.news.v1.subscribeResponse
import fr.fbernard.newsapp.backend.data.NewsStore
import fr.fbernard.newsapp.backend.data.PUBLISHABLE_TOPICS
import io.grpc.Status
import io.grpc.StatusException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.map

class NewsService(private val store: NewsStore) : NewsServiceGrpcKt.NewsServiceCoroutineImplBase() {

    private val published = MutableSharedFlow<News>(extraBufferCapacity = 64)

    /** Number of active [subscribe] streams. */
    val subscriberCount: StateFlow<Int> get() = published.subscriptionCount

    override suspend fun getNews(request: GetNewsRequest): GetNewsResponse =
        getNewsResponse { news += store.byTopic(request.topic) }

    override fun subscribe(request: SubscribeRequest): Flow<SubscribeResponse> =
        published
            .filter { request.topic == Topic.TOPIC_UNSPECIFIED || it.topic == request.topic }
            .map { subscribeResponse { news = it } }

    override suspend fun postNews(request: PostNewsRequest): PostNewsResponse {
        if (request.title.isBlank()) invalidArgument("title must not be blank")
        if (request.topic !in PUBLISHABLE_TOPICS) invalidArgument("topic must be one of $PUBLISHABLE_TOPICS")

        val saved = store.add(
            news {
                title = request.title
                description = request.description
                imageUrl = request.imageUrl
                topic = request.topic
            },
        )
        published.emit(saved)
        return postNewsResponse { news = saved }
    }

    private fun invalidArgument(description: String): Nothing =
        throw StatusException(Status.INVALID_ARGUMENT.withDescription(description))
}

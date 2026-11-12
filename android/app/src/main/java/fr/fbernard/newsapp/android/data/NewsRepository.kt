package fr.fbernard.newsapp.android.data

import fr.fbernard.grpc.demo.news.v1.News
import fr.fbernard.grpc.demo.news.v1.NewsServiceGrpcKt.NewsServiceCoroutineStub
import fr.fbernard.grpc.demo.news.v1.Topic
import fr.fbernard.grpc.demo.news.v1.getNewsRequest
import fr.fbernard.grpc.demo.news.v1.postNewsRequest
import fr.fbernard.grpc.demo.news.v1.subscribeRequest
import fr.fbernard.newsapp.android.BuildConfig
import io.grpc.ManagedChannel
import io.grpc.Status
import io.grpc.okhttp.OkHttpChannelBuilder
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.Duration
import java.util.concurrent.TimeUnit

/** Topics a news item can be published in. */
val PUBLISHABLE_TOPICS = listOf(Topic.TOPIC_TECH, Topic.TOPIC_SPORT, Topic.TOPIC_ECONOMY)

interface NewsRepository {
    /** News of [topic], or of every topic for [Topic.TOPIC_UNSPECIFIED]. */
    suspend fun getNews(topic: Topic): List<News>

    /** News published from now on in [topic] (every topic for [Topic.TOPIC_UNSPECIFIED]). */
    fun subscribe(topic: Topic): Flow<News>

    suspend fun postNews(title: String, description: String, imageUrl: String, topic: Topic): News

    /** Makes the next calls try to connect right away, instead of waiting for the reconnect backoff. */
    fun reconnect() {}
}

/** [NewsRepository] backed by the gRPC NewsService, through grpc-kotlin coroutine stubs. */
class GrpcNewsRepository(private val channel: ManagedChannel) : NewsRepository {

    private val stub = NewsServiceCoroutineStub(channel)

    override suspend fun getNews(topic: Topic): List<News> =
        stub.withDeadlineAfter(CALL_TIMEOUT).getNews(getNewsRequest { this.topic = topic }).newsList

    override fun subscribe(topic: Topic): Flow<News> =
        stub.subscribe(subscribeRequest { this.topic = topic }).map { it.news }

    override suspend fun postNews(title: String, description: String, imageUrl: String, topic: Topic): News =
        stub.withDeadlineAfter(CALL_TIMEOUT).postNews(
            postNewsRequest {
                this.title = title
                this.description = description
                this.imageUrl = imageUrl
                this.topic = topic
            },
        ).news

    override fun reconnect() = channel.resetConnectBackoff()

    companion object {
        private val CALL_TIMEOUT = Duration.ofSeconds(10)

        /** Plaintext channel to the server configured in BuildConfig (the host machine, from the emulator). */
        val channel: ManagedChannel by lazy {
            OkHttpChannelBuilder.forAddress(BuildConfig.GRPC_HOST, BuildConfig.GRPC_PORT)
                .usePlaintext()
                // While a call is open, ping the server: a Subscribe stream only reads, and a connection
                // dropped by a NAT or a lost network would otherwise look alive forever.
                .keepAliveTime(20, TimeUnit.SECONDS)
                .keepAliveTimeout(5, TimeUnit.SECONDS)
                .build()
        }
    }
}

/** A readable message for a failed call: the server's description, or the reason the server was not reached. */
fun Throwable.grpcMessage(): String {
    val status = Status.fromThrowable(this)
    return when (status.code) {
        Status.Code.UNAVAILABLE -> "the server is unreachable"
        Status.Code.DEADLINE_EXCEEDED -> "the server did not answer in time"
        else -> status.description ?: status.code.name
    }
}

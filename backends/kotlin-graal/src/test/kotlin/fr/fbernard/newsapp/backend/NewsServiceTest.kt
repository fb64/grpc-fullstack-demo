package fr.fbernard.newsapp.backend

import fr.fbernard.grpc.demo.news.v1.NewsServiceGrpc
import fr.fbernard.grpc.demo.news.v1.NewsServiceGrpcKt.NewsServiceCoroutineStub
import fr.fbernard.grpc.demo.news.v1.Topic
import fr.fbernard.grpc.demo.news.v1.getNewsRequest
import fr.fbernard.grpc.demo.news.v1.postNewsRequest
import fr.fbernard.grpc.demo.news.v1.subscribeRequest
import fr.fbernard.newsapp.backend.data.NewsStore
import io.grpc.ManagedChannel
import io.grpc.Server
import io.grpc.Status
import io.grpc.StatusException
import io.grpc.health.v1.HealthCheckRequest
import io.grpc.health.v1.HealthCheckResponse.ServingStatus
import io.grpc.health.v1.HealthGrpc
import io.grpc.inprocess.InProcessChannelBuilder
import io.grpc.inprocess.InProcessServerBuilder
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withTimeout
import kotlin.test.AfterTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue

class NewsServiceTest {

    private val serverName = InProcessServerBuilder.generateName()
    private val service = NewsService(NewsStore(seedPerTopic = 3))
    private val server: Server = InProcessServerBuilder.forName(serverName)
        .addService(service)
        .build()
        .start()
    private val channel: ManagedChannel = InProcessChannelBuilder.forName(serverName).build()
    private val stub = NewsServiceCoroutineStub(channel)

    @AfterTest
    fun tearDown() {
        channel.shutdownNow()
        server.shutdownNow()
    }

    @Test
    fun `getNews returns the news of the requested topic`() = runBlocking {
        val response = stub.getNews(getNewsRequest { topic = Topic.TOPIC_SPORT })

        assertEquals(listOf("sport-0", "sport-1", "sport-2"), response.newsList.map { it.id })
        assertTrue(response.newsList.all { it.topic == Topic.TOPIC_SPORT })
    }

    @Test
    fun `getNews returns every topic when the topic is unspecified`() = runBlocking {
        val response = stub.getNews(getNewsRequest {})

        assertEquals(9, response.newsCount)
    }

    @Test
    fun `postNews assigns an id and stores the news`() = runBlocking {
        val posted = stub.postNews(
            postNewsRequest {
                title = "New phone"
                description = "It folds"
                topic = Topic.TOPIC_TECH
            },
        ).news

        assertEquals("tech-3", posted.id)
        assertEquals("New phone", posted.title)
        val stored = stub.getNews(getNewsRequest { topic = Topic.TOPIC_TECH }).newsList
        assertEquals(posted, stored.last())
    }

    @Test
    fun `postNews rejects news without a topic or a title`() = runBlocking {
        val noTopic = assertFailsWith<StatusException> {
            stub.postNews(postNewsRequest { title = "Untitled topic" })
        }
        val noTitle = assertFailsWith<StatusException> {
            stub.postNews(postNewsRequest { topic = Topic.TOPIC_TECH })
        }

        assertEquals(Status.Code.INVALID_ARGUMENT, noTopic.status.code)
        assertEquals(Status.Code.INVALID_ARGUMENT, noTitle.status.code)
    }

    @Test
    fun `subscribe streams only news posted for the subscribed topic`() = runBlocking {
        withTimeout(5_000) {
            val received = async {
                stub.subscribe(subscribeRequest { topic = Topic.TOPIC_ECONOMY })
                    .map { it.news.title }
                    .take(2)
                    .toList()
            }
            service.subscriberCount.first { it == 1 }

            stub.postNews(postNewsRequest { title = "Markets up"; topic = Topic.TOPIC_ECONOMY })
            stub.postNews(postNewsRequest { title = "Match won"; topic = Topic.TOPIC_SPORT })
            stub.postNews(postNewsRequest { title = "Rates down"; topic = Topic.TOPIC_ECONOMY })

            assertEquals(listOf("Markets up", "Rates down"), received.await())
        }
    }

    @Test
    fun `newsServer reports the news service as serving`() {
        val name = InProcessServerBuilder.generateName()
        val fullServer = newsServer(InProcessServerBuilder.forName(name)).start()
        val healthChannel = InProcessChannelBuilder.forName(name).build()
        try {
            val response = HealthGrpc.newBlockingStub(healthChannel)
                .check(HealthCheckRequest.newBuilder().setService(NewsServiceGrpc.SERVICE_NAME).build())

            assertEquals(ServingStatus.SERVING, response.status)
        } finally {
            healthChannel.shutdownNow()
            fullServer.shutdownNow()
        }
    }
}

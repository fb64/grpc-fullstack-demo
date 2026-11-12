package fr.fbernard.newsapp.android

import fr.fbernard.grpc.demo.news.v1.News
import fr.fbernard.grpc.demo.news.v1.Topic
import fr.fbernard.grpc.demo.news.v1.news
import fr.fbernard.newsapp.android.data.NewsRepository
import io.grpc.Status
import io.grpc.StatusException
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class NewsViewModelTest {

    private val repository = FakeNewsRepository()

    @Before
    fun setUp() = Dispatchers.setMain(UnconfinedTestDispatcher())

    @After
    fun tearDown() = Dispatchers.resetMain()

    @Test
    fun `starts with the news of every topic`() {
        val viewModel = NewsViewModel(repository)

        val state = viewModel.state.value
        assertEquals(Topic.TOPIC_UNSPECIFIED, state.topic)
        assertEquals(listOf("tech-0", "sport-0"), state.news.map { it.id })
        assertEquals(false, state.loading)
        assertNull(state.error)
    }

    @Test
    fun `selecting a topic shows its news and follows only that topic`() = runTest {
        val viewModel = NewsViewModel(repository)
        val breaking = mutableListOf<News>()
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) { viewModel.breakingNews.collect(breaking::add) }

        viewModel.selectTopic(Topic.TOPIC_SPORT)
        repository.publish(newsItem("tech-1", Topic.TOPIC_TECH))
        repository.publish(newsItem("sport-1", Topic.TOPIC_SPORT))

        assertEquals(listOf("sport-0", "sport-1"), viewModel.state.value.news.map { it.id })
        assertEquals(listOf("sport-1"), breaking.map { it.id })
        assertEquals(1, repository.published.subscriptionCount.value) // the "All" stream was cancelled
    }

    @Test
    fun `news streamed while GetNews runs are kept once`() = runTest {
        val gate = CompletableDeferred<Unit>()
        repository.getNewsGate = gate
        val viewModel = NewsViewModel(repository)

        repository.publish(newsItem("tech-1", Topic.TOPIC_TECH)) // arrives before GetNews answers
        repository.stored += newsItem("tech-1", Topic.TOPIC_TECH) // and is also part of its answer
        repository.publish(newsItem("sport-1", Topic.TOPIC_SPORT))
        gate.complete(Unit)

        assertEquals(listOf("tech-0", "sport-0", "tech-1", "sport-1"), viewModel.state.value.news.map { it.id })
    }

    @Test
    fun `a failed load is reported and retry recovers`() {
        repository.getNewsError = StatusException(Status.UNAVAILABLE.withDescription("server down"))
        val viewModel = NewsViewModel(repository)

        assertEquals("Could not load the news: the server is unreachable", viewModel.state.value.error)
        assertTrue(viewModel.state.value.news.isEmpty())

        repository.getNewsError = null
        viewModel.retry()

        assertTrue(repository.reconnected)
        assertNull(viewModel.state.value.error)
        assertEquals(2, viewModel.state.value.news.size)
    }

    @Test
    fun `post returns null on success and the server message on failure`() = runTest {
        val viewModel = NewsViewModel(repository)

        assertNull(viewModel.post("Hello", "From Android", Topic.TOPIC_TECH))
        assertEquals("Hello", viewModel.state.value.news.last().title) // received through the stream
        assertEquals("title must not be blank", viewModel.post(" ", "", Topic.TOPIC_TECH))
    }

    private fun newsItem(id: String, topic: Topic) = news {
        this.id = id
        this.title = id
        this.topic = topic
    }

    private class FakeNewsRepository : NewsRepository {
        val stored = mutableListOf(
            news { id = "tech-0"; title = "tech-0"; topic = Topic.TOPIC_TECH },
            news { id = "sport-0"; title = "sport-0"; topic = Topic.TOPIC_SPORT },
        )
        val published = MutableSharedFlow<News>()
        var getNewsGate: CompletableDeferred<Unit>? = null
        var getNewsError: Exception? = null
        var reconnected = false

        suspend fun publish(news: News) = published.emit(news)

        override suspend fun getNews(topic: Topic): List<News> {
            getNewsGate?.await()
            getNewsError?.let { throw it }
            return stored.filter { topic == Topic.TOPIC_UNSPECIFIED || it.topic == topic }
        }

        override fun subscribe(topic: Topic): Flow<News> =
            published.filter { topic == Topic.TOPIC_UNSPECIFIED || it.topic == topic }

        override fun reconnect() {
            reconnected = true
        }

        override suspend fun postNews(title: String, description: String, imageUrl: String, topic: Topic): News {
            if (title.isBlank()) throw StatusException(Status.INVALID_ARGUMENT.withDescription("title must not be blank"))
            val created = news {
                this.id = "${topic.name}-${stored.size}"
                this.title = title
                this.description = description
                this.imageUrl = imageUrl
                this.topic = topic
            }
            stored += created
            publish(created)
            return created
        }
    }
}

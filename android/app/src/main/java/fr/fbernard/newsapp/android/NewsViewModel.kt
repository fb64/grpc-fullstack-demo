package fr.fbernard.newsapp.android

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import fr.fbernard.grpc.demo.news.v1.News
import fr.fbernard.grpc.demo.news.v1.Topic
import fr.fbernard.newsapp.android.data.GrpcNewsRepository
import fr.fbernard.newsapp.android.data.NewsRepository
import fr.fbernard.newsapp.android.data.grpcMessage
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.UUID

data class NewsUiState(
    val topic: Topic = Topic.TOPIC_UNSPECIFIED,
    val news: List<News> = emptyList(),
    val loading: Boolean = true,
    val error: String? = null,
)

class NewsViewModel(private val repository: NewsRepository) : ViewModel() {

    private val _state = MutableStateFlow(NewsUiState())
    val state: StateFlow<NewsUiState> = _state.asStateFlow()

    private val _breakingNews = Channel<News>(Channel.BUFFERED)

    /** News received live from the Subscribe stream, each delivered once. */
    val breakingNews: Flow<News> = _breakingNews.receiveAsFlow()

    private var topicJob: Job? = null

    init {
        selectTopic(Topic.TOPIC_UNSPECIFIED)
    }

    /** Shows the news of [topic] and follows the new ones, replacing the previous topic's stream. */
    fun selectTopic(topic: Topic) {
        topicJob?.cancel()
        _state.value = NewsUiState(topic = topic)
        topicJob = viewModelScope.launch {
            // Subscribe first, so nothing published while GetNews runs is missed.
            launch { follow(topic) }
            try {
                val loaded = repository.getNews(topic)
                val loadedIds = loaded.mapTo(HashSet()) { it.id }
                _state.update { state ->
                    state.copy(news = loaded + state.news.filterNot { it.id in loadedIds }, loading = false)
                }
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                Log.w(TAG, "GetNews failed", e)
                _state.update { it.copy(loading = false, error = "Could not load the news: ${e.grpcMessage()}") }
            }
        }
    }

    fun retry() {
        repository.reconnect()
        selectTopic(_state.value.topic)
    }

    /** Publishes a news item; returns an error message, or null on success. */
    suspend fun post(title: String, description: String, topic: Topic): String? =
        try {
            repository.postNews(title, description, "https://picsum.photos/seed/${UUID.randomUUID()}/400/400", topic)
            null
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Log.w(TAG, "PostNews failed", e)
            e.grpcMessage()
        }

    private suspend fun follow(topic: Topic) {
        try {
            repository.subscribe(topic).collect { news ->
                _state.update { it.copy(news = it.news + news) }
                _breakingNews.send(news)
            }
            _state.update { it.copy(error = "Live updates ended") }
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Log.w(TAG, "Subscribe failed", e)
            _state.update { it.copy(error = "Live updates stopped: ${e.grpcMessage()}") }
        }
    }

    companion object {
        private const val TAG = "NewsViewModel"

        val Factory = viewModelFactory {
            initializer { NewsViewModel(GrpcNewsRepository(GrpcNewsRepository.channel)) }
        }
    }
}

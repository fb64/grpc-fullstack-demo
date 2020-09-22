package fr.fbernard.newsapp.backend

import fr.fbernard.grpc.news.*
import fr.fbernard.newsapp.backend.data.NewsStore
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.channels.BroadcastChannel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.filter

class NewsService : NewsServiceGrpcKt.NewsServiceCoroutineImplBase() {

    private val addNewEvent = BroadcastChannel<News>(Channel.BUFFERED)

    override suspend fun getNews(request: NewsRequest): NewsResponse {
        val news = NewsStore.getByTopic(request.topic)
        return NewsResponse.newBuilder().addAllNews(news).build()
    }

    @FlowPreview
    override fun subscribe(request: SubscribeRequest): Flow<News> {
        return addNewEvent.asFlow().filter { it.topic == request.topic }
    }

    override suspend fun postNews(request: News): News {
        return NewsStore.saveNews(request)?.let {saved->
            //addNewEvent.onNext(saved)
            addNewEvent.send(saved)
            saved
        } ?: throw IllegalStateException("Save news failed")
    }
}
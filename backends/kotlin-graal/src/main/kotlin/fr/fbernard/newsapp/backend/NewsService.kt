package fr.fbernard.newsapp.backend

import fr.fbernard.grpc.news.News
import fr.fbernard.grpc.news.NewsRequest
import fr.fbernard.grpc.news.NewsResponse
import fr.fbernard.grpc.news.NewsServiceGrpcKt
import fr.fbernard.grpc.news.SubscribeRequest
import fr.fbernard.newsapp.backend.data.NewsStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.filter

class NewsService : NewsServiceGrpcKt.NewsServiceCoroutineImplBase() {

    private val addNewEvent = MutableSharedFlow<News>()

    override suspend fun getNews(request: NewsRequest): NewsResponse {
        val news = NewsStore.getByTopic(request.topic)
        return NewsResponse.newBuilder().addAllNews(news).build()
    }

    //@FlowPreview
    override fun subscribe(request: SubscribeRequest): Flow<News> {
        return addNewEvent.filter { it.topic == request.topic }
    }

    override suspend fun postNews(request: News): News {
        return NewsStore.saveNews(request)?.let {saved->
            addNewEvent.emit(saved)
            saved
        } ?: throw IllegalStateException("Save news failed")
    }
}
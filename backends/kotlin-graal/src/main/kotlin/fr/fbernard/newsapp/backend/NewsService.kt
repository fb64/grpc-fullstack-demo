package fr.fbernard.newsapp.backend

import fr.fbernard.grpc.news.*
import fr.fbernard.newsapp.backend.data.NewsStore
import io.reactivex.BackpressureStrategy
import io.reactivex.Flowable
import io.reactivex.Single
import io.reactivex.subjects.PublishSubject

class NewsService : RxNewsServiceGrpc.NewsServiceImplBase() {

    val addNewEvent = PublishSubject.create<News>()

    override fun getNews(request: Single<NewsRequest>?): Single<NewsResponse> {
        return request?.map {
            val news = NewsStore.getByTopic(it.topic)
            NewsResponse.newBuilder().addAllNews(news).build()
        } ?: Single.just(NewsResponse.newBuilder().build())
    }

    override fun subscribe(request: Single<SubscribeRequest>?): Flowable<News> {
        return request?.toFlowable()?.flatMap { req->
            addNewEvent.toFlowable(BackpressureStrategy.BUFFER).share().filter { it.topic == req.topic }

        } ?: Flowable.empty<News>()

    }

    override fun postNews(request: Single<News>?): Single<News> {
        return request?.map {
            NewsStore.saveNews(it)?.let {saved->
                addNewEvent.onNext(saved)
                saved
            } ?: throw IllegalStateException("Save news failed")

        } ?: Single.error<News>(NullPointerException())
    }
}
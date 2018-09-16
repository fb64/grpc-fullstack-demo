package fr.fbernard.newsapp.backend.service

import fr.fbernard.grpc.news.*
import fr.fbernard.newsapp.backend.data.NewsStore
import io.reactivex.BackpressureStrategy
import io.reactivex.Flowable
import io.reactivex.Single
import io.reactivex.subjects.PublishSubject
import org.lognet.springboot.grpc.GRpcService

@GRpcService
class NewsGrpcService(val newsStore: NewsStore) : RxNewsServiceGrpc.NewsServiceImplBase() {

    val addNewEvent = PublishSubject.create<News>()

    override fun getNews(request: Single<NewsRequest>?): Single<NewsResponse> {
        return request?.map {
            val news = newsStore.getByTopic(it.topic)
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
            newsStore.saveNews(it)?.let {saved->
                addNewEvent.onNext(saved)
                saved
            } ?: throw IllegalStateException("Save news failed")

        } ?: Single.error<News>(NullPointerException())
    }

}
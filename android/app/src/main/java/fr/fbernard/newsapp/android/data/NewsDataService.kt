package fr.fbernard.newsapp.android.data

import fr.fbernard.grpc.news.*
import fr.fbernard.newsapp.android.BuildConfig
import io.grpc.ManagedChannelBuilder
import io.reactivex.Flowable
import io.reactivex.Single

object NewsDataService {

    private val newsGrpcService =  RxNewsServiceGrpc.newRxStub(
            ManagedChannelBuilder.forAddress(BuildConfig.GRPC_NEWS_HOST,BuildConfig.GRPC_NEWS_PORT)
            .usePlaintext()
            .build())

    fun getNews(topic: Topic): Single<NewsResponse> {
        return newsGrpcService.getNews(NewsRequest.newBuilder().setTopic(topic).build())
    }

    fun subscribe(topic: Topic): Flowable<News> {
        return newsGrpcService.subscribe(SubscribeRequest.newBuilder().setTopic(topic).build())
    }

    fun postNews(news: News):Single<News>{
        return newsGrpcService.postNews(news)
    }




}
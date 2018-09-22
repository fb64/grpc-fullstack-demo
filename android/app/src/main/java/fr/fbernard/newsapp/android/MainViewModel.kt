package fr.fbernard.newsapp.android

import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import fr.fbernard.grpc.news.News
import fr.fbernard.grpc.news.Topic
import fr.fbernard.newsapp.android.data.NewsDataService
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers

class MainViewModel : ViewModel() {

    val news = MutableLiveData<List<News>>()
    val addNewsEvent = MutableLiveData<News>()
    val error = MutableLiveData<String>()
    private val disposableList = CompositeDisposable()
    private var subscriptionDisposable : Disposable? = null


    fun getNews(topic: Topic){
        val disposable = NewsDataService.getNews(topic)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    news.postValue(it.newsList)
                },{
                    error.postValue(it.localizedMessage)
                })

        disposableList.add(disposable)
    }


    fun subscribe(topic: Topic){
        subscriptionDisposable?.dispose()
        subscriptionDisposable = NewsDataService.subscribe(topic)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    addNewsEvent.postValue(it)
                },{
                    error.postValue(it.localizedMessage)
                })
    }


    override fun onCleared() {
        super.onCleared()
        disposableList.dispose()
        subscriptionDisposable?.dispose()
    }
}
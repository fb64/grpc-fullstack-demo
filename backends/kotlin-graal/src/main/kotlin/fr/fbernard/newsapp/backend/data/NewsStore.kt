package fr.fbernard.newsapp.backend.data

import fr.fbernard.grpc.news.News
import fr.fbernard.grpc.news.Topic
import java.util.concurrent.ConcurrentHashMap

object NewsStore {
    private val data = ConcurrentHashMap<Topic,MutableList<News>>()

    //Populate 10 news by topic
    init {
        Topic.values().filter { it !=Topic.UNRECOGNIZED }.forEach {
            val newsList = mutableListOf<News>()
            for( i in 0..9){
                val news = News.newBuilder().setTopic(it)
                    .setNewsId("${it.name}-$i")
                    .setTitle("${it.name} Title $i")
                    .setDescription("${it.name.toLowerCase()} description $i")
                    .setImageUrl("https://picsum.photos/400/400/?${it.name}-$i").build()
                newsList.add(news)
            }
            data[it] = newsList
        }
    }


    fun saveNews(news: News):News?{
        return data[news.topic]?.let {
            val id = "${news.topic.name}-${it.size+1}"
            val toAdd = news.toBuilder().setNewsId(id).build()
            it.add(toAdd)
            toAdd
        }
    }

    fun getByTopic(topic: Topic):List<News>{
        return data[topic]?.toList() ?: emptyList()
    }
}
package fr.fbernard.newsapp.android

import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import fr.fbernard.grpc.news.News
import kotlinx.android.synthetic.main.item_news.view.*


class NewsAdapter : RecyclerView.Adapter<NewsAdapter.NewsViewHolder>() {

    private val newsList = mutableListOf<News>()

    override fun onCreateViewHolder(parent: ViewGroup, position: Int): NewsViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_news,parent,false)
        return NewsViewHolder(view)
    }

    override fun getItemCount() = newsList.size

    override fun onBindViewHolder(viewHolder: NewsViewHolder, position: Int) {
        viewHolder.bind(newsList[position])
    }

    fun updateNews(news:List<News>){
        newsList.clear()
        newsList.addAll(news)
        notifyDataSetChanged()
    }


    fun addNews(news:News){
        newsList.add(news)
        notifyDataSetChanged()
    }


    inner class NewsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        fun bind(news:News){
            itemView.newsTitle.text = news.title
            itemView.newsDescription.text = news.description
            Glide.with(itemView).load(news.imageUrl).into(itemView.newsImage)
        }
    }
}
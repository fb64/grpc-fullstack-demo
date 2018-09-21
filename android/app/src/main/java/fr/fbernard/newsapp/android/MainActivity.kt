package fr.fbernard.newsapp.android
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.design.widget.BottomNavigationView
import android.support.v7.app.AppCompatActivity
import android.util.Log
import fr.fbernard.grpc.news.Topic
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {



    private val mainViewModel : MainViewModel by lazy {
        ViewModelProviders.of(this).get(MainViewModel::class.java)
    }

    private val newsAdapter = NewsAdapter()

    private val mOnNavigationItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener { item ->
        when (item.itemId) {
            R.id.navigation_sport -> {
                mainTitle.setText(R.string.title_sport)
                mainViewModel.getNews(Topic.SPORT)
                true
            }
            R.id.navigation_tech -> {
                mainTitle.setText(R.string.title_tech)
                mainViewModel.getNews(Topic.TECH)
                true
            }
            R.id.navigation_economy -> {
                mainTitle.setText(R.string.title_economy)
                mainViewModel.getNews(Topic.ECONOMY)
                 true
            }
            else->false
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener)

        newsList.adapter = newsAdapter
        mainViewModel.news.observe(this, Observer {newsList->
            newsList?.let {
                newsAdapter.updateNews(it)
            }
        })

        mainViewModel.getNews(Topic.TECH)
    }
}

package fr.fbernard.newsapp.android
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.design.widget.BottomNavigationView
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import fr.fbernard.grpc.news.Topic
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {



    private val mainViewModel : MainViewModel by lazy {
        ViewModelProviders.of(this).get(MainViewModel::class.java)
    }

    private val newsAdapter = NewsAdapter()
    private lateinit var layoutManager : LinearLayoutManager

    private val mOnNavigationItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener { item ->
        when (item.itemId) {
            R.id.navigation_sport -> {
                mainTitle.setText(R.string.title_sport)
                updateTopic(Topic.SPORT)
                true
            }
            R.id.navigation_tech -> {
                mainTitle.setText(R.string.title_tech)
                updateTopic(Topic.TECH)
                true
            }
            R.id.navigation_economy -> {
                mainTitle.setText(R.string.title_economy)
                updateTopic(Topic.ECONOMY)
                 true
            }
            else->false
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener)

        layoutManager = LinearLayoutManager(this)

        newsList.adapter = newsAdapter
        newsList.layoutManager = layoutManager

        mainViewModel.news.observe(this, Observer {newsList->
            newsList?.let {
                //update adapters item
                newsAdapter.updateNews(it)
                //reset scroll
                layoutManager.scrollToPosition(0)
            }
        })

        mainViewModel.addNewsEvent.observe(this, Observer {news->
           news?.let {
               newsAdapter.addNews(it)
                showSnackBar()
           }
        })

        updateTopic(Topic.TECH)

    }


    private fun updateTopic(topic: Topic){
        mainViewModel.getNews(topic)
        mainViewModel.subscribe(topic)
    }

    private fun showSnackBar(){
        Snackbar.make(newsList,"Breaking News !",Snackbar.LENGTH_LONG)
                .setAction("show") {
                    layoutManager.scrollToPosition(newsAdapter.itemCount - 1)
                }.show()
    }

}

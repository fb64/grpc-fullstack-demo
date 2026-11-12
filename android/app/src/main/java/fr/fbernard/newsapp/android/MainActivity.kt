package fr.fbernard.newsapp.android

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.lifecycle.viewmodel.compose.viewModel
import fr.fbernard.newsapp.android.ui.NewsScreen
import fr.fbernard.newsapp.android.ui.NewsTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            NewsTheme {
                NewsScreen(viewModel(factory = NewsViewModel.Factory))
            }
        }
    }
}

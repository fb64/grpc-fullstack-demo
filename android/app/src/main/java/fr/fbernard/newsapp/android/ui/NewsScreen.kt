package fr.fbernard.newsapp.android.ui

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.DrawableRes
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.AsyncImage
import fr.fbernard.grpc.demo.news.v1.News
import fr.fbernard.grpc.demo.news.v1.Topic
import fr.fbernard.newsapp.android.NewsViewModel
import fr.fbernard.newsapp.android.R
import fr.fbernard.newsapp.android.data.PUBLISHABLE_TOPICS
import kotlinx.coroutines.flow.collectLatest

private data class TopicTab(val topic: Topic, val label: String, @param:DrawableRes val icon: Int)

private val TABS = listOf(
    TopicTab(Topic.TOPIC_UNSPECIFIED, "All", R.drawable.ic_all),
    TopicTab(Topic.TOPIC_TECH, "Tech", R.drawable.ic_tech),
    TopicTab(Topic.TOPIC_SPORT, "Sport", R.drawable.ic_sport),
    TopicTab(Topic.TOPIC_ECONOMY, "Economy", R.drawable.ic_money),
)

fun Topic.label(): String = name.removePrefix("TOPIC_").lowercase().replaceFirstChar { it.uppercase() }

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewsScreen(viewModel: NewsViewModel) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    val listState = rememberLazyListState()
    var publishing by rememberSaveable { mutableStateOf(false) }
    val localNetworkDenied = rememberLocalNetworkAccess(onGranted = viewModel::retry)

    // Each news received live shows a snackbar; a newer one replaces it.
    LaunchedEffect(viewModel) {
        viewModel.breakingNews.collectLatest { news ->
            val result = snackbarHostState.showSnackbar(
                message = "Breaking news: ${news.title}",
                actionLabel = "Show",
                duration = SnackbarDuration.Long,
            )
            if (result == SnackbarResult.ActionPerformed) {
                val index = viewModel.state.value.news.indexOfFirst { it.id == news.id }
                if (index >= 0) listState.animateScrollToItem(index)
            }
        }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text(if (state.topic == Topic.TOPIC_UNSPECIFIED) "All news" else "${state.topic.label()} news") },
            )
        },
        bottomBar = {
            NavigationBar {
                TABS.forEach { tab ->
                    NavigationBarItem(
                        selected = state.topic == tab.topic,
                        onClick = { viewModel.selectTopic(tab.topic) },
                        icon = { Icon(painterResource(tab.icon), contentDescription = null) },
                        label = { Text(tab.label) },
                    )
                }
            }
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { publishing = true }) {
                Icon(painterResource(R.drawable.ic_add), contentDescription = "Publish a news item")
            }
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
    ) { padding ->
        Column(Modifier.fillMaxSize().padding(padding)) {
            // Errors stay above the list: inserted as list items, they would land above a scrolled viewport.
            Column(Modifier.padding(horizontal = 16.dp).padding(top = 8.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                if (localNetworkDenied) {
                    ErrorCard("Allow local network access in the app settings to reach the demo server.", onRetry = null)
                }
                state.error?.let { ErrorCard(it, onRetry = viewModel::retry) }
            }
            Box(Modifier.weight(1f)) {
                LazyColumn(
                    state = listState,
                    // Extra bottom space so the floating button never hides the last news.
                    contentPadding = PaddingValues(start = 16.dp, top = 8.dp, end = 16.dp, bottom = 96.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    items(state.news, key = { it.id }) { NewsCard(it) }
                }
                if (state.loading && state.news.isEmpty()) {
                    CircularProgressIndicator(Modifier.align(Alignment.Center))
                }
            }
        }
    }

    if (publishing) {
        PostNewsSheet(
            initialTopic = state.topic.takeIf { it in PUBLISHABLE_TOPICS } ?: Topic.TOPIC_TECH,
            onPost = viewModel::post,
            onDismiss = { publishing = false },
        )
    }
}

@Composable
private fun NewsCard(news: News) {
    Card(Modifier.fillMaxWidth()) {
        Row(Modifier.padding(12.dp), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            AsyncImage(
                model = news.imageUrl,
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(88.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant),
            )
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(news.title, style = MaterialTheme.typography.titleMedium)
                if (news.description.isNotEmpty()) Text(news.description, style = MaterialTheme.typography.bodyMedium)
                Text(
                    news.topic.label(),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.primary,
                )
            }
        }
    }
}

/**
 * Since Android 17 (API 37), connecting to a local network address such as the emulator's host (10.0.2.2)
 * needs the ACCESS_LOCAL_NETWORK runtime permission: asks for it once, and returns true if it was refused.
 */
@Composable
private fun rememberLocalNetworkAccess(onGranted: () -> Unit): Boolean {
    if (Build.VERSION.SDK_INT < 37) return false
    val context = LocalContext.current
    var denied by rememberSaveable { mutableStateOf(false) }
    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
        denied = !granted
        if (granted) onGranted()
    }
    LaunchedEffect(Unit) {
        if (context.checkSelfPermission(Manifest.permission.ACCESS_LOCAL_NETWORK) != PackageManager.PERMISSION_GRANTED) {
            launcher.launch(Manifest.permission.ACCESS_LOCAL_NETWORK)
        }
    }
    return denied
}

@Composable
private fun ErrorCard(message: String, onRetry: (() -> Unit)?) {
    Card(
        Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer),
    ) {
        Row(
            Modifier.padding(start = 16.dp, end = 8.dp, top = 8.dp, bottom = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                message,
                Modifier.weight(1f),
                color = MaterialTheme.colorScheme.onErrorContainer,
                style = MaterialTheme.typography.bodyMedium,
            )
            if (onRetry != null) TextButton(onClick = onRetry) { Text("Retry") }
        }
    }
}

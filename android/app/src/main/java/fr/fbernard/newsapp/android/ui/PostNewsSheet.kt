package fr.fbernard.newsapp.android.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import fr.fbernard.grpc.demo.news.v1.Topic
import fr.fbernard.newsapp.android.data.PUBLISHABLE_TOPICS
import kotlinx.coroutines.launch

/** Bottom sheet to publish a news item with PostNews. [onPost] returns an error message, or null on success. */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PostNewsSheet(
    initialTopic: Topic,
    onPost: suspend (title: String, description: String, topic: Topic) -> String?,
    onDismiss: () -> Unit,
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val scope = rememberCoroutineScope()
    var title by rememberSaveable { mutableStateOf("") }
    var description by rememberSaveable { mutableStateOf("") }
    var topic by rememberSaveable { mutableStateOf(initialTopic) }
    var sending by rememberSaveable { mutableStateOf(false) }
    var error by rememberSaveable { mutableStateOf<String?>(null) }

    ModalBottomSheet(onDismissRequest = onDismiss, sheetState = sheetState) {
        Column(
            Modifier.padding(horizontal = 24.dp).padding(bottom = 24.dp).imePadding(),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Text("Publish a news item", style = MaterialTheme.typography.titleLarge)
            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                label = { Text("Title") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
            )
            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                label = { Text("Description") },
                minLines = 2,
                modifier = Modifier.fillMaxWidth(),
            )
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                PUBLISHABLE_TOPICS.forEach { value ->
                    FilterChip(
                        selected = topic == value,
                        onClick = { topic = value },
                        label = { Text(value.label()) },
                    )
                }
            }
            error?.let { Text(it, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall) }
            Button(
                onClick = {
                    scope.launch {
                        sending = true
                        error = onPost(title, description, topic)
                        sending = false
                        if (error == null) {
                            sheetState.hide()
                            onDismiss()
                        }
                    }
                },
                enabled = title.isNotBlank() && !sending,
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text(if (sending) "Publishing…" else "Publish")
            }
        }
    }
}

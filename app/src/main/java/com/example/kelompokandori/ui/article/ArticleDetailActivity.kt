package com.example.kelompokandori.ui.article

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.kelompokandori.SupabaseClient
import com.example.kelompokandori.model.ArticleComment
import com.example.kelompokandori.ui.theme.KelompokAndoriTheme
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.query.Order
import kotlinx.coroutines.launch
import kotlinx.serialization.json.jsonPrimitive

class ArticleDetailActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val articleId = intent.getLongExtra("ARTICLE_ID", -1L)
        val title = intent.getStringExtra("ARTICLE_TITLE") ?: ""
        val content = intent.getStringExtra("ARTICLE_CONTENT") ?: ""
        val imageUrl = intent.getStringExtra("ARTICLE_IMAGE")

        setContent {
            KelompokAndoriTheme {
                ArticleDetailScreen(articleId, title, content, imageUrl)
            }
        }
    }
}

@Composable
fun ArticleDetailScreen(articleId: Long, title: String, content: String, imageUrl: String?) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    var commentText by remember { mutableStateOf("") }
    var commentsList by remember { mutableStateOf<List<ArticleComment>>(emptyList()) }
    var isLoadingComments by remember { mutableStateOf(false) }

    fun fetchComments() {
        if (articleId == -1L) return
        scope.launch {
            isLoadingComments = true
            try {
                val results = SupabaseClient.client
                    .from("article_comments")
                    .select {
                        filter { eq("article_id", articleId) }
                        order("created_at", order = Order.DESCENDING)
                    }
                    .decodeList<ArticleComment>()
                commentsList = results
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                isLoadingComments = false
            }
        }
    }

    LaunchedEffect(Unit) {
        fetchComments()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        LazyColumn(
            modifier = Modifier.weight(1f),
            contentPadding = PaddingValues(bottom = 16.dp)
        ) {
            item {
                if (imageUrl != null) {
                    AsyncImage(
                        model = imageUrl,
                        contentDescription = "Cover Image",
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp)
                            .clip(RoundedCornerShape(12.dp)),
                        contentScale = ContentScale.Crop
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                }

                Text(text = title, style = MaterialTheme.typography.headlineLarge)
                Spacer(modifier = Modifier.height(8.dp))
                HorizontalDivider(modifier = Modifier.padding(vertical = 16.dp))
                Text(text = content, style = MaterialTheme.typography.bodyLarge)

                Spacer(modifier = Modifier.height(24.dp))
                Text(
                    text = "Komentar (${commentsList.size})",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(8.dp))
            }

            if (isLoadingComments) {
                item { CircularProgressIndicator(modifier = Modifier.padding(16.dp)) }
            } else if (commentsList.isEmpty()) {
                item { Text("Belum ada komentar.", color = Color.Gray) }
            } else {
                items(commentsList) { comment ->
                    CommentItem(comment)
                }
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = commentText,
                onValueChange = { commentText = it },
                placeholder = { Text("Tulis komentar...") },
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(24.dp)
            )

            Spacer(modifier = Modifier.width(8.dp))

            IconButton(
                onClick = {
                    if (commentText.isBlank()) return@IconButton

                    scope.launch {
                        try {
                            val user = SupabaseClient.client.auth.currentUserOrNull()
                            if (user == null) {
                                Toast.makeText(context, "Login dulu untuk komen", Toast.LENGTH_SHORT).show()
                                return@launch
                            }

                            val fullName = user.userMetadata?.get("full_name")?.jsonPrimitive?.content ?: "User"

                            val newComment = ArticleComment(
                                content = commentText,
                                articleId = articleId,
                                userId = user.id,
                                userName = fullName
                            )

                            SupabaseClient.client.from("article_comments").insert(newComment)

                            commentText = ""
                            fetchComments()

                        } catch (e: Exception) {
                            Toast.makeText(context, "Gagal: ${e.message}", Toast.LENGTH_SHORT).show()
                        }
                    }
                },
                enabled = commentText.isNotBlank()
            ) {
                Icon(Icons.AutoMirrored.Filled.Send, contentDescription = "Kirim", tint = MaterialTheme.colorScheme.primary)
            }
        }
    }
}

@Composable
fun CommentItem(comment: ArticleComment) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(
                text = comment.userName ?: "Anonymous",
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = comment.content,
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}
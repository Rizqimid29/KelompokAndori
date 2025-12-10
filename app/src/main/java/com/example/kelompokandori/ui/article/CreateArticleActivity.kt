package com.example.kelompokandori.ui.article

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.kelompokandori.SupabaseClient
import com.example.kelompokandori.model.Article
import com.example.kelompokandori.ui.theme.KelompokAndoriTheme
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.auth.auth
import kotlinx.coroutines.launch

class CreateArticleActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            KelompokAndoriTheme {
                CreateArticleScreen(onFinish = { finish() })
            }
        }
    }
}

@Composable
fun CreateArticleScreen(onFinish: () -> Unit) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    var title by remember { mutableStateOf("") }
    var content by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Bagikan Pengalamanmu",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(bottom = 24.dp)
        )

        OutlinedTextField(
            value = title,
            onValueChange = { title = it },
            label = { Text("Judul Artikel") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = content,
            onValueChange = { content = it },
            label = { Text("Ceritakan pengalamanmu...") },
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp),
            maxLines = 10
        )

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = {
                if (title.isBlank() || content.isBlank()) {
                    Toast.makeText(context, "Judul dan isi tidak boleh kosong", Toast.LENGTH_SHORT).show()
                    return@Button
                }

                scope.launch {
                    try {
                        isLoading = true

                        val currentUser = SupabaseClient.client.auth.currentUserOrNull()

                        if (currentUser == null) {
                            Toast.makeText(context, "Anda harus login terlebih dahulu", Toast.LENGTH_SHORT).show()
                            isLoading = false
                            return@launch
                        }

                        val newArticle = Article(
                            title = title,
                            content = content,
                            userId = currentUser.id
                        )

                        SupabaseClient.client.from("articles").insert(newArticle)

                        Toast.makeText(context, "Artikel berhasil diposting!", Toast.LENGTH_LONG).show()
                        onFinish()

                    } catch (e: Exception) {
                        e.printStackTrace()
                        Toast.makeText(context, "Gagal memposting: ${e.message}", Toast.LENGTH_LONG).show()
                    } finally {
                        isLoading = false
                    }
                }
            },
            enabled = !isLoading,
            modifier = Modifier.fillMaxWidth()
        ) {
            if (isLoading) {
                CircularProgressIndicator(
                    color = MaterialTheme.colorScheme.onPrimary,
                    modifier = Modifier.size(24.dp)
                )
            } else {
                Text("Posting Artikel")
            }
        }
    }
}
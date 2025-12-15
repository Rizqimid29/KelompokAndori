package com.example.kelompokandori.ui.article

import android.net.Uri
        import android.os.Bundle
        import android.widget.Toast
        import androidx.activity.ComponentActivity
        import androidx.activity.compose.rememberLauncherForActivityResult
        import androidx.activity.compose.setContent
        import androidx.activity.result.contract.ActivityResultContracts
        import androidx.compose.foundation.layout.*
        import androidx.compose.foundation.rememberScrollState
        import androidx.compose.foundation.verticalScroll
        import androidx.compose.material3.*
        import androidx.compose.runtime.*
        import androidx.compose.ui.Alignment
        import androidx.compose.ui.Modifier
        import androidx.compose.ui.layout.ContentScale
        import androidx.compose.ui.platform.LocalContext
        import androidx.compose.ui.unit.dp
        import coil.compose.AsyncImage
        import com.example.kelompokandori.SupabaseClient
        import com.example.kelompokandori.model.Article
        import com.example.kelompokandori.ui.theme.KelompokAndoriTheme
        import io.github.jan.supabase.postgrest.from
        import io.github.jan.supabase.auth.auth
        import io.github.jan.supabase.storage.storage
        import kotlinx.coroutines.launch
        import java.util.UUID

class CreateArticleActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val articleId = intent.getLongExtra("ARTICLE_ID", -1L)
        val initialTitle = intent.getStringExtra("ARTICLE_TITLE") ?: ""
        val initialContent = intent.getStringExtra("ARTICLE_CONTENT") ?: ""
        val initialImageUrl = intent.getStringExtra("ARTICLE_IMAGE")

        setContent {
            KelompokAndoriTheme {
                CreateArticleScreen(
                    onFinish = { finish() },
                    articleId = if (articleId == -1L) null else articleId,
                    initialTitle = initialTitle,
                    initialContent = initialContent,
                    initialImageUrl = initialImageUrl
                )
            }
        }
    }
}

@Composable
fun CreateArticleScreen(
    onFinish: () -> Unit,
    articleId: Long? = null,
    initialTitle: String = "",
    initialContent: String = "",
    initialImageUrl: String? = null
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    var title by remember { mutableStateOf(initialTitle) }
    var content by remember { mutableStateOf(initialContent) }
    var imageUri by remember { mutableStateOf<Uri?>(null) }
    var currentImageUrl by remember { mutableStateOf(initialImageUrl) }
    var isLoading by remember { mutableStateOf(false) }

    val isEditMode = articleId != null

    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        imageUri = uri
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = if (isEditMode) "Edit Artikel" else "Bagikan Pengalamanmu",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(bottom = 24.dp)
        )

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
                .padding(bottom = 16.dp),
            contentAlignment = Alignment.Center
        ) {
            if (imageUri != null) {
                AsyncImage(
                    model = imageUri,
                    contentDescription = "Selected Image",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            } else if (currentImageUrl != null) {
                AsyncImage(
                    model = currentImageUrl,
                    contentDescription = "Current Image",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            } else {
                OutlinedButton(onClick = { imagePickerLauncher.launch("image/*") }) {
                    Text("Pilih Foto Header")
                }
            }

            if (imageUri != null || currentImageUrl != null) {
                Button(
                    onClick = { imagePickerLauncher.launch("image/*") },
                    modifier = Modifier.align(Alignment.BottomEnd).padding(8.dp)
                ) {
                    Text("Ganti")
                }
            }
        }

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

                        var finalImageUrl = currentImageUrl
                        if (imageUri != null) {
                            val fileName = "article_${UUID.randomUUID()}.jpg"
                            val bucket = SupabaseClient.client.storage.from("article-images")
                            val bytes = context.contentResolver.openInputStream(imageUri!!)?.readBytes()

                            if (bytes != null) {
                                bucket.upload(fileName, bytes)
                                finalImageUrl = bucket.publicUrl(fileName)
                            }
                        }

                        if (isEditMode) {
                            val updatedArticle = Article(
                                id = articleId,
                                title = title,
                                content = content,
                                userId = currentUser.id,
                                imageUrl = finalImageUrl
                            )
                            SupabaseClient.client.from("articles").update(updatedArticle) {
                                filter { eq("id", articleId) }
                            }
                            Toast.makeText(context, "Artikel berhasil diperbarui!", Toast.LENGTH_LONG).show()
                        } else {
                            val newArticle = Article(
                                title = title,
                                content = content,
                                userId = currentUser.id,
                                imageUrl = finalImageUrl
                            )
                            SupabaseClient.client.from("articles").insert(newArticle)
                            Toast.makeText(context, "Artikel berhasil diposting!", Toast.LENGTH_LONG).show()
                        }

                        onFinish()

                    } catch (e: Exception) {
                        e.printStackTrace()
                        Toast.makeText(context, "Gagal: ${e.message}", Toast.LENGTH_LONG).show()
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
                Text(if (isEditMode) "Simpan Perubahan" else "Posting Artikel")
            }
        }
    }
}
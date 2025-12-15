package com.example.kelompokandori.ui.article

import android.content.Intent
        import android.os.Bundle
        import androidx.activity.ComponentActivity
        import androidx.activity.compose.setContent
        import androidx.compose.foundation.clickable
        import androidx.compose.foundation.layout.*
        import androidx.compose.foundation.lazy.LazyColumn
        import androidx.compose.foundation.lazy.items
        import androidx.compose.material.icons.Icons
        import androidx.compose.material.icons.filled.Add
        import androidx.compose.material.icons.filled.Delete
        import androidx.compose.material.icons.filled.Edit
        import androidx.compose.material.icons.filled.MoreVert
        import androidx.compose.material3.*
        import androidx.compose.runtime.*
        import androidx.compose.ui.Alignment
        import androidx.compose.ui.Modifier
        import androidx.compose.ui.graphics.Color
        import androidx.compose.ui.layout.ContentScale
        import androidx.compose.ui.platform.LocalContext
        import androidx.compose.ui.text.font.FontWeight
        import androidx.compose.ui.text.style.TextOverflow
        import androidx.compose.ui.unit.dp
        import androidx.lifecycle.viewmodel.compose.viewModel
        import coil.compose.AsyncImage
        import com.example.kelompokandori.model.Article
        import com.example.kelompokandori.model.ArticleViewModel
        import com.example.kelompokandori.ui.theme.KelompokAndoriTheme

class ArticleListActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            KelompokAndoriTheme {
                ArticleListScreen()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ArticleListScreen(viewModel: ArticleViewModel = viewModel()) {
    val publicArticles by viewModel.articles.collectAsState()
    val userArticles by viewModel.userArticles.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    val context = LocalContext.current
    var selectedTab by remember { mutableIntStateOf(0) }

    LaunchedEffect(selectedTab) {
        if (selectedTab == 0) viewModel.fetchArticles() else viewModel.fetchUserArticles()
    }

    LaunchedEffect(Unit) {
        viewModel.fetchArticles()
        viewModel.fetchUserArticles()
    }

    Scaffold(
        topBar = {
            Column {
                TopAppBar(title = { Text("Tips & Pengalaman") })
                TabRow(selectedTabIndex = selectedTab) {
                    Tab(
                        selected = selectedTab == 0,
                        onClick = { selectedTab = 0 },
                        text = { Text("Public") }
                    )
                    Tab(
                        selected = selectedTab == 1,
                        onClick = { selectedTab = 1 },
                        text = { Text("My Articles") }
                    )
                }
            }
        },
        floatingActionButton = {
            FloatingActionButton(onClick = {
                context.startActivity(Intent(context, CreateArticleActivity::class.java))
            }) {
                Icon(Icons.Default.Add, contentDescription = "Buat Artikel")
            }
        }
    ) { padding ->
        Box(modifier = Modifier.padding(padding).fillMaxSize()) {

            val currentList = if (selectedTab == 0) publicArticles else userArticles

            if (isLoading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            } else if (currentList.isEmpty()) {
                Column(
                    modifier = Modifier.align(Alignment.Center),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("Belum ada artikel.", style = MaterialTheme.typography.bodyLarge)
                    Button(onClick = {
                        if (selectedTab == 0) viewModel.fetchArticles() else viewModel.fetchUserArticles()
                    }) {
                        Text("Refresh")
                    }
                }
            } else {
                LazyColumn(contentPadding = PaddingValues(16.dp)) {
                    items(currentList) { article ->
                        ArticleItem(
                            article = article,
                            isOwner = selectedTab == 1,
                            onEdit = {
                                val intent = Intent(context, CreateArticleActivity::class.java)
                                intent.putExtra("ARTICLE_ID", article.id)
                                intent.putExtra("ARTICLE_TITLE", article.title)
                                intent.putExtra("ARTICLE_CONTENT", article.content)
                                intent.putExtra("ARTICLE_IMAGE", article.imageUrl)
                                context.startActivity(intent)
                            },
                            onDelete = {
                                article.id?.let { viewModel.deleteArticle(it) }
                            },
                            onClick = {
                                val intent = Intent(context, ArticleDetailActivity::class.java)
                                intent.putExtra("ARTICLE_ID", article.id ?: -1L)
                                intent.putExtra("ARTICLE_TITLE", article.title)
                                intent.putExtra("ARTICLE_CONTENT", article.content)
                                intent.putExtra("ARTICLE_DATE", article.createdAt)
                                intent.putExtra("ARTICLE_IMAGE", article.imageUrl)
                                context.startActivity(intent)
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ArticleItem(
    article: Article,
    isOwner: Boolean,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    onClick: () -> Unit
) {
    var showMenu by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 12.dp)
            .clickable { onClick() },
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column {
            if (article.imageUrl != null) {
                AsyncImage(
                    model = article.imageUrl,
                    contentDescription = null,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(150.dp),
                    contentScale = ContentScale.Crop
                )
            }

            Row(
                modifier = Modifier.padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = article.title,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = article.content,
                        style = MaterialTheme.typography.bodyMedium,
                        maxLines = 3,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                if (isOwner) {
                    Box {
                        IconButton(onClick = { showMenu = true }) {
                            Icon(Icons.Default.MoreVert, contentDescription = "Menu")
                        }
                        DropdownMenu(
                            expanded = showMenu,
                            onDismissRequest = { showMenu = false }
                        ) {
                            DropdownMenuItem(
                                text = { Text("Edit") },
                                leadingIcon = { Icon(Icons.Default.Edit, null) },
                                onClick = {
                                    showMenu = false
                                    onEdit()
                                }
                            )
                            DropdownMenuItem(
                                text = { Text("Delete") },
                                leadingIcon = { Icon(Icons.Default.Delete, null, tint = Color.Red) },
                                onClick = {
                                    showMenu = false
                                    onDelete()
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}
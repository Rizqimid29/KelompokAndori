package com.example.kelompokandori.ui.article

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.kelompokandori.ui.theme.KelompokAndoriTheme

class ArticleDetailActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val title = intent.getStringExtra("ARTICLE_TITLE") ?: ""
        val content = intent.getStringExtra("ARTICLE_CONTENT") ?: ""
        val date = intent.getStringExtra("ARTICLE_DATE") ?: ""

        setContent {
            KelompokAndoriTheme {
                ArticleDetailScreen(title, content, date)
            }
        }
    }
}

@Composable
fun ArticleDetailScreen(title: String, content: String, date: String) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()) // Allow scrolling for long stories
    ) {
        Text(text = title, style = MaterialTheme.typography.headlineLarge)

        Spacer(modifier = Modifier.height(8.dp))
        Text(text = "Diposting pada: $date", style = MaterialTheme.typography.labelMedium)

        HorizontalDivider(modifier = Modifier.padding(vertical = 16.dp))

        Text(text = content, style = MaterialTheme.typography.bodyLarge)
    }
}
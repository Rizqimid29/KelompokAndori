package com.example.kelompokandori.model

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.kelompokandori.SupabaseClient
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.query.Order
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ArticleViewModel : ViewModel() {
    private val _articles = MutableStateFlow<List<Article>>(emptyList())
    val articles: StateFlow<List<Article>> = _articles

    private val _userArticles = MutableStateFlow<List<Article>>(emptyList())
    val userArticles: StateFlow<List<Article>> = _userArticles

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    init {
        fetchArticles()
    }

    fun fetchArticles() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val list = SupabaseClient.client
                    .from("articles")
                    .select {
                        order("created_at", order = Order.DESCENDING)
                    }
                    .decodeList<Article>()

                _articles.value = list
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun fetchUserArticles() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val currentUser = SupabaseClient.client.auth.currentUserOrNull()
                if (currentUser != null) {
                    val list = SupabaseClient.client
                        .from("articles")
                        .select {
                            filter {
                                eq("user_id", currentUser.id)
                            }
                            order("created_at", order = Order.DESCENDING)
                        }
                        .decodeList<Article>()
                    _userArticles.value = list
                }
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun deleteArticle(articleId: Long) {
        viewModelScope.launch {
            try {
                SupabaseClient.client.from("articles").delete {
                    filter { eq("id", articleId) }
                }
                fetchUserArticles()
                fetchArticles()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}
package com.example.kelompokandori.model

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.kelompokandori.data.DiscussionRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class DiscussionViewModel : ViewModel() {
    private val repository = DiscussionRepository()

    private val _threads = MutableStateFlow<List<Discussion>>(emptyList())
    val threads = _threads.asStateFlow()

    private val _comments = MutableStateFlow<List<Comment>>(emptyList())
    val comments = _comments.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    private val _replyingTo = MutableStateFlow<Comment?>(null)
    val replyingTo = _replyingTo.asStateFlow()

    init {
        loadThreads()
    }

    fun loadThreads() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                _threads.value = repository.getThreads()
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun loadComments(threadId: String) {
        viewModelScope.launch {
            try {
                val flatList = repository.getComments(threadId)

                val rootComments = flatList.filter { it.parentId == null }

                val organizedComments = rootComments.map { root ->
                    val children = flatList.filter { it.rootId == root.id }
                    root.copy(replies = children)
                }

                _comments.value = organizedComments
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun postThread(title: String, content: String, imageBytes: ByteArray?, onSuccess: () -> Unit) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                repository.createThread(title, content, imageBytes)
                loadThreads()
                onSuccess()
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun postComment(threadId: String, content: String) {
        viewModelScope.launch {
            try {
                val target = _replyingTo.value

                val parentId = target?.id

                val rootId = if (target == null) null else (target.rootId ?: target.id)

                repository.postComment(threadId, content, parentId, rootId)

                _replyingTo.value = null
                loadComments(threadId)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun setReplyingTo(comment: Comment?) {
        _replyingTo.value = comment
    }
}
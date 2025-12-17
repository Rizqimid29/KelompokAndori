package com.example.kelompokandori.ui.discussion

import android.net.Uri
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
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
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.kelompokandori.model.DiscussionComment
import com.example.kelompokandori.model.Discussion
import com.example.kelompokandori.model.DiscussionViewModel

val DiscussionBlue = Color(0xFF9AA9E3)
val PrimaryPastel = Color(0xFF9AA9E3)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DiscussionScreen(viewModel: DiscussionViewModel = viewModel()) {
    var selectedThread by remember { mutableStateOf<Discussion?>(null) }
    val context = LocalContext.current

    if (selectedThread == null) {
        DiscussionList(
            viewModel = viewModel,
            onThreadClick = { thread ->
                selectedThread = thread
                viewModel.loadComments(thread.id!!)
            }
        )
    } else {
        DiscussionDetail(
            thread = selectedThread!!,
            viewModel = viewModel,
            onBack = { selectedThread = null }
        )
    }
}
@Composable
fun DiscussionList(
    viewModel: DiscussionViewModel,
    onThreadClick: (Discussion) -> Unit
) {
    val threads by viewModel.threads.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    var showCreateDialog by remember { mutableStateOf(false) }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showCreateDialog = true },
                containerColor = DiscussionBlue,
                contentColor = Color.White
            ) { Icon(Icons.Default.Add, "Post") }
        }
    ) { padding ->
        Box(modifier = Modifier.padding(padding).fillMaxSize()) {
            LazyColumn(
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                item {
                    Text("Discussion Board", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
                    Text("Tempat berbagi pertanyaan seputar wisata", color = Color.Gray)
                }
                items(threads) { thread ->
                    ThreadCard(thread, onClick = { onThreadClick(thread) })
                }
            }
            if (isLoading) CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
        }
    }

    if (showCreateDialog) {
        CreateDiscussionDialog(
            onDismiss = { showCreateDialog = false },
            onSubmit = { title, content, bytes ->
                viewModel.postThread(title, content, bytes) { showCreateDialog = false }
            }
        )
    }
}

@Composable
fun DiscussionDetail(
    thread: Discussion,
    viewModel: DiscussionViewModel,
    onBack: () -> Unit
) {
    val comments by viewModel.comments.collectAsState()
    val replyingTo by viewModel.replyingTo.collectAsState()

    var commentInput by remember { mutableStateOf("") }

    LaunchedEffect(replyingTo) {
        replyingTo?.let { target ->
            commentInput = "@${target.userName ?: "User"} "
        }
    }

    BackHandler { onBack() }

    Scaffold(
        bottomBar = {
            Column(Modifier.background(Color.White).padding(8.dp)) {
                if (replyingTo != null) {
                    Row(
                        Modifier.fillMaxWidth().background(Color(0xFFEEEEEE)).padding(horizontal = 8.dp, vertical = 4.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Replying to ${replyingTo?.userName}...", fontSize = 12.sp, color = Color.Gray)
                        Icon(Icons.Default.Close, "Cancel", Modifier.size(16.dp).clickable {
                            viewModel.setReplyingTo(null)
                            commentInput = ""
                        })
                    }
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    OutlinedTextField(
                        value = commentInput,
                        onValueChange = { commentInput = it },
                        placeholder = { Text("Tulis komentar...") },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(24.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = DiscussionBlue,
                            focusedLabelColor = DiscussionBlue,
                            cursorColor = DiscussionBlue
                        )
                    )
                    IconButton(onClick = {
                        if (commentInput.isNotBlank()) {
                            viewModel.postComment(thread.id!!, commentInput)
                            commentInput = ""
                        }
                    }) { Icon(Icons.Default.Send, null, tint = DiscussionBlue) }
                }
            }
        }
    ) { padding ->
        LazyColumn(Modifier.padding(padding).fillMaxSize(), contentPadding = PaddingValues(16.dp)) {
            item {
                Button(onClick = onBack, colors = ButtonDefaults.textButtonColors()) { Text("< Back", color = DiscussionBlue) }
                ThreadCard(thread, onClick = {})
                Divider(Modifier.padding(vertical = 16.dp))
                Text("${comments.fold(0) { acc, c -> acc + 1 + c.replies.size }} Comments", fontWeight = FontWeight.Bold)
                Spacer(Modifier.height(16.dp))
            }

            items(comments) { comment ->
                CommentItem(comment, onReply = { viewModel.setReplyingTo(it) })
            }
        }
    }
}

@Composable
fun ThreadCard(thread: Discussion, onClick: () -> Unit) {
    Card(
        Modifier.fillMaxWidth().clickable { onClick() },
        elevation = CardDefaults.cardElevation(4.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(Modifier.padding(16.dp)) {
            Text(thread.title, fontWeight = FontWeight.Bold, fontSize = 18.sp)
            Text("by ${thread.userName ?: "User"}", fontSize = 12.sp, color = Color.Gray)
            Spacer(Modifier.height(8.dp))
            Text(thread.content)
            if (thread.imageUrl != null) {
                Spacer(Modifier.height(8.dp))
                AsyncImage(
                    model = thread.imageUrl, contentDescription = null,
                    modifier = Modifier.fillMaxWidth().height(180.dp).clip(RoundedCornerShape(8.dp)),
                    contentScale = ContentScale.Crop
                )
            }
        }
    }
}

@Composable
fun CommentItem(comment: DiscussionComment, onReply: (DiscussionComment) -> Unit) {
    Column(Modifier.padding(vertical = 8.dp)) {
        CommentBubble(comment, onReply)

        if (comment.replies.isNotEmpty()) {
            Column(Modifier.padding(start = 32.dp, top = 8.dp)) {
                comment.replies.forEach { reply ->
                    CommentBubble(reply, onReply)
                    Spacer(Modifier.height(8.dp))
                }
            }
        }
    }
}

@Composable
fun CommentBubble(comment: DiscussionComment, onReply: (DiscussionComment) -> Unit) {
    Row(verticalAlignment = Alignment.Top) {
        if (comment.userAvatar != null) {
            AsyncImage(
                model = comment.userAvatar, contentDescription = null,
                modifier = Modifier.size(32.dp).clip(CircleShape), contentScale = ContentScale.Crop
            )
        } else {
            Box(Modifier.size(32.dp).background(Color.LightGray, CircleShape))
        }

        Spacer(Modifier.width(8.dp))

        Column {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(comment.userName ?: "User", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                Spacer(Modifier.width(8.dp))
                Text("Reply", color = Color.Gray, fontSize = 11.sp, fontWeight = FontWeight.Bold, modifier = Modifier.clickable { onReply(comment) })
            }
            Text(comment.content, fontSize = 14.sp)
        }
    }
}

@Composable
fun CreateDiscussionDialog(onDismiss: () -> Unit, onSubmit: (String, String, ByteArray?) -> Unit) {
    var title by remember { mutableStateOf("") }
    var content by remember { mutableStateOf("") }
    var imageUri by remember { mutableStateOf<Uri?>(null) }
    val context = LocalContext.current
    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.PickVisualMedia()) { imageUri = it }

    androidx.compose.ui.window.Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(Modifier.padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                Text("Buat Diskusi", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
                Spacer(Modifier.height(16.dp))
                OutlinedTextField(title, { title = it }, label = { Text("Judul") }, modifier = Modifier.fillMaxWidth())
                Spacer(Modifier.height(8.dp))
                OutlinedTextField(content, { content = it }, label = { Text("Isi") }, minLines = 3, modifier = Modifier.fillMaxWidth())
                Spacer(Modifier.height(16.dp))
                OutlinedButton(onClick = { launcher.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)) }, modifier = Modifier.fillMaxWidth()) {
                    Text(if (imageUri == null) "Upload Foto" else "Foto Terpilih")
                }
                Spacer(Modifier.height(16.dp))
                Row {
                    TextButton({ onDismiss() }, Modifier.weight(1f)) { Text("Batal", color = Color.Gray) }
                    Button({
                        val bytes = imageUri?.let { context.contentResolver.openInputStream(it)?.readBytes() }
                        onSubmit(title, content, bytes)
                    }, Modifier.weight(1f), colors = ButtonDefaults.buttonColors(containerColor = DiscussionBlue)) { Text("Post") }
                }
            }
        }
    }
}
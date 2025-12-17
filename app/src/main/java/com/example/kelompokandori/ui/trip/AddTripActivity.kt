package com.example.kelompokandori.ui.trip

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
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.kelompokandori.SupabaseClient
import com.example.kelompokandori.ui.theme.KelompokAndoriTheme
import io.github.jan.supabase.auth.auth
import kotlinx.coroutines.launch

class AddTripActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            KelompokAndoriTheme {
                AddTripScreen(onFinish = { finish() })
            }
        }
    }
}

@Composable
fun AddTripScreen(onFinish: () -> Unit) {
    val context = LocalContext.current
    val viewModel: TripViewModel = viewModel()
    val scope = rememberCoroutineScope()

    var destination by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var startDate by remember { mutableStateOf("") }
    var endDate by remember { mutableStateOf("") }
    var imageUri by remember { mutableStateOf<Uri?>(null) }
    var imageByteArray by remember { mutableStateOf<ByteArray?>(null) }
    
    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            imageUri = it
            val inputStream = context.contentResolver.openInputStream(it)
            imageByteArray = inputStream?.readBytes()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text("Rencanakan Perjalananmu", style = MaterialTheme.typography.headlineMedium)

        Box(
            modifier = Modifier.fillMaxWidth().height(200.dp),
            contentAlignment = Alignment.Center
        ) {
            if (imageUri != null) {
                AsyncImage(
                    model = imageUri,
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            } else {
                Button(onClick = { imagePickerLauncher.launch("image/*") }) {
                    Text("Pilih Foto Destinasi")
                }
            }
        }

        OutlinedTextField(
            value = destination,
            onValueChange = { destination = it },
            label = { Text("Nama Destinasi") },
            modifier = Modifier.fillMaxWidth()
        )

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            OutlinedTextField(
                value = startDate,
                onValueChange = { startDate = it },
                label = { Text("Mulai (YYYY-MM-DD)") },
                modifier = Modifier.weight(1f)
            )
            OutlinedTextField(
                value = endDate,
                onValueChange = { endDate = it },
                label = { Text("Selesai (YYYY-MM-DD)") },
                modifier = Modifier.weight(1f)
            )
        }

        OutlinedTextField(
            value = description,
            onValueChange = { description = it },
            label = { Text("Deskripsi / Catatan") },
            modifier = Modifier.fillMaxWidth().height(120.dp),
            maxLines = 5
        )

        Button(
            onClick = {
                scope.launch {
                    val user = SupabaseClient.client.auth.currentUserOrNull()
                    if (user != null) {
                        viewModel.addTrip(
                            destination = destination,
                            startDate = startDate,
                            endDate = endDate,
                            description = description,
                            userId = user.id,
                            imageByteArray = imageByteArray
                        )
                        Toast.makeText(context, "Berhasil disimpan!", Toast.LENGTH_SHORT).show()
                        onFinish()
                    } else {
                        Toast.makeText(context, "Silakan login ulang", Toast.LENGTH_SHORT).show()
                    }
                }
            },
            modifier = Modifier.fillMaxWidth(),
            enabled = !viewModel.isLoading
        ) {
            if (viewModel.isLoading) {
                CircularProgressIndicator(color = MaterialTheme.colorScheme.onPrimary, modifier = Modifier.size(24.dp))
            } else {
                Text("Simpan Rencana")
            }
        }
    }
}
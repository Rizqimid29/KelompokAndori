package com.example.kelompokandori.ui.auth

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import com.example.kelompokandori.ui.home.Profile

class Login : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val context = LocalContext.current
            var username by remember { mutableStateOf("") }
            var password by remember { mutableStateOf("") }

            Column {
                TextField(value = username, onValueChange = { username = it }, placeholder = { Text("Username") })
                TextField(value = password, onValueChange = { password = it }, placeholder = { Text("Password") })
                Button(onClick = {
                    context.startActivity(Intent(context, Profile::class.java))
                }) {
                    Text("Login")
                }
                Button(onClick = {
                    context.startActivity(Intent(context, Register::class.java))
                }) {
                    Text("Register")
                }
            }
        }
    }
}
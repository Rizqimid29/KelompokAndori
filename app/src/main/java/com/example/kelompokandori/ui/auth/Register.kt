package com.example.kelompokandori.ui.auth

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext

class Register : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val context = LocalContext.current
            var email by remember { mutableStateOf("") }
            var password by remember { mutableStateOf("") }

            Column {
                TextField(value = email, onValueChange = { email = it }, placeholder = { Text("Email") })
                TextField(value = password, onValueChange = { password = it }, placeholder = { Text("Password") })
                Button(onClick = {
                    var currentContext = context
                    while (currentContext is ContextWrapper) {
                        if (currentContext is Activity) {
                            currentContext.finish()
                            return@Button
                        }
                        currentContext = currentContext.baseContext
                    }
                }) {
                    Text("Save")
                }
            }
        }
    }
}
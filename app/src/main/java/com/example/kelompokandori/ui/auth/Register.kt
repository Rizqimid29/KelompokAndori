package com.example.kelompokandori.ui.auth

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import com.example.kelompokandori.ui.home.Profile

class Register : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val context = LocalContext.current
            var firstName by remember {
                mutableStateOf("")
            }
            var lastName by remember {
                mutableStateOf("")
            }
            var username by remember {
                mutableStateOf("")
            }
            var email by remember {
                mutableStateOf("")
            }
            var password by remember {
                mutableStateOf("")
            }
            var phoneNumber by remember {
                mutableStateOf("")
            }
            var address by remember {
                mutableStateOf("")
            }
            var dateOfBirth by remember {
                mutableStateOf("")
            }

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                TextField(value = firstName,
                    onValueChange = {
                        firstName = it },
                    placeholder = {
                        Text("First Name") }
                )
                TextField(value = lastName,
                    onValueChange = {
                        lastName = it },
                    placeholder = {
                        Text("Last Name") }
                )
                TextField(value = username,
                    onValueChange = {
                        username = it },
                    placeholder = {
                        Text("Username") }
                )
                TextField(value = email,
                    onValueChange = {
                        email = it },
                    placeholder = {
                        Text("Email") }
                )
                TextField(
                    value = password,
                    onValueChange = {
                        password = it },
                    placeholder = {
                        Text("Password") },
                    visualTransformation = PasswordVisualTransformation()
                )
                TextField(value = phoneNumber,
                    onValueChange = {
                        phoneNumber = it },
                    placeholder = { Text("Phone Number") }
                )
                TextField(value = address,
                    onValueChange = {
                        address = it },
                    placeholder = {
                        Text("Address") })
                TextField(value = dateOfBirth,
                    onValueChange = {
                        dateOfBirth = it },
                    placeholder = {
                        Text("Date of Birth (YYYY-MM-DD)") }
                )

                Button(onClick = {
                    val intent = Intent(context, Profile::class.java).apply {
                        putExtra("FIRST_NAME", firstName)
                        putExtra("LAST_NAME", lastName)
                        putExtra("USERNAME", username)
                        putExtra("EMAIL", email)
                        putExtra("PHONE_NUMBER", phoneNumber)
                        putExtra("ADDRESS", address)
                        putExtra("DATE_OF_BIRTH", dateOfBirth)
                    }
                    context.startActivity(intent)

                    var currentContext = context
                    while (currentContext is ContextWrapper) {
                        if (currentContext is Activity) {
                            currentContext.finish()
                            break
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

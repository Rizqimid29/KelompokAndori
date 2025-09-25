package com.example.kelompokandori.ui.home

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
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.kelompokandori.ui.auth.Login

class Profile : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val firstName = intent.getStringExtra("FIRST_NAME") ?: "N/A"
        val lastName = intent.getStringExtra("LAST_NAME") ?: "N/A"
        val username = intent.getStringExtra("USERNAME") ?: "N/A"
        val email = intent.getStringExtra("EMAIL") ?: "N/A"
        val phoneNumber = intent.getStringExtra("PHONE_NUMBER") ?: "N/A"
        val address = intent.getStringExtra("ADDRESS") ?: "N/A"
        val dateOfBirth = intent.getStringExtra("DATE_OF_BIRTH") ?: "N/A"

        setContent {
            ProfileScreen(
                firstName = firstName,
                lastName = lastName,
                username = username,
                email = email,
                phoneNumber = phoneNumber,
                address = address,
                dateOfBirth = dateOfBirth
            )
        }
    }
}

@Composable
fun ProfileScreen(
    firstName: String,
    lastName: String,
    username: String,
    email: String,
    phoneNumber: String,
    address: String,
    dateOfBirth: String
) {
    val context = LocalContext.current
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.Start
    ) {
        Text("First Name: $firstName")
        Text("Last Name: $lastName")
        Text("Username: $username")
        Text("Email: $email")
        Text("Phone Number: $phoneNumber")
        Text("Address: $address")
        Text("Date of Birth: $dateOfBirth")
        Button(
            onClick = {
                val intent = Intent(context, Login::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                context.startActivity(intent)
            },
            modifier = Modifier.align(Alignment.CenterHorizontally)
        ) {
            Text("Logout")
        }
    }
}

package com.example.kelompokandori

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import com.example.kelompokandori.ui.auth.Login

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        startActivity(Intent(this, Login::class.java))
        finish()
    }
}

package com.example.kelompokandori

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import io.github.jan.supabase.auth.auth
import com.example.kelompokandori.ui.home.HomeScreen
import com.example.kelompokandori.ui.auth.AuthScreen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val session = SupabaseClient.client.auth.currentSessionOrNull()
        val startScreen = if (session != null) "home" else "auth"

        setContent {
            MaterialTheme {
                val navController = rememberNavController()

                NavHost(navController = navController, startDestination = startScreen) {

                    composable("auth") {
                        AuthScreen(onLoginSuccess = {
                            navController.navigate("home") {
                                popUpTo("auth") { inclusive = true }
                            }
                        })
                    }

                    composable("home") {
                        HomeScreen()
                    }
                }
            }
        }
    }
}
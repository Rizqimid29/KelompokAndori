package com.example.kelompokandori.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.kelompokandori.ui.article.ArticleListScreen
import com.example.kelompokandori.ui.home.ProfileScreen
import com.example.kelompokandori.ui.trip.TripListScreen

// 1. Model untuk Item Navbar
sealed class BottomNavItem(val route: String, val title: String, val icon: ImageVector) {
    object Trip : BottomNavItem("trip", "Trip", Icons.Default.Place)
    object Review : BottomNavItem("review", "Review", Icons.Default.Star)
    object Article : BottomNavItem("article", "Article", Icons.Default.Article)
    object Discuss : BottomNavItem("discuss", "Discuss", Icons.Default.Chat)
    object Profile : BottomNavItem("profile", "Profile", Icons.Default.Person)
}

// 2. Composable Utama dengan Navbar
@Composable
fun MainScreen(onLogout: () -> Unit) {
    val navController = rememberNavController()

    // Daftar menu
    val items = listOf(
        BottomNavItem.Trip,
        BottomNavItem.Review,
        BottomNavItem.Article,
        BottomNavItem.Discuss,
        BottomNavItem.Profile
    )

    Scaffold(
        bottomBar = {
            NavigationBar {
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentRoute = navBackStackEntry?.destination?.route

                items.forEach { item ->
                    NavigationBarItem(
                        icon = { Icon(item.icon, contentDescription = item.title) },
                        label = { Text(item.title) },
                        selected = currentRoute == item.route,
                        onClick = {
                            navController.navigate(item.route) {
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    )
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = BottomNavItem.Trip.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            // --- FITUR 1: TRIP ---
            composable(BottomNavItem.Trip.route) {
                TripListScreen(
                    onNavigateToAdd = {
                        // Panggil Activity AddTripActivity
                        navController.context.startActivity(
                            android.content.Intent(navController.context, com.example.kelompokandori.ui.trip.AddTripActivity::class.java)
                        )
                    }
                )
            }

            // --- FITUR 2: REVIEW ---
            composable(BottomNavItem.Review.route) {
                // Ganti dengan screen Review kamu nanti
                PlaceholderScreen("Fitur Review")
            }

            // --- FITUR 3: ARTICLE ---
            composable(BottomNavItem.Article.route) {
                ArticleListScreen()
            }

            // --- FITUR 4: DISCUSS ---
            composable(BottomNavItem.Discuss.route) {
                // Ganti dengan screen Discuss kamu nanti
                PlaceholderScreen("Fitur Diskusi")
            }

            // --- FITUR 5: PROFIL ---
            composable(BottomNavItem.Profile.route) {
                // Untuk sementara pakai dummy data agar tidak error
                ProfileScreen(
                    firstName = "User",
                    lastName = "Pengguna",
                    username = "user123",
                    email = "email@contoh.com",
                    phoneNumber = "-",
                    address = "-",
                    dateOfBirth = "-"
                )
                // Nanti bisa tambahkan tombol logout di dalam ProfileScreen yang memanggil onLogout()
            }
        }
    }
}

@Composable
fun PlaceholderScreen(text: String) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text(text)
    }
}
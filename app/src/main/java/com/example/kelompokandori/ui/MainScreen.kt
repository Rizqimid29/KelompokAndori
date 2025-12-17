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
import com.example.kelompokandori.ui.discussion.DiscussionScreen
import com.example.kelompokandori.ui.review.AddReviewScreen

sealed class BottomNavItem(val route: String, val title: String, val icon: ImageVector) {
    object Trip : BottomNavItem("trip", "Trip", Icons.Default.Place)
    object Review : BottomNavItem("review", "Review", Icons.Default.Star)
    object Article : BottomNavItem("article", "Article", Icons.Default.Article)
    object Discuss : BottomNavItem("discuss", "Discuss", Icons.Default.Chat)
    object Profile : BottomNavItem("profile", "Profile", Icons.Default.Person)
}

@Composable
fun MainScreen(onLogout: () -> Unit) {
    val navController = rememberNavController()

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
            composable(BottomNavItem.Trip.route) {
                TripListScreen(
                    onNavigateToAdd = {
                        navController.context.startActivity(
                            android.content.Intent(navController.context, com.example.kelompokandori.ui.trip.AddTripActivity::class.java)
                        )
                    }
                )
            }

            composable(BottomNavItem.Review.route) {
                AddReviewScreen()
            }

            composable(BottomNavItem.Article.route) {
                ArticleListScreen()
            }

            composable(BottomNavItem.Discuss.route) {
                DiscussionScreen()
            }

            composable(BottomNavItem.Profile.route) {
                ProfileScreen()
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
package com.erdem.shaman.ui.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.erdem.shaman.ui.screens.*

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val bottomBarRoutes = listOf("symptom", "profile")

    Scaffold(
        bottomBar = {
            if (currentRoute in bottomBarRoutes) {
                NavigationBar(
                    containerColor = Color(0xFF1A2F5A)
                ) {
                    NavigationBarItem(
                        icon = { Icon(Icons.Default.Favorite, contentDescription = "Analiz") },
                        label = { Text("Analiz") },
                        selected = currentRoute == "symptom",
                        onClick = { navController.navigate("symptom") { launchSingleTop = true } },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = Color(0xFF00BFA5),
                            selectedTextColor = Color(0xFF00BFA5),
                            unselectedIconColor = Color.White.copy(alpha = 0.5f),
                            unselectedTextColor = Color.White.copy(alpha = 0.5f),
                            indicatorColor = Color.Transparent
                        )
                    )
                    NavigationBarItem(
                        icon = { Icon(Icons.Default.Person, contentDescription = "Profil") },
                        label = { Text("Profil") },
                        selected = currentRoute == "profile",
                        onClick = { navController.navigate("profile") { launchSingleTop = true } },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = Color(0xFF00BFA5),
                            selectedTextColor = Color(0xFF00BFA5),
                            unselectedIconColor = Color.White.copy(alpha = 0.5f),
                            unselectedTextColor = Color.White.copy(alpha = 0.5f),
                            indicatorColor = Color.Transparent
                        )
                    )
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = "login",
            modifier = Modifier.padding(innerPadding)
        ) {
            composable("login") { LoginScreen(navController) }
            composable("register") { RegisterScreen(navController) }
            composable("symptom") { SymptomFlowScreen(navController) }
            composable("result/{result}") { backStackEntry ->
                val result = try {
                    java.net.URLDecoder.decode(
                        backStackEntry.arguments?.getString("result") ?: "", "UTF-8"
                    )
                } catch (e: Exception) {
                    backStackEntry.arguments?.getString("result") ?: ""
                }
                ResultScreen(navController, result)
            }
            composable("disease/{name}") { backStackEntry ->
                val name = java.net.URLDecoder.decode(
                    backStackEntry.arguments?.getString("name") ?: "", "UTF-8"
                )
                DiseaseDetailScreen(navController, name)
            }
            composable("profile") { ProfileScreen(navController) }
        }
    }
}
package com.kelasxi.aplikasimonitoringkelas

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.*
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.kelasxi.aplikasimonitoringkelas.ui.screens.*
import com.kelasxi.aplikasimonitoringkelas.ui.theme.AplikasiMonitoringKelasTheme
import com.kelasxi.aplikasimonitoringkelas.utils.SharedPrefManager

class KepalaSekolahActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AplikasiMonitoringKelasTheme {
                KepalaSekolahNavigationHost()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun KepalaSekolahNavigationHost() {
    val navController = rememberNavController()
    val context = LocalContext.current
    val sharedPrefManager = remember { SharedPrefManager.getInstance(context) }
    val userName = sharedPrefManager.getUserName() ?: "Kepala Sekolah"

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = userName,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF1F3A7F)
                ),
                actions = {
                    IconButton(onClick = {
                        sharedPrefManager.clearToken()
                        val intent = Intent(context, MainActivity::class.java)
                        intent.flags =
                            Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        context.startActivity(intent)
                    }) {
                        Icon(
                            Icons.AutoMirrored.Filled.Logout,
                            contentDescription = "Logout",
                            tint = Color.White
                        )
                    }
                }
            )
        },
        bottomBar = {
            NavigationBar(
                containerColor = Color.White,
                contentColor = Color(0xFF1F3A7F),
                modifier = Modifier.height(80.dp)
            ) {
                val backStackEntry by navController.currentBackStackEntryAsState()
                val currentRoute = backStackEntry?.destination?.route

                NavigationBarItem(
                    selected = currentRoute == "dashboard",
                    onClick = {
                        navController.navigate("dashboard") {
                            popUpTo("dashboard") { inclusive = true }
                        }
                    },
                    icon = {
                        Icon(
                            Icons.Default.Dashboard,
                            contentDescription = "Dashboard"
                        )
                    },
                    label = { Text("Dashboard", fontSize = 10.sp) }
                )

                NavigationBarItem(
                    selected = currentRoute == "tren",
                    onClick = {
                        navController.navigate("tren") {
                            popUpTo("tren") { inclusive = true }
                        }
                    },
                    icon = {
                        Icon(
                            Icons.AutoMirrored.Filled.TrendingUp,
                            contentDescription = "Tren"
                        )
                    },
                    label = { Text("Tren", fontSize = 10.sp) }
                )

                NavigationBarItem(
                    selected = currentRoute == "ranking",
                    onClick = {
                        navController.navigate("ranking") {
                            popUpTo("ranking") { inclusive = true }
                        }
                    },
                    icon = {
                        Icon(
                            Icons.Default.EmojiEvents,
                            contentDescription = "Ranking"
                        )
                    },
                    label = { Text("Ranking", fontSize = 10.sp) }
                )

                NavigationBarItem(
                    selected = currentRoute == "laporan",
                    onClick = {
                        navController.navigate("laporan") {
                            popUpTo("laporan") { inclusive = true }
                        }
                    },
                    icon = {
                        Icon(
                            Icons.Default.Assessment,
                            contentDescription = "Laporan"
                        )
                    },
                    label = { Text("Laporan", fontSize = 10.sp) }
                )

                NavigationBarItem(
                    selected = currentRoute == "profil",
                    onClick = {
                        navController.navigate("profil") {
                            popUpTo("profil") { inclusive = true }
                        }
                    },
                    icon = {
                        Icon(
                            Icons.Default.Person,
                            contentDescription = "Profil"
                        )
                    },
                    label = { Text("Profil", fontSize = 10.sp) }
                )
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = "dashboard",
            modifier = Modifier.padding(innerPadding)
        ) {
            composable("dashboard") {
                KepalaSekolahDashboardScreen()
            }
            composable("tren") {
                KepalaSekolahTrenScreen()
            }
            composable("ranking") {
                KepalaSekolahRankingScreen()
            }
            composable("laporan") {
                KepalaSekolahLaporanScreen()
            }
            composable("profil") {
                KepalaSekolahProfileScreen(navController)
            }
        }
    }
}

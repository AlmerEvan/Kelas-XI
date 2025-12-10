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
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.kelasxi.aplikasimonitoringkelas.ui.screens.*
import com.kelasxi.aplikasimonitoringkelas.ui.theme.AplikasiMonitoringKelasTheme
import com.kelasxi.aplikasimonitoringkelas.utils.SharedPrefManager

class AdminActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AplikasiMonitoringKelasTheme {
                AdminNavigationHost()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminNavigationHost() {
    val navController = rememberNavController()
    val context = LocalContext.current
    val sharedPrefManager = remember { SharedPrefManager.getInstance(context) }
    val userName = sharedPrefManager.getUserName() ?: "Admin"

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
                            Icons.Default.Logout,
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
                    selected = currentRoute == "entry_user",
                    onClick = {
                        navController.navigate("entry_user") {
                            popUpTo("entry_user") { inclusive = true }
                        }
                    },
                    icon = {
                        Icon(
                            Icons.Default.Person,
                            contentDescription = "Entry User"
                        )
                    },
                    label = { Text("Entry User", fontSize = 10.sp) }
                )

                NavigationBarItem(
                    selected = currentRoute == "entry_jadwal",
                    onClick = {
                        navController.navigate("entry_jadwal") {
                            popUpTo("entry_jadwal") { inclusive = true }
                        }
                    },
                    icon = {
                        Icon(
                            Icons.Default.DateRange,
                            contentDescription = "Entry Jadwal"
                        )
                    },
                    label = { Text("Entry Jadwal", fontSize = 10.sp) }
                )

                NavigationBarItem(
                    selected = currentRoute == "list_jadwal",
                    onClick = {
                        navController.navigate("list_jadwal") {
                            popUpTo("list_jadwal") { inclusive = true }
                        }
                    },
                    icon = {
                        Icon(
                            Icons.Default.ViewList,
                            contentDescription = "List Jadwal"
                        )
                    },
                    label = { Text("List Jadwal", fontSize = 10.sp) }
                )

                NavigationBarItem(
                    selected = currentRoute == "admin_profile",
                    onClick = {
                        navController.navigate("admin_profile") {
                            popUpTo("admin_profile") { inclusive = true }
                        }
                    },
                    icon = {
                        Icon(
                            Icons.Default.AccountCircle,
                            contentDescription = "Profile"
                        )
                    },
                    label = { Text("Profile", fontSize = 10.sp) }
                )
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = "entry_user",
            modifier = Modifier.padding(innerPadding)
        ) {
            composable("entry_user") {
                AdminEntryUserScreen()
            }
            composable("entry_jadwal") {
                AdminEntryJadwalScreen()
            }
            composable("list_jadwal") {
                AdminListJadwalScreen()
            }
            composable("admin_profile") {
                AdminProfileScreen(navController)
            }
        }
    }
}

package com.kelasxi.aplikasimonitoringkelas

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.kelasxi.aplikasimonitoringkelas.ui.screens.*
import com.kelasxi.aplikasimonitoringkelas.ui.theme.AplikasiMonitoringKelasTheme

class SiswaActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AplikasiMonitoringKelasTheme {
                SiswaMainScreen()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SiswaMainScreen() {
    val navController = rememberNavController()

    val navigationItems = listOf(
        SiswaNavItem("home", "Home", Icons.Default.Home),
        SiswaNavItem("jadwal", "Jadwal", Icons.Default.Schedule),
        SiswaNavItem("guru_pengganti", "Pengganti", Icons.Default.Person),
        SiswaNavItem("profile", "Profile", Icons.Default.AccountCircle)
    )

    Scaffold(
        bottomBar = {
            SiswaBottomNavigation(navController = navController, items = navigationItems)
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = "home",
            modifier = Modifier.padding(innerPadding)
        ) {
            composable("home") {
                SiswaHomeScreen()
            }
            composable("jadwal") {
                JadwalPage()
            }
            composable("guru_pengganti") {
                SiswaGuruPenggantiScreen()
            }
            composable("profile") {
                SiswaProfileScreen()
            }
            composable("guru_list") {
                SiswaGuruScreen()
            }
        }
    }
}

@Composable
fun SiswaBottomNavigation(
    navController: NavController,
    items: List<SiswaNavItem>
) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    NavigationBar(
        containerColor = Color(0xFFF8F9FA),
        contentColor = Color(0xFF5E72E4),
        tonalElevation = 4.dp
    ) {
        items.forEach { item ->
            NavigationBarItem(
                icon = {
                    Icon(
                        item.icon,
                        contentDescription = item.label,
                        modifier = Modifier.size(24.dp)
                    )
                },
                label = {
                    Text(
                        text = item.label,
                        style = MaterialTheme.typography.labelSmall
                    )
                },
                selected = currentRoute == item.route,
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = Color(0xFF5E72E4),
                    selectedTextColor = Color(0xFF5E72E4),
                    unselectedIconColor = Color(0xFF718096),
                    unselectedTextColor = Color(0xFF718096),
                    indicatorColor = Color(0xFF5E72E4).copy(alpha = 0.1f)
                ),
                onClick = {
                    navController.navigate(item.route) {
                        popUpTo(navController.graph.startDestinationId) {
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

data class SiswaNavItem(
    val route: String,
    val label: String,
    val icon: ImageVector
)

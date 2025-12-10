package com.kelasxi.aplikasimonitoringkelas

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.*
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.kelasxi.aplikasimonitoringkelas.ui.screens.*
import com.kelasxi.aplikasimonitoringkelas.ui.theme.AplikasiMonitoringKelasTheme
import com.kelasxi.aplikasimonitoringkelas.ui.theme.*
import com.kelasxi.aplikasimonitoringkelas.utils.SharedPrefManager
import kotlinx.coroutines.launch

class KurikulumActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AplikasiMonitoringKelasTheme {
                KurikulumScreen()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun KurikulumScreen() {
    val context = LocalContext.current
    val navController = rememberNavController()
    val sharedPrefManager = remember { SharedPrefManager.getInstance(context) }
    val scope = rememberCoroutineScope()
    
    // Navigation items for bottom bar (6 screens)
    val navigationItems = listOf(
        NavigationItem("dashboard", "Dashboard", Icons.Default.Home),
        NavigationItem("kehadiran", "Kehadiran", Icons.Default.CheckCircle),
        NavigationItem("laporan", "Laporan", Icons.Default.BarChart),
        NavigationItem("guru_pengganti", "Pengganti", Icons.Default.PersonAdd),
        NavigationItem("jadwal", "Jadwal", Icons.Default.Schedule),
        NavigationItem("daftar_guru", "Guru", Icons.Default.Person)
    )
    
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = "Kurikulum",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold
                    )
                },
                actions = {
                    IconButton(onClick = {
                        scope.launch {
                            sharedPrefManager.clearToken()
                            val intent = Intent(context, MainActivity::class.java)
                            context.startActivity(intent)
                            (context as? ComponentActivity)?.finish()
                        }
                    }) {
                        Icon(
                            Icons.AutoMirrored.Filled.Logout,
                            contentDescription = "Logout",
                            tint = SMKPrimary
                        )
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = SMKSurface,
                    titleContentColor = SMKOnSurface,
                    actionIconContentColor = SMKPrimary
                )
            )
        },
        bottomBar = {
            BottomNavigationBar(navController = navController, items = navigationItems)
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = "dashboard",
            modifier = Modifier.padding(innerPadding)
        ) {
            // Dashboard Screen
            composable("dashboard") {
                DashboardKurikulumPage()
            }
            
            // Kehadiran (Attendance) Screen
            composable("kehadiran") {
                KehadiranKurikulumPage()
            }
            
            // Laporan Kehadiran (Attendance Report) Screen
            composable("laporan") {
                LaporanKehadiranKurikulumPage()
            }
            
            // Guru Pengganti (Substitute Teachers) Screen
            composable("guru_pengganti") {
                LaporanKehadiranGuruPenggantiScreen()
            }
            
            // Jadwal (Schedule) Screen
            composable("jadwal") {
                JadwalPage()
            }
            
            // Daftar Guru (Teacher List) Screen
            composable("daftar_guru") {
                DaftarGuruKurikulumPage()
            }
        }
    }
}

/**
 * Bottom Navigation Bar with dynamic items
 */
@Composable
fun BottomNavigationBar(
    navController: NavController,
    items: List<NavigationItem>
) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    
    NavigationBar(
        containerColor = SMKSurface,
        contentColor = SMKPrimary,
        tonalElevation = 8.dp,
        modifier = Modifier.fillMaxWidth()
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
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = if (currentRoute == item.route) FontWeight.Bold else FontWeight.Medium
                    )
                },
                selected = currentRoute == item.route,
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = SMKPrimary,
                    selectedTextColor = SMKPrimary,
                    unselectedIconColor = NeutralGray500,
                    unselectedTextColor = NeutralGray500,
                    indicatorColor = SMKPrimaryContainer
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

/**
 * Data class for navigation items
 */
data class NavigationItem(
    val route: String,
    val label: String,
    val icon: ImageVector
)

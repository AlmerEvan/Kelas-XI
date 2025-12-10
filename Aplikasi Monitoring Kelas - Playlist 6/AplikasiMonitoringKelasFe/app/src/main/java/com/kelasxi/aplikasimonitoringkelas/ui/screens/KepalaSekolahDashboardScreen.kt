package com.kelasxi.aplikasimonitoringkelas.ui.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.*
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kelasxi.aplikasimonitoringkelas.data.api.RetrofitClient
import com.kelasxi.aplikasimonitoringkelas.data.repository.AppRepositoryNew
import com.kelasxi.aplikasimonitoringkelas.ui.theme.*
import com.kelasxi.aplikasimonitoringkelas.utils.SharedPrefManager
import kotlinx.coroutines.launch
import kotlinx.coroutines.delay
import java.time.LocalDate

@Composable
fun KepalaSekolahDashboardScreen() {
    val context = LocalContext.current
    val sharedPrefManager = remember { SharedPrefManager.getInstance(context) }
    val token = sharedPrefManager.getToken()
    val repository = remember { AppRepositoryNew(RetrofitClient.apiService) }
    val scope = rememberCoroutineScope()

    var userName by remember { mutableStateOf(sharedPrefManager.getUserName() ?: "Kepala Sekolah") }
    var todayAttendancePercent by remember { mutableStateOf(0) }
    var totalGuru by remember { mutableStateOf(0) }
    var totalKelas by remember { mutableStateOf(0) }
    var totalJadwal by remember { mutableStateOf(0) }
    var jadwalHariIni by remember { mutableStateOf(0) }
    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    // Function to load dashboard data
    fun loadDashboardData() {
        if (token != null) {
            scope.launch {
                try {
                    errorMessage = null
                    
                    // Get teachers count
                    repository.getTeachers(token).onSuccess { teachersResponse ->
                        totalGuru = teachersResponse.data?.size ?: 0
                        println("DEBUG KepSek: Teachers loaded - $totalGuru")
                    }.onFailure { error ->
                        println("DEBUG KepSek: Error getTeachers - ${error.message}")
                    }

                    // Get schedules count
                    repository.getSchedules(token).onSuccess { schedulesResponse ->
                        totalJadwal = schedulesResponse.data?.size ?: 0
                        
                        // Get unique classes from schedules
                        val uniqueClasses = schedulesResponse.data?.map { it.kelas }?.distinct() ?: emptyList()
                        totalKelas = uniqueClasses.size
                        println("DEBUG KepSek: Schedules loaded - totalJadwal: $totalJadwal, totalKelas: $totalKelas, classes: $uniqueClasses")
                        
                        // Get jadwal hari ini
                        val hari = getCurrentDay()
                        val jadwalHariIniList = schedulesResponse.data?.filter { it.hari == hari } ?: emptyList()
                        jadwalHariIni = jadwalHariIniList.size
                    }.onFailure { error ->
                        println("DEBUG KepSek: Error getSchedules - ${error.message}")
                        errorMessage = "Error loading schedules: ${error.message}"
                    }

                    // Get today attendance percentage
                    repository.getTodayAttendance(token).onSuccess { attendanceResponse ->
                        val hadir = attendanceResponse.hadir
                        val total = attendanceResponse.total
                        todayAttendancePercent = if (total > 0) {
                            (hadir * 100 / total)
                        } else 0
                        println("DEBUG KepSek: Attendance loaded - percent: $todayAttendancePercent")
                    }.onFailure { error ->
                        println("DEBUG KepSek: Error getTodayAttendance - ${error.message}")
                    }

                    isLoading = false
                } catch (e: Exception) {
                    errorMessage = "Error: ${e.message}"
                    println("DEBUG KepSek: Exception - ${e.message}")
                    Toast.makeText(context, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                    isLoading = false
                }
            }
        }
    }

    // Polling effect - refresh setiap 5 detik
    LaunchedEffect(Unit) {
        // Load initial data
        loadDashboardData()
        
        // Start polling
        while (true) {
            delay(5000) // Tunggu 5 detik
            loadDashboardData()
        }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF5F5F5))
    ) {
        // Header
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFF1F3A7F))
                    .padding(Spacing.lg)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Selamat Datang,",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.White.copy(alpha = 0.8f)
                        )
                        Text(
                            text = userName,
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Text(
                            text = "Kepala Sekolah",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color(0xFFFFA500)
                        )
                    }

                    Box(
                        modifier = Modifier
                            .size(56.dp)
                            .background(
                                color = Color(0xFFFFA500),
                                shape = RoundedCornerShape(28.dp)
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Icons.Default.Person,
                            contentDescription = "Profile",
                            tint = Color.White,
                            modifier = Modifier.size(32.dp)
                        )
                    }
                }
            }
        }

        if (isLoading) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(300.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = Color(0xFF1F3A7F))
                }
            }
        } else {
            // Show error message if any
            if (errorMessage != null) {
                item {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(Spacing.lg)
                            .padding(bottom = Spacing.md),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = Color(0xFFEF5350).copy(alpha = 0.1f)
                        ),
                        border = androidx.compose.foundation.BorderStroke(
                            1.dp,
                            Color(0xFFEF5350).copy(alpha = 0.3f)
                        ),
                        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(Spacing.md)
                        ) {
                            Text(
                                text = "Warning",
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.SemiBold,
                                color = Color(0xFFEF5350)
                            )
                            Spacer(modifier = Modifier.height(Spacing.sm))
                            Text(
                                text = errorMessage ?: "Error tidak diketahui",
                                style = MaterialTheme.typography.bodySmall,
                                color = Color(0xFFEF5350)
                            )
                        }
                    }
                }
            }

            // Executive Dashboard Card
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(Spacing.lg)
                        .padding(bottom = Spacing.md),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = Color(0xFF1F3A7F)
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(Spacing.lg)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(Spacing.sm)
                            ) {
                                Icon(
                                    Icons.Default.BarChart,
                                    contentDescription = "Dashboard",
                                    tint = Color.White,
                                    modifier = Modifier.size(24.dp)
                                )
                                Text(
                                    text = "Dashboard Executive",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            }

                            Box(
                                modifier = Modifier
                                    .size(24.dp)
                                    .background(
                                        color = Color(0xFF4CAF50),
                                        shape = RoundedCornerShape(12.dp)
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "•",
                                    color = Color.White,
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(Spacing.md))

                        Text(
                            text = getCurrentDay(),
                            style = MaterialTheme.typography.labelSmall,
                            color = Color.White.copy(alpha = 0.7f)
                        )

                        Spacer(modifier = Modifier.height(Spacing.lg))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = "$todayAttendancePercent%",
                                    style = MaterialTheme.typography.displaySmall,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFFFFA500),
                                    fontSize = 48.sp
                                )
                                Text(
                                    text = "Kehadiran Hari Ini",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = Color.White.copy(alpha = 0.7f)
                                )
                            }

                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                AttendanceStatItem("✓", "Hadir", "0", Color(0xFF4CAF50))
                                AttendanceStatItem("✕", "Tidak Hadir", "0", Color(0xFFEF5350))
                                AttendanceStatItem("📋", "Izin", "0", Color(0xFF29B6F6))
                            }
                        }
                    }
                }
            }

            // Stats Cards Grid
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = Spacing.lg),
                    horizontalArrangement = Arrangement.spacedBy(Spacing.md)
                ) {
                    DashStatCard(
                        icon = Icons.Default.Person,
                        label = "Total Guru",
                        value = totalGuru.toString(),
                        color = Color(0xFF7C6EF7),
                        modifier = Modifier.weight(1f)
                    )
                    DashStatCard(
                        icon = Icons.Default.BarChart,
                        label = "Total Kelas",
                        value = totalKelas.toString(),
                        color = Color(0xFF1DC071),
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = Spacing.lg)
                        .padding(top = Spacing.md),
                    horizontalArrangement = Arrangement.spacedBy(Spacing.md)
                ) {
                    DashStatCard(
                        icon = Icons.Default.Schedule,
                        label = "Jadwal Hari Ini",
                        value = jadwalHariIni.toString(),
                        color = Color(0xFFEB3B5A),
                        modifier = Modifier.weight(1f)
                    )
                    DashStatCard(
                        icon = Icons.Default.CalendarMonth,
                        label = "Total Jadwal",
                        value = totalJadwal.toString(),
                        color = Color(0xFF20C0D8),
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            // Performa Bulanan
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(Spacing.lg),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = Color.White
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(Spacing.lg)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(Spacing.sm)
                            ) {
                                Icon(
                                    Icons.AutoMirrored.Filled.TrendingUp,
                                    contentDescription = "Performa",
                                    tint = Color(0xFFDA4C4C),
                                    modifier = Modifier.size(20.dp)
                                )
                                Text(
                                    text = "Performa Bulanan",
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF1F3A7F)
                                )
                            }

                            Surface(
                                color = Color(0xFFD4EDDA),
                                shape = RoundedCornerShape(16.dp)
                            ) {
                                Text(
                                    text = "▲ +33.3%",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = Color(0xFF28A745),
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(Spacing.lg))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(60.dp)
                                        .background(
                                            color = Color.Gray.copy(alpha = 0.2f),
                                            shape = RoundedCornerShape(30.dp)
                                        ),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = "0%",
                                        style = MaterialTheme.typography.labelMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.Gray
                                    )
                                }
                                Text(
                                    text = "Nov",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = Color.Gray,
                                    modifier = Modifier.padding(top = Spacing.sm)
                                )
                            }

                            Icon(
                                Icons.AutoMirrored.Filled.ArrowForward,
                                contentDescription = "Arrow",
                                tint = Color(0xFFFFA500),
                                modifier = Modifier.size(24.dp)
                            )

                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(60.dp)
                                        .background(
                                            color = Color(0xFF1F3A7F),
                                            shape = RoundedCornerShape(30.dp)
                                        ),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = "33%",
                                        style = MaterialTheme.typography.labelMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White
                                    )
                                }
                                Text(
                                    text = "Dec",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = Color.Gray,
                                    modifier = Modifier.padding(top = Spacing.sm)
                                )
                            }
                        }
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(Spacing.lg))
            }
        }
    }
}

@Composable
fun AttendanceStatItem(icon: String, label: String, value: String, color: Color) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.padding(vertical = Spacing.sm)
    ) {
        Text(
            text = icon,
            fontSize = 16.sp
        )
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = Color.White.copy(alpha = 0.7f),
            fontSize = 10.sp
        )
        Text(
            text = value,
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.Bold,
            color = Color.White,
            fontSize = 12.sp
        )
    }
}

@Composable
fun DashStatCard(
    icon: ImageVector,
    label: String,
    value: String,
    color: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .height(120.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = color.copy(alpha = 0.1f)
        ),
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            color.copy(alpha = 0.2f)
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(Spacing.md),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                icon,
                contentDescription = label,
                tint = color,
                modifier = Modifier.size(28.dp)
            )
            Spacer(modifier = Modifier.height(Spacing.sm))
            Text(
                text = value,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = color,
                fontSize = 24.sp
            )
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                color = Color.Gray,
                fontSize = 10.sp
            )
        }
    }
}

package com.kelasxi.aplikasimonitoringkelas.ui.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
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

@Composable
fun DashboardKurikulumPage() {
    val context = LocalContext.current
    val sharedPrefManager = remember { SharedPrefManager.getInstance(context) }
    val token = sharedPrefManager.getToken()
    val repository = remember { AppRepositoryNew(RetrofitClient.apiService) }
    val scope = rememberCoroutineScope()

    var totalGuru by remember { mutableStateOf(0) }
    var totalJadwal by remember { mutableStateOf(0) }
    var totalKelas by remember { mutableStateOf(0) }
    var kehadiranHariIni by remember { mutableStateOf(0) }
    var guruPenggantiAktif by remember { mutableStateOf(0) }
    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    // Load dashboard data
    LaunchedEffect(Unit) {
        if (token != null) {
            scope.launch {
                try {
                    errorMessage = null
                    
                    // Get teachers count
                    repository.getTeachers(token).onSuccess { teachersResponse ->
                        totalGuru = teachersResponse.data?.size ?: 0
                    }.onFailure { error ->
                        println("DEBUG Dashboard: Error getTeachers - ${error.message}")
                    }
                    
                    // Get schedules count
                    repository.getSchedules(token).onSuccess { schedulesResponse ->
                        totalJadwal = schedulesResponse.data?.size ?: 0
                        
                        // Get unique classes from schedules
                        val uniqueClasses = schedulesResponse.data?.map { it.kelas }?.distinct() ?: emptyList()
                        totalKelas = uniqueClasses.size
                        println("DEBUG Dashboard: Schedules loaded - totalJadwal: $totalJadwal, totalKelas: $totalKelas")
                    }.onFailure { error ->
                        println("DEBUG Dashboard: Error getSchedules - ${error.message}")
                    }
                    
                    // Get today attendance count - gunakan data dari response
                    repository.getTodayAttendance(token).onSuccess { attendanceResponse ->
                        kehadiranHariIni = attendanceResponse.hadir
                        println("DEBUG Dashboard: Attendance loaded - hadir: $kehadiranHariIni")
                    }.onFailure { error ->
                        println("DEBUG Dashboard: Error getTodayAttendance - ${error.message}")
                        errorMessage = "Error loading attendance: ${error.message}"
                    }
                    
                    // Get active substitutes count
                    repository.getGuruPengganti(token).onSuccess { guruPenggantiResponse ->
                        guruPenggantiAktif = guruPenggantiResponse.data?.size ?: 0
                    }.onFailure { error ->
                        println("DEBUG Dashboard: Error getGuruPengganti - ${error.message}")
                    }
                    
                    isLoading = false
                } catch (e: Exception) {
                    errorMessage = "Error: ${e.message}"
                    println("DEBUG Dashboard: Exception - ${e.message}")
                    Toast.makeText(context, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                    isLoading = false
                }
            }
        }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        SMKPrimary.copy(alpha = 0.02f),
                        SMKSurface,
                        SMKAccent.copy(alpha = 0.01f)
                    )
                )
            )
            .padding(Spacing.lg)
    ) {
        item {
            Text(
                text = "Ringkasan",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = Spacing.md)
            )
        }

        // Show error message if any
        if (errorMessage != null) {
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = Spacing.lg),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = Color(0xFFEF5350).copy(alpha = 0.1f)
                    ),
                    border = androidx.compose.foundation.BorderStroke(
                        1.dp,
                        Color(0xFFEF5350).copy(alpha = 0.3f)
                    )
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

        // Stats Cards Grid
        item {
            Column(
                verticalArrangement = Arrangement.spacedBy(Spacing.md)
            ) {
                // Row 1: Guru dan Jadwal
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(Spacing.md)
                ) {
                    StatCard(
                        icon = Icons.Default.Person,
                        title = "Total Guru",
                        value = totalGuru.toString(),
                        modifier = Modifier.weight(1f),
                        color = SMKPrimary
                    )
                    StatCard(
                        icon = Icons.Default.Schedule,
                        title = "Total Jadwal",
                        value = totalJadwal.toString(),
                        modifier = Modifier.weight(1f),
                        color = SMKSecondary
                    )
                }

                // Row 2: Kelas dan Kehadiran
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(Spacing.md)
                ) {
                    StatCard(
                        icon = Icons.Default.Class,
                        title = "Total Kelas",
                        value = totalKelas.toString(),
                        modifier = Modifier.weight(1f),
                        color = SMKAccent
                    )
                    StatCard(
                        icon = Icons.Default.CheckCircle,
                        title = "Kehadiran Hari Ini",
                        value = kehadiranHariIni.toString(),
                        modifier = Modifier.weight(1f),
                        color = Color(0xFF4CAF50)
                    )
                }

                // Row 3: Guru Pengganti
                StatCard(
                    icon = Icons.Default.PersonAdd,
                    title = "Guru Pengganti Aktif",
                    value = guruPenggantiAktif.toString(),
                    modifier = Modifier.fillMaxWidth(),
                    color = Color(0xFFFF9800)
                )
            }
        }

        item {
            Spacer(modifier = Modifier.height(Spacing.lg))
        }
    }

    if (isLoading) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator(color = SMKPrimary)
        }
    }
}

@Composable
fun StatCard(
    icon: ImageVector? = null,
    title: String,
    value: String,
    modifier: Modifier = Modifier,
    color: androidx.compose.ui.graphics.Color = SMKPrimary
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .height(120.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = SMKSurface,
            contentColor = SMKOnSurface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            color.copy(alpha = 0.2f)
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(Spacing.md),
            horizontalAlignment = Alignment.Start,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.bodyMedium,
                    color = NeutralGray600,
                    fontSize = 12.sp
                )
                if (icon != null) {
                    Icon(
                        imageVector = icon,
                        contentDescription = title,
                        tint = color,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
            Text(
                text = value,
                style = MaterialTheme.typography.displaySmall,
                fontWeight = FontWeight.Bold,
                color = color,
                fontSize = 28.sp
            )
        }
    }
}

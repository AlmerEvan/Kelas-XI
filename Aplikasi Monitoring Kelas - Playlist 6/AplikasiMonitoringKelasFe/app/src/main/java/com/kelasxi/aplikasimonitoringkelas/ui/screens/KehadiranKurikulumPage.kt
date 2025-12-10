package com.kelasxi.aplikasimonitoringkelas.ui.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
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
import com.kelasxi.aplikasimonitoringkelas.data.model.TeacherAttendance
import com.kelasxi.aplikasimonitoringkelas.data.repository.AppRepositoryNew
import com.kelasxi.aplikasimonitoringkelas.ui.theme.*
import com.kelasxi.aplikasimonitoringkelas.utils.SharedPrefManager
import kotlinx.coroutines.launch
import kotlinx.coroutines.delay
import java.time.LocalDate

@Composable
fun KehadiranKurikulumPage() {
    val context = LocalContext.current
    val sharedPrefManager = remember { SharedPrefManager.getInstance(context) }
    val token = sharedPrefManager.getToken()
    val repository = remember { AppRepositoryNew(RetrofitClient.apiService) }
    val scope = rememberCoroutineScope()

    var attendanceList by remember { mutableStateOf<List<TeacherAttendance>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    // Stats dari response
    var hadirCount by remember { mutableStateOf(0) }
    var terlambatCount by remember { mutableStateOf(0) }
    var tidakHadirCount by remember { mutableStateOf(0) }

    // Function to load attendance data
    fun loadAttendanceData() {
        if (token != null) {
            scope.launch {
                try {
                    isLoading = true
                    errorMessage = null
                    
                    // Load kehadiran hari ini
                    repository.getTodayAttendance(token).onSuccess { response ->
                        attendanceList = response.data ?: emptyList()
                        
                        // Gunakan stats dari response backend
                        hadirCount = response.hadir
                        terlambatCount = response.telat
                        tidakHadirCount = response.tidakHadir
                        
                        println("DEBUG: Attendance loaded - Hadir: $hadirCount, Telat: $terlambatCount, Tidak Hadir: $tidakHadirCount")
                        
                        isLoading = false
                    }.onFailure { error ->
                        errorMessage = error.message ?: "Error tidak diketahui"
                        println("DEBUG: Error loading attendance - ${error.message}")
                        isLoading = false
                    }
                } catch (e: Exception) {
                    errorMessage = e.message ?: "Error tidak diketahui"
                    println("DEBUG: Exception loading attendance - ${e.message}")
                    isLoading = false
                }
            }
        } else {
            errorMessage = "Token tidak ditemukan"
            isLoading = false
        }
    }

    // Polling effect - refresh setiap 5 detik dengan proper cleanup
    LaunchedEffect(Unit) {
        // Load initial data
        loadAttendanceData()
        
        // Start polling dengan pemberhentian proper
        try {
            while (true) {
                delay(5000) // Tunggu 5 detik
                loadAttendanceData()
            }
        } catch (e: Exception) {
            // Handle cancellation gracefully
        }
    }

    if (isLoading) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator(color = SMKPrimary)
        }
        return
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        SMKPrimary.copy(alpha = 0.02f),
                        SMKSurface
                    )
                )
            )
            .padding(Spacing.lg)
    ) {
        item {
            Text(
                text = "Kehadiran Hari Ini",
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
                            text = "Error",
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

        // Stats Row
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = Spacing.lg),
                horizontalArrangement = Arrangement.spacedBy(Spacing.md)
            ) {
                StatBadge(
                    label = "Hadir",
                    count = hadirCount,
                    color = Color(0xFF4CAF50),
                    modifier = Modifier.weight(1f)
                )
                StatBadge(
                    label = "Terlambat",
                    count = terlambatCount,
                    color = Color(0xFFFF9800),
                    modifier = Modifier.weight(1f)
                )
                StatBadge(
                    label = "Tidak Hadir",
                    count = tidakHadirCount,
                    color = Color(0xFFEF5350),
                    modifier = Modifier.weight(1f)
                )
                StatBadge(
                    label = "Izin",
                    count = 0,
                    color = Color(0xFF29B6F6),
                    modifier = Modifier.weight(1f)
                )
            }
        }

        // Attendance List
        if (attendanceList.isEmpty()) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            Icons.Default.NotInterested,
                            contentDescription = "No Data",
                            tint = NeutralGray400,
                            modifier = Modifier.size(48.dp)
                        )
                        Spacer(modifier = Modifier.height(Spacing.md))
                        Text(
                            text = "Belum ada data kehadiran",
                            style = MaterialTheme.typography.bodyMedium,
                            color = NeutralGray500
                        )
                    }
                }
            }
        } else {
            items(attendanceList) { attendance ->
                AttendanceCard(attendance)
                Spacer(modifier = Modifier.height(Spacing.md))
            }
        }
    }
}

@Composable
fun StatBadge(
    label: String,
    count: Int,
    color: androidx.compose.ui.graphics.Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .height(100.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = color.copy(alpha = 0.1f)
        ),
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            color.copy(alpha = 0.3f)
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(Spacing.md),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = count.toString(),
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = color,
                fontSize = 24.sp
            )
            Text(
                text = label,
                style = MaterialTheme.typography.bodySmall,
                color = NeutralGray600,
                fontSize = 11.sp
            )
        }
    }
}

@Composable
fun AttendanceCard(attendance: TeacherAttendance) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = SMKSurface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(Spacing.md)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = attendance.guru?.name ?: "Guru",
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.SemiBold,
                        color = SMKOnSurface
                    )
                    Text(
                        text = attendance.schedule?.mata_pelajaran ?: "-",
                        style = MaterialTheme.typography.bodySmall,
                        color = NeutralGray600
                    )
                }
                
                StatusBadge(status = attendance.status ?: "unknown")
            }
            
            Spacer(modifier = Modifier.height(Spacing.sm))
            
            Text(
                text = attendance.keterangan ?: "",
                style = MaterialTheme.typography.bodySmall,
                color = NeutralGray500
            )
        }
    }
}

@Composable
fun StatusBadge(status: String) {
    val (bgColor, textColor, label) = when (status) {
        "hadir" -> Triple(Color(0xFF4CAF50), Color.White, "Hadir")
        "telat" -> Triple(Color(0xFFFF9800), Color.White, "Terlambat")
        "tidak_hadir" -> Triple(Color(0xFFEF5350), Color.White, "Tidak Hadir")
        else -> Triple(NeutralGray300, NeutralGray600, "Unknown")
    }
    
    Surface(
        color = bgColor,
        shape = RoundedCornerShape(20.dp)
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = textColor,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
            fontWeight = FontWeight.Medium,
            fontSize = 10.sp
        )
    }
}

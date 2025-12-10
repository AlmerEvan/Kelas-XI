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
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kelasxi.aplikasimonitoringkelas.data.api.RetrofitClient
import com.kelasxi.aplikasimonitoringkelas.data.repository.AppRepositoryNew
import com.kelasxi.aplikasimonitoringkelas.ui.theme.Spacing
import com.kelasxi.aplikasimonitoringkelas.utils.SharedPrefManager
import kotlinx.coroutines.launch

data class BestTeacher(
    val name: String,
    val nip: String,
    val subject: String,
    val attendanceCount: Int,
    val attendancePercentage: Double
)

@Composable
fun KepalaSekolahLaporanScreen() {
    val context = LocalContext.current
    val sharedPrefManager = remember { SharedPrefManager.getInstance(context) }
    val token = sharedPrefManager.getToken()
    val repository = remember { AppRepositoryNew(RetrofitClient.apiService) }
    val scope = rememberCoroutineScope()

    var isLoading by remember { mutableStateOf(true) }
    var totalRecords by remember { mutableStateOf(0) }
    var totalHadir by remember { mutableStateOf(0) }
    var totalTidakHadir by remember { mutableStateOf(0) }
    var totalIzin by remember { mutableStateOf(0) }
    var attendancePercentage by remember { mutableStateOf(0.0) }
    var bestTeachers by remember { mutableStateOf<List<BestTeacher>>(emptyList()) }

    LaunchedEffect(Unit) {
        if (token != null) {
            scope.launch {
                try {
                    // Load attendance statistics
                    repository.getTodayAttendance(token).onSuccess { attendanceResponse ->
                        val attendanceList = attendanceResponse.data ?: emptyList()
                        totalRecords = attendanceList.size
                        totalHadir = attendanceList.count { it.status == "masuk" }
                        totalTidakHadir = attendanceList.count { it.status == "tidak_masuk" }
                        totalIzin = attendanceList.count { it.status == "izin" }

                        if (totalRecords > 0) {
                            attendancePercentage = (totalHadir.toDouble() / totalRecords) * 100
                        }

                        // Get teachers to build best teacher list
                        repository.getTeachers(token).onSuccess { teachersResponse ->
                            val teachers = teachersResponse.data ?: emptyList()
                            val best = teachers.mapNotNull { teacher ->
                                val teacherAttendance = attendanceList.filter {
                                    it.guru?.id == teacher.id
                                }
                                if (teacherAttendance.isNotEmpty()) {
                                    val hadir = teacherAttendance.count { it.status == "masuk" }
                                    val percentage =
                                        (hadir.toDouble() / teacherAttendance.size) * 100
                                    BestTeacher(
                                        name = teacher.name ?: "Unknown",
                                        nip = teacher.id.toString(),
                                        subject = teacher.mata_pelajaran ?: "Unknown",
                                        attendanceCount = hadir,
                                        attendancePercentage = percentage
                                    )
                                } else {
                                    null
                                }
                            }.sortedByDescending { it.attendancePercentage }.take(3)

                            bestTeachers = best
                        }
                    }
                    isLoading = false
                } catch (e: Exception) {
                    Toast.makeText(context, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                    isLoading = false
                }
            }
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
                Column {
                    Text(
                        text = "Laporan Bulanan",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Text(
                        text = "Desember 2025",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.White.copy(alpha = 0.8f),
                        modifier = Modifier.padding(top = Spacing.xs)
                    )
                }
            }
        }

        // Statistics Grid
        item {
            Column(
                modifier = Modifier.padding(Spacing.lg)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(Spacing.md)
                ) {
                    LaporanStatCard(
                        icon = Icons.Default.Assessment,
                        title = "Total Record",
                        value = totalRecords.toString(),
                        color = Color(0xFF7C6EF7),
                        modifier = Modifier.weight(1f)
                    )
                    LaporanStatCard(
                        icon = Icons.Default.CheckCircle,
                        title = "Hadir",
                        value = totalHadir.toString(),
                        color = Color(0xFF4CAF50),
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(modifier = Modifier.height(Spacing.md))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(Spacing.md)
                ) {
                    LaporanStatCard(
                        icon = Icons.Default.Cancel,
                        title = "Tidak Hadir",
                        value = totalTidakHadir.toString(),
                        color = Color(0xFFEF5350),
                        modifier = Modifier.weight(1f)
                    )
                    LaporanStatCard(
                        icon = Icons.AutoMirrored.Filled.EventNote,
                        title = "Izin",
                        value = totalIzin.toString(),
                        color = Color(0xFF29B6F6),
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }

        // Attendance Percentage
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = Spacing.lg)
                    .padding(bottom = Spacing.md),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(Spacing.lg),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Persentase Kehadiran",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1F3A7F),
                        modifier = Modifier.padding(bottom = Spacing.md)
                    )

                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier.size(120.dp)
                    ) {
                        CircularProgressIndicator(
                            progress = { (attendancePercentage / 100).toFloat() },
                            modifier = Modifier
                                .size(120.dp)
                                .align(Alignment.Center),
                            color = Color(0xFF1F3A7F),
                            strokeWidth = 8.dp
                        )
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "${attendancePercentage.toInt()}%",
                                style = MaterialTheme.typography.headlineSmall,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF1F3A7F),
                                fontSize = 24.sp
                            )
                            Text(
                                text = "Kehadiran",
                                style = MaterialTheme.typography.labelSmall,
                                color = Color.Gray,
                                fontSize = 10.sp
                            )
                        }
                    }

                    LinearProgressIndicator(
                        progress = { (attendancePercentage / 100).toFloat() },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(6.dp)
                            .padding(top = Spacing.lg),
                        color = Color(0xFF1DC071),
                        trackColor = Color.Gray.copy(alpha = 0.2f)
                    )
                }
            }
        }

        // Guru Terbaik Section
        item {
            Text(
                text = "Guru Terbaik Bulan Ini",
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF1F3A7F),
                modifier = Modifier
                    .padding(horizontal = Spacing.lg)
                    .padding(top = Spacing.md, bottom = Spacing.sm)
            )
        }

        if (isLoading) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(Spacing.lg),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(
                        color = Color(0xFF1F3A7F)
                    )
                }
            }
        } else {
            items(bestTeachers) { teacher ->
                BestTeacherCard(teacher = teacher)
            }
        }

        item {
            Spacer(modifier = Modifier.height(Spacing.lg))
        }
    }
}

@Composable
fun LaporanStatCard(
    icon: ImageVector,
    title: String,
    value: String,
    color: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.height(100.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(Spacing.md),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = title,
                modifier = Modifier.size(24.dp),
                tint = color
            )
            Text(
                text = value,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Bold,
                color = color,
                modifier = Modifier.padding(top = Spacing.xs)
            )
            Text(
                text = title,
                style = MaterialTheme.typography.labelSmall,
                color = Color.Gray,
                fontSize = 10.sp
            )
        }
    }
}

@Composable
fun BestTeacherCard(teacher: BestTeacher) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = Spacing.lg)
            .padding(bottom = Spacing.md),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(Spacing.md),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = teacher.name,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1F3A7F)
                )
                Text(
                    text = teacher.nip,
                    style = MaterialTheme.typography.labelSmall,
                    color = Color.Gray,
                    fontSize = 10.sp
                )
                Text(
                    text = teacher.subject,
                    style = MaterialTheme.typography.labelSmall,
                    color = Color.Gray,
                    fontSize = 10.sp
                )
                Row(
                    modifier = Modifier.padding(top = Spacing.sm),
                    horizontalArrangement = Arrangement.spacedBy(Spacing.xs)
                ) {
                    Surface(
                        color = Color(0xFF4CAF50).copy(alpha = 0.1f),
                        shape = RoundedCornerShape(4.dp)
                    ) {
                        Text(
                            text = "✓ ${teacher.attendanceCount}",
                            style = MaterialTheme.typography.labelSmall,
                            color = Color(0xFF4CAF50),
                            fontWeight = FontWeight.Bold,
                            fontSize = 9.sp,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }
            }

            // Percentage Display
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "${teacher.attendancePercentage.toInt()}%",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1DC071),
                    fontSize = 16.sp
                )
                Text(
                    text = "Kehadiran",
                    style = MaterialTheme.typography.labelSmall,
                    color = Color.Gray,
                    fontSize = 8.sp
                )
            }
        }
    }
}

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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kelasxi.aplikasimonitoringkelas.data.api.RetrofitClient
import com.kelasxi.aplikasimonitoringkelas.data.model.Teacher
import com.kelasxi.aplikasimonitoringkelas.data.repository.AppRepositoryNew
import com.kelasxi.aplikasimonitoringkelas.ui.theme.*
import com.kelasxi.aplikasimonitoringkelas.utils.SharedPrefManager
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter

data class AttendanceReport(
    val teacherId: Int,
    val teacherName: String,
    val totalAbsent: Int = 0,
    val totalPresent: Int = 0,
    val totalExcused: Int = 0,
    val attendancePercentage: Float = 0f
)

@Composable
fun LaporanKehadiranKurikulumPage() {
    val context = LocalContext.current
    val sharedPrefManager = remember { SharedPrefManager.getInstance(context) }
    val token = sharedPrefManager.getToken()
    val repository = remember { AppRepositoryNew(RetrofitClient.apiService) }
    val scope = rememberCoroutineScope()

    var reportList by remember { mutableStateOf<List<AttendanceReport>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var selectedTeacher by remember { mutableStateOf<String>("Semua") }
    
    // Date range
    var startDate by remember { mutableStateOf(LocalDate.now().minusDays(30)) }
    var endDate by remember { mutableStateOf(LocalDate.now()) }

    LaunchedEffect(Unit) {
        if (token != null) {
            scope.launch {
                try {
                    isLoading = true
                    errorMessage = null
                    
                    // Load attendance statistics for date range
                    repository.getAttendanceStatistics(
                        token,
                        startDate.toString(),
                        endDate.toString()
                    ).onSuccess { stat ->
                        // Convert to AttendanceReport format dengan data dari response
                        reportList = listOf(AttendanceReport(
                            teacherId = 0,
                            teacherName = "Ringkasan Kehadiran",
                            totalAbsent = stat.tidakHadir,
                            totalPresent = stat.hadir,
                            totalExcused = stat.telat,
                            attendancePercentage = stat.percentageHadir.toFloat()
                        ))
                        isLoading = false
                    }.onFailure { error ->
                        errorMessage = error.message ?: "Error tidak diketahui"
                        reportList = emptyList()
                        isLoading = false
                    }
                } catch (e: Exception) {
                    errorMessage = e.message ?: "Error tidak diketahui"
                    reportList = emptyList()
                    isLoading = false
                }
            }
        } else {
            errorMessage = "Token tidak ditemukan"
            isLoading = false
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
                text = "Laporan Kehadiran",
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

        // Date Range Card
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = Spacing.lg),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = SMKPrimary.copy(alpha = 0.05f)),
                border = androidx.compose.foundation.BorderStroke(1.dp, SMKPrimary.copy(alpha = 0.2f))
            ) {
                Column(
                    modifier = Modifier.padding(Spacing.md)
                ) {
                    Text(
                        text = "Periode Laporan",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = SMKPrimary
                    )
                    
                    Spacer(modifier = Modifier.height(Spacing.sm))
                    
                    Text(
                        text = "${formatDate(startDate)} - ${formatDate(endDate)}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = SMKOnSurface,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }

        // Summary Statistics
        item {
            if (reportList.isNotEmpty()) {
                val totalPresent = reportList.sumOf { it.totalPresent }
                val totalAbsent = reportList.sumOf { it.totalAbsent }
                val totalExcused = reportList.sumOf { it.totalExcused }
                val totalDays = totalPresent + totalAbsent + totalExcused
                val avgAttendance = if (totalDays > 0) 
                    (totalPresent.toFloat() / totalDays * 100).toInt() 
                else 0

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = Spacing.lg),
                    horizontalArrangement = Arrangement.spacedBy(Spacing.md)
                ) {
                    SummaryCard(
                        label = "Hadir",
                        value = totalPresent,
                        color = Color(0xFF4CAF50),
                        modifier = Modifier.weight(1f)
                    )
                    SummaryCard(
                        label = "Tidak Hadir",
                        value = totalAbsent,
                        color = Color(0xFFEF5350),
                        modifier = Modifier.weight(1f)
                    )
                    SummaryCard(
                        label = "Izin",
                        value = totalExcused,
                        color = Color(0xFF29B6F6),
                        modifier = Modifier.weight(1f)
                    )
                }

                // Average Attendance
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = Spacing.lg),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = SMKSecondary.copy(alpha = 0.1f)
                    ),
                    border = androidx.compose.foundation.BorderStroke(
                        1.dp,
                        SMKSecondary.copy(alpha = 0.3f)
                    )
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(Spacing.md),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "Rata-rata Kehadiran",
                                style = MaterialTheme.typography.bodyMedium,
                                color = NeutralGray600
                            )
                            Text(
                                text = "$avgAttendance%",
                                style = MaterialTheme.typography.headlineMedium,
                                fontWeight = FontWeight.Bold,
                                color = SMKSecondary
                            )
                        }
                        
                        Box(
                            modifier = Modifier
                                .size(80.dp)
                                .background(
                                    color = SMKSecondary.copy(alpha = 0.2f),
                                    shape = RoundedCornerShape(40.dp)
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator(
                                progress = { avgAttendance / 100f },
                                modifier = Modifier.size(80.dp),
                                color = SMKSecondary,
                                trackColor = SMKSecondary.copy(alpha = 0.1f),
                                strokeWidth = 6.dp
                            )
                            Text(
                                text = "$avgAttendance%",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = SMKSecondary,
                                fontSize = 12.sp
                            )
                        }
                    }
                }
            }
        }

        // Teacher List
        if (reportList.isEmpty()) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(250.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            Icons.Default.BarChart,
                            contentDescription = "No Report",
                            tint = NeutralGray400,
                            modifier = Modifier.size(48.dp)
                        )
                        Spacer(modifier = Modifier.height(Spacing.md))
                        Text(
                            text = if (errorMessage != null) "Terjadi kesalahan" else "Belum ada laporan kehadiran",
                            style = MaterialTheme.typography.bodyMedium,
                            color = NeutralGray500
                        )
                    }
                }
            }
        } else {
            items(reportList) { report ->
                AttendanceReportCard(report)
                Spacer(modifier = Modifier.height(Spacing.md))
            }
        }
    }
}

@Composable
fun SummaryCard(
    label: String,
    value: Int,
    color: androidx.compose.ui.graphics.Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .height(90.dp),
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
                text = value.toString(),
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = color,
                fontSize = 22.sp
            )
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                color = NeutralGray600,
                fontSize = 10.sp
            )
        }
    }
}

@Composable
fun AttendanceReportCard(report: AttendanceReport) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = SMKSurface),
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
                        text = report.teacherName,
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.SemiBold,
                        color = SMKOnSurface
                    )
                    Text(
                        text = "Kehadiran: ${report.attendancePercentage.toInt()}%",
                        style = MaterialTheme.typography.bodySmall,
                        color = NeutralGray600
                    )
                }
                
                Box(
                    modifier = Modifier
                        .size(60.dp)
                        .background(
                            color = SMKPrimary.copy(alpha = 0.1f),
                            shape = RoundedCornerShape(30.dp)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "${report.attendancePercentage.toInt()}%",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = SMKPrimary,
                        fontSize = 13.sp
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(Spacing.md))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(Spacing.sm)
            ) {
                DetailBadge(
                    icon = Icons.Default.CheckCircle,
                    label = "Hadir",
                    value = report.totalPresent,
                    color = Color(0xFF4CAF50),
                    modifier = Modifier.weight(1f)
                )
                DetailBadge(
                    icon = Icons.Default.Cancel,
                    label = "Tidak Hadir",
                    value = report.totalAbsent,
                    color = Color(0xFFEF5350),
                    modifier = Modifier.weight(1f)
                )
                DetailBadge(
                    icon = Icons.Default.Info,
                    label = "Izin",
                    value = report.totalExcused,
                    color = Color(0xFF29B6F6),
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
fun DetailBadge(
    icon: ImageVector,
    label: String,
    value: Int,
    color: androidx.compose.ui.graphics.Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.height(60.dp),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(
            containerColor = color.copy(alpha = 0.05f)
        ),
        border = androidx.compose.foundation.BorderStroke(
            0.5.dp,
            color.copy(alpha = 0.2f)
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(Spacing.sm),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = value.toString(),
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold,
                color = color,
                fontSize = 12.sp
            )
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                color = NeutralGray600,
                fontSize = 8.sp
            )
        }
    }
}

fun formatDate(date: LocalDate): String {
    val formatter = DateTimeFormatter.ofPattern("dd MMM yyyy")
    return date.format(formatter)
}

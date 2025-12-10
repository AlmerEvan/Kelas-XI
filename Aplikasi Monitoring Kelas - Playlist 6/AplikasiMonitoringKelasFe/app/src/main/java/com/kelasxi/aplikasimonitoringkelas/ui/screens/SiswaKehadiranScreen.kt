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
import com.kelasxi.aplikasimonitoringkelas.data.model.TeacherAttendance
import com.kelasxi.aplikasimonitoringkelas.data.repository.AppRepositoryNew
import com.kelasxi.aplikasimonitoringkelas.ui.theme.*
import com.kelasxi.aplikasimonitoringkelas.utils.SharedPrefManager
import kotlinx.coroutines.launch
import java.time.LocalDate

@Composable
fun SiswaKehadiranScreen() {
    val context = LocalContext.current
    val sharedPrefManager = remember { SharedPrefManager.getInstance(context) }
    val token = sharedPrefManager.getToken()
    val repository = remember { AppRepositoryNew(RetrofitClient.apiService) }
    val scope = rememberCoroutineScope()

    var attendanceList by remember { mutableStateOf<List<TeacherAttendance>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var masukCount by remember { mutableStateOf(0) }
    var terlambatCount by remember { mutableStateOf(0) }
    var tidakMasukCount by remember { mutableStateOf(0) }
    
    var showInputDialog by remember { mutableStateOf(false) }
    var selectedAttendance by remember { mutableStateOf<TeacherAttendance?>(null) }

    LaunchedEffect(Unit) {
        if (token != null) {
            scope.launch {
                try {
                    repository.getTodayAttendance(token).onSuccess { response ->
                        attendanceList = response.data ?: emptyList()
                        
                        masukCount = attendanceList.count { it.status == "masuk" }
                        terlambatCount = attendanceList.count { it.status == "izin" }
                        tidakMasukCount = attendanceList.count { it.status == "tidak_masuk" }
                    }
                    isLoading = false
                } catch (e: Exception) {
                    Toast.makeText(context, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                    isLoading = false
                }
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF7B68EE),
                        Color.Black.copy(alpha = 0.5f)
                    )
                )
            )
    ) {
        // Header
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            Color(0xFF7B68EE),
                            Color(0xFF6A5ACD)
                        )
                    )
                )
                .padding(Spacing.lg)
        ) {
            Column {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(Spacing.sm)
                ) {
                    Icon(
                        Icons.Default.CheckCircle,
                        contentDescription = "Kehadiran",
                        tint = Color.White,
                        modifier = Modifier.size(28.dp)
                    )
                    Text(
                        text = "Kehadiran Guru",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
                
                Spacer(modifier = Modifier.height(Spacing.sm))
                
                Text(
                    text = getCurrentDateIndonesian(),
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.White.copy(alpha = 0.8f)
                )
            }
        }

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(Spacing.lg),
            verticalArrangement = Arrangement.spacedBy(Spacing.md)
        ) {
            // Stat Cards
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(Spacing.sm)
                ) {
                    AttendanceStatCard(
                        label = "Hadir",
                        count = masukCount,
                        color = Color(0xFF4CAF50),
                        icon = Icons.Default.Done,
                        modifier = Modifier.weight(1f)
                    )
                    AttendanceStatCard(
                        label = "Terlambat",
                        count = terlambatCount,
                        color = Color(0xFFFF9800),
                        icon = Icons.Default.Warning,
                        modifier = Modifier.weight(1f)
                    )
                    AttendanceStatCard(
                        label = "Tidak Hadir",
                        count = tidakMasukCount,
                        color = Color(0xFFEF5350),
                        icon = Icons.Default.Close,
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            // Daftar Guru Section
            item {
                Text(
                    text = "Daftar Guru",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }

            if (isLoading) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(color = Color.White)
                    }
                }
            } else if (attendanceList.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(150.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Tidak ada data kehadiran",
                            color = Color.White.copy(alpha = 0.7f)
                        )
                    }
                }
            } else {
                items(attendanceList) { attendance ->
                    AttendanceItemCard(
                        attendance = attendance,
                        onInputClick = {
                            selectedAttendance = attendance
                            showInputDialog = true
                        }
                    )
                }
            }
        }
    }

    // Input Dialog
    if (showInputDialog && selectedAttendance != null) {
        AttendanceInputDialog(
            attendance = selectedAttendance!!,
            onDismiss = { showInputDialog = false },
            onSubmit = { status, keterangan ->
                scope.launch {
                    try {
                        // Update attendance in backend
                        // TODO: Implement API call to update attendance
                        Toast.makeText(context, "Kehadiran diupdate: $status", Toast.LENGTH_SHORT).show()
                        showInputDialog = false
                    } catch (e: Exception) {
                        Toast.makeText(context, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        )
    }
}

@Composable
fun AttendanceStatCard(
    label: String,
    count: Int,
    color: Color,
    icon: ImageVector,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .height(100.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = color.copy(alpha = 0.15f)),
        border = androidx.compose.foundation.BorderStroke(1.dp, color.copy(alpha = 0.3f))
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
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.height(Spacing.sm))
            Text(
                text = count.toString(),
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = color,
                fontSize = 24.sp
            )
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                color = color.copy(alpha = 0.8f)
            )
        }
    }
}

@Composable
fun AttendanceItemCard(
    attendance: TeacherAttendance,
    onInputClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF2A2A2A)),
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
                    text = attendance.guru?.name ?: "Guru",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Text(
                    text = attendance.schedule?.mata_pelajaran ?: "-",
                    style = MaterialTheme.typography.labelSmall,
                    color = Color.White.copy(alpha = 0.7f)
                )
            }

            if (attendance.status == "tidak_masuk") {
                Button(
                    onClick = onInputClick,
                    modifier = Modifier.height(36.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF6A5ACD)
                    ),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text("Belum Input", fontSize = 10.sp)
                }
            } else {
                AttendanceStatusBadge(status = attendance.status ?: "unknown")
            }
        }
    }
}

@Composable
fun AttendanceStatusBadge(status: String) {
    val (bgColor, label) = when (status) {
        "masuk" -> Pair(Color(0xFF4CAF50), "Hadir")
        "terlambat" -> Pair(Color(0xFFFF9800), "Terlambat")
        "tidak_masuk" -> Pair(Color(0xFFEF5350), "Tidak Hadir")
        else -> Pair(Color.Gray, "Unknown")
    }
    
    Surface(
        color = bgColor.copy(alpha = 0.2f),
        shape = RoundedCornerShape(20.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, bgColor)
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = bgColor,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
            fontWeight = FontWeight.Medium,
            fontSize = 10.sp
        )
    }
}

@Composable
fun AttendanceInputDialog(
    attendance: TeacherAttendance,
    onDismiss: () -> Unit,
    onSubmit: (String, String) -> Unit
) {
    var selectedStatus by remember { mutableStateOf("masuk") }
    var keterangan by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Column {
                Text(
                    text = "Input Kehadiran",
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = attendance.guru?.name ?: "Guru",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Gray
                )
            }
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(Spacing.md)
            ) {
                Text(
                    text = "Status Kehadiran:",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.SemiBold
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(Spacing.sm)
                ) {
                    listOf(
                        "masuk" to "Hadir",
                        "terlambat" to "Terlambat",
                        "tidak_masuk" to "Tidak Hadir",
                        "izin" to "Izin"
                    ).forEach { (status, label) ->
                        Button(
                            onClick = { selectedStatus = status },
                            modifier = Modifier
                                .weight(1f)
                                .height(36.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (selectedStatus == status)
                                    Color(0xFF6A5ACD)
                                else
                                    Color.Gray.copy(alpha = 0.3f)
                            ),
                            shape = RoundedCornerShape(6.dp)
                        ) {
                            Text(label, fontSize = 9.sp)
                        }
                    }
                }

                OutlinedTextField(
                    value = keterangan,
                    onValueChange = { keterangan = it },
                    label = { Text("Keterangan (opsional)") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 2,
                    maxLines = 3
                )
            }
        },
        confirmButton = {
            Button(
                onClick = { onSubmit(selectedStatus, keterangan) },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6A5ACD))
            ) {
                Text("Simpan")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Batal")
            }
        }
    )
}

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
import com.kelasxi.aplikasimonitoringkelas.data.model.Schedule
import com.kelasxi.aplikasimonitoringkelas.data.repository.AppRepositoryNew
import com.kelasxi.aplikasimonitoringkelas.ui.theme.*
import com.kelasxi.aplikasimonitoringkelas.utils.SharedPrefManager
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

@Composable
fun SiswaHomeScreen() {
    val context = LocalContext.current
    val sharedPrefManager = remember { SharedPrefManager.getInstance(context) }
    val token = sharedPrefManager.getToken()
    val repository = remember { AppRepositoryNew(RetrofitClient.apiService) }
    val scope = rememberCoroutineScope()

    var userName by remember { mutableStateOf("") }
    var kelas by remember { mutableStateOf("") }
    var scheduleList by remember { mutableStateOf<List<Schedule>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }

    LaunchedEffect(Unit) {
        if (token != null) {
            scope.launch {
                try {
                    // Get user info from prefs
                    userName = sharedPrefManager.getUserName() ?: "Siswa"
                    kelas = sharedPrefManager.getKelas() ?: ""
                    
                    // Get today's schedule
                    val hari = getCurrentDay()
                    if (kelas.isNotEmpty()) {
                        repository.getSchedules(token, hari, kelas).onSuccess { response ->
                            scheduleList = response.data ?: emptyList()
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
            .background(Color(0xFFF8F9FA))
    ) {
        // Header - Professional Greeting
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        brush = Brush.verticalGradient(
                            colors = listOf(
                                Color(0xFF5E72E4),
                                Color(0xFF4C63D2)
                            )
                        )
                    )
                    .padding(Spacing.lg)
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Halo, $userName 👋",
                                style = MaterialTheme.typography.headlineSmall,
                                fontWeight = FontWeight.Bold,
                                color = Color.White,
                                fontSize = 24.sp
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Kelas: $kelas",
                                style = MaterialTheme.typography.bodySmall,
                                color = Color.White.copy(alpha = 0.85f)
                            )
                        }
                        Box(
                            modifier = Modifier
                                .size(56.dp)
                                .background(
                                    color = Color.White.copy(alpha = 0.2f),
                                    shape = RoundedCornerShape(12.dp)
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                Icons.Default.School,
                                contentDescription = "Siswa",
                                tint = Color.White,
                                modifier = Modifier.size(32.dp)
                            )
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(Spacing.md))
                    
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = Color.White
                        ),
                        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(Spacing.md),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(Spacing.sm)
                        ) {
                            Icon(
                                Icons.Default.CalendarToday,
                                contentDescription = "Date",
                                tint = Color(0xFF5E72E4),
                                modifier = Modifier.size(20.dp)
                            )
                            Text(
                                text = getCurrentDateIndonesian(),
                                style = MaterialTheme.typography.bodySmall,
                                color = Color(0xFF2D3748),
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }
            }
        }

        // Jadwal Hari Ini Section
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = Spacing.lg, vertical = Spacing.md)
            ) {
                Text(
                    text = "Jadwal Hari Ini",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF2D3748)
                )
            }
        }

        if (isLoading) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = Color(0xFF5E72E4))
                }
            }
        } else if (scheduleList.isEmpty()) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(Spacing.lg)
                        .background(
                            color = Color(0xFFF0F1F3),
                            shape = RoundedCornerShape(16.dp)
                        )
                        .padding(Spacing.lg),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(
                            Icons.Default.EventNote,
                            contentDescription = "Tidak ada jadwal",
                            tint = Color(0xFFCAD1DC),
                            modifier = Modifier.size(48.dp)
                        )
                        Spacer(modifier = Modifier.height(Spacing.sm))
                        Text(
                            text = "Tidak ada jadwal hari ini",
                            color = Color(0xFF718096),
                            style = MaterialTheme.typography.bodySmall,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }
        } else {
            items(scheduleList) { schedule ->
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = Spacing.lg, vertical = 6.dp)
                ) {
                    ScheduleCardHome(schedule)
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(Spacing.lg))
        }
    }
}

@Composable
fun ScheduleCardHome(schedule: Schedule) {
    val hasReplacement = schedule.guru_pengganti != null
    
    Card(
        modifier = Modifier
            .fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(Spacing.md)
        ) {
            // Top row: Time and Subject
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(Spacing.md),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Time Box
                Box(
                    modifier = Modifier
                        .size(72.dp)
                        .background(
                            color = if (hasReplacement) Color(0xFFEF5350).copy(alpha = 0.15f) else Color(0xFF5E72E4).copy(alpha = 0.15f),
                            shape = RoundedCornerShape(12.dp)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = formatTime(schedule.jam_mulai),
                            style = MaterialTheme.typography.labelMedium,
                            color = if (hasReplacement) Color(0xFFEF5350) else Color(0xFF5E72E4),
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                        Text(
                            text = "–",
                            style = MaterialTheme.typography.labelSmall,
                            color = Color(0xFFCAD1DC),
                            fontSize = 10.sp
                        )
                        Text(
                            text = formatTime(schedule.jam_selesai),
                            style = MaterialTheme.typography.labelSmall,
                            color = if (hasReplacement) Color(0xFFEF5350) else Color(0xFF5E72E4),
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp
                        )
                    }
                }

                // Subject and Guru
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = schedule.mata_pelajaran ?: "Mata Pelajaran",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF2D3748)
                    )
                    
                    Spacer(modifier = Modifier.height(3.dp))
                    
                    if (hasReplacement && schedule.guru_pengganti != null) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Icon(
                                Icons.Default.Person,
                                contentDescription = "Guru Pengganti",
                                tint = Color(0xFFEF5350),
                                modifier = Modifier.size(14.dp)
                            )
                            Text(
                                text = schedule.guru_pengganti.guruPengganti?.name ?: "Guru Pengganti",
                                style = MaterialTheme.typography.labelSmall,
                                color = Color(0xFFEF5350),
                                fontWeight = FontWeight.SemiBold,
                                maxLines = 1
                            )
                        }
                    } else {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Icon(
                                Icons.Default.Person,
                                contentDescription = "Guru",
                                tint = Color(0xFF718096),
                                modifier = Modifier.size(14.dp)
                            )
                            Text(
                                text = schedule.guru?.name ?: "Guru",
                                style = MaterialTheme.typography.labelSmall,
                                color = Color(0xFF4A5568),
                                maxLines = 1
                            )
                        }
                    }
                }

                // Replacement badge if applicable
                if (hasReplacement) {
                    Surface(
                        color = Color(0xFFEF5350).copy(alpha = 0.2f),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            text = "Diganti",
                            style = MaterialTheme.typography.labelSmall,
                            color = Color(0xFFEF5350),
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            fontSize = 10.sp
                        )
                    }
                }
            }

            // Divider
            Spacer(modifier = Modifier.height(Spacing.sm))
            Divider(color = Color(0xFFF0F1F3), thickness = 1.dp)
            Spacer(modifier = Modifier.height(Spacing.sm))

            // Bottom row: Room info
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Icon(
                    Icons.Default.LocationOn,
                    contentDescription = "Ruang",
                    tint = Color(0xFF718096),
                    modifier = Modifier.size(14.dp)
                )
                Text(
                    text = schedule.ruang ?: "Ruang",
                    style = MaterialTheme.typography.labelSmall,
                    color = Color(0xFF718096)
                )
            }

            // Keterangan if replacement
            if (hasReplacement && schedule.guru_pengganti?.keterangan?.isNotEmpty() == true) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Alasan: ${schedule.guru_pengganti.keterangan}",
                    style = MaterialTheme.typography.labelSmall,
                    color = Color(0xFF718096),
                    fontSize = 10.sp
                )
            }
        }
    }
}

fun getCurrentDay(): String {
    val days = arrayOf("Minggu", "Senin", "Selasa", "Rabu", "Kamis", "Jumat", "Sabtu")
    val calendar = java.util.Calendar.getInstance()
    return days[calendar.get(java.util.Calendar.DAY_OF_WEEK) - 1]
}

fun getCurrentDateIndonesian(): String {
    val formatter = DateTimeFormatter.ofPattern("EEEE, dd MMMM yyyy", Locale("id", "ID"))
    return LocalDate.now().format(formatter).replaceFirstChar { it.uppercase() }
}

fun formatTime(timeString: String?): String {
    return try {
        if (timeString == null) return "-"
        val time = when {
            timeString.contains("T") -> timeString.substringAfter("T").substringBefore(".")
            timeString.length > 5 -> timeString.substring(0, 5)
            else -> timeString
        }
        time
    } catch (e: Exception) {
        timeString ?: "-"
    }
}

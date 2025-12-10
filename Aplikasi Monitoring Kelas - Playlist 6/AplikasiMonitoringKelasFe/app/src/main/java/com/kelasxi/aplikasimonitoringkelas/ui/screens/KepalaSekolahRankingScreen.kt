package com.kelasxi.aplikasimonitoringkelas.ui.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kelasxi.aplikasimonitoringkelas.data.api.RetrofitClient
import com.kelasxi.aplikasimonitoringkelas.data.repository.AppRepositoryNew
import com.kelasxi.aplikasimonitoringkelas.ui.theme.Spacing
import com.kelasxi.aplikasimonitoringkelas.utils.SharedPrefManager
import kotlinx.coroutines.launch

data class RankingTeacher(
    val rank: Int,
    val name: String,
    val nip: String,
    val subject: String,
    val attendancePercentage: Double,
    val hadir: Int,
    val tidakHadir: Int,
    val izin: Int,
    val medal: String? = null
)

@Composable
fun KepalaSekolahRankingScreen() {
    val context = LocalContext.current
    val sharedPrefManager = remember { SharedPrefManager.getInstance(context) }
    val token = sharedPrefManager.getToken()
    val repository = remember { AppRepositoryNew(RetrofitClient.apiService) }
    val scope = rememberCoroutineScope()

    var isLoading by remember { mutableStateOf(true) }
    var rankingData by remember { mutableStateOf<List<RankingTeacher>>(emptyList()) }
    var selectedFilter by remember { mutableStateOf("semua") }

    LaunchedEffect(Unit) {
        if (token != null) {
            scope.launch {
                try {
                    // Load ranking data
                    repository.getTeachers(token).onSuccess { teachersResponse ->
                        repository.getTodayAttendance(token).onSuccess { attendanceResponse ->
                            // Calculate ranking based on attendance data
                            val teachers = teachersResponse.data ?: emptyList()
                            val attendanceList = attendanceResponse.data ?: emptyList()

                            val ranking = teachers.mapIndexed { index, teacher ->
                                val teacherAttendance = attendanceList.filter {
                                    it.guru?.id == teacher.id
                                }
                                val hadir = teacherAttendance.count { it.status == "masuk" }
                                val tidakHadir = teacherAttendance.count { it.status == "tidak_masuk" }
                                val izin = teacherAttendance.count { it.status == "izin" }
                                val total = hadir + tidakHadir + izin
                                val percentage = if (total > 0) (hadir.toDouble() / total) * 100 else 0.0

                                RankingTeacher(
                                    rank = index + 1,
                                    name = teacher.name ?: "Unknown",
                                    nip = teacher.id.toString(),
                                    subject = teacher.mata_pelajaran ?: "Unknown",
                                    attendancePercentage = percentage,
                                    hadir = hadir,
                                    tidakHadir = tidakHadir,
                                    izin = izin,
                                    medal = when (index) {
                                        0 -> "🥇"
                                        1 -> "🥈"
                                        2 -> "🥉"
                                        else -> null
                                    }
                                )
                            }.sortedByDescending { it.attendancePercentage }

                            rankingData = ranking
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
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(Spacing.sm)
                    ) {
                        Icon(
                            Icons.Default.EmojiEvents,
                            contentDescription = "Ranking",
                            tint = Color(0xFFFFD700),
                            modifier = Modifier.size(24.dp)
                        )
                        Text(
                            text = "Ranking Kehadiran",
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                    Text(
                        text = "Desember 2025",
                        style = MaterialTheme.typography.labelSmall,
                        color = Color.White.copy(alpha = 0.8f),
                        modifier = Modifier.padding(top = Spacing.sm)
                    )
                }
            }
        }

        // Filter Tabs
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(Spacing.lg)
                    .padding(bottom = Spacing.md),
                horizontalArrangement = Arrangement.spacedBy(Spacing.sm)
            ) {
                FilterChip(
                    selected = selectedFilter == "semua",
                    onClick = { selectedFilter = "semua" },
                    label = { Text("Semua", fontSize = 12.sp) },
                    modifier = Modifier.height(32.dp)
                )
                FilterChip(
                    selected = selectedFilter == "terbaik",
                    onClick = { selectedFilter = "terbaik" },
                    label = { Text("Terbaik", fontSize = 12.sp) },
                    modifier = Modifier.height(32.dp)
                )
                FilterChip(
                    selected = selectedFilter == "perhatian",
                    onClick = { selectedFilter = "perhatian" },
                    label = { Text("Perlu Perhatian", fontSize = 12.sp) },
                    modifier = Modifier.height(32.dp)
                )
            }
        }

        // Loading state
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
            // Ranking Cards
            items(rankingData) { teacher ->
                RankingCard(teacher = teacher)
            }

            item {
                Spacer(modifier = Modifier.height(Spacing.lg))
            }
        }
    }
}

@Composable
fun RankingCard(teacher: RankingTeacher) {
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
                .padding(Spacing.md)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Row(
                    modifier = Modifier.weight(1f),
                    horizontalArrangement = Arrangement.spacedBy(Spacing.md),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Rank Badge
                    Surface(
                        color = when (teacher.rank) {
                            1 -> Color(0xFFFFD700).copy(alpha = 0.2f)
                            2 -> Color(0xFFC0C0C0).copy(alpha = 0.2f)
                            3 -> Color(0xFFCD7F32).copy(alpha = 0.2f)
                            else -> Color.Gray.copy(alpha = 0.1f)
                        },
                        shape = CircleShape,
                        modifier = Modifier.size(48.dp)
                    ) {
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier.fillMaxSize()
                        ) {
                            Text(
                                text = teacher.medal ?: "#${teacher.rank}",
                                style = MaterialTheme.typography.bodyLarge,
                                fontWeight = FontWeight.Bold,
                                color = when (teacher.rank) {
                                    1 -> Color(0xFFFFD700)
                                    2 -> Color(0xFFC0C0C0)
                                    3 -> Color(0xFFCD7F32)
                                    else -> Color.Gray
                                },
                                fontSize = 16.sp
                            )
                        }
                    }

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
                    }
                }

                // Attendance Percentage
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Surface(
                        color = Color(0xFF1F3A7F).copy(alpha = 0.1f),
                        shape = CircleShape,
                        modifier = Modifier.size(56.dp)
                    ) {
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier.fillMaxSize()
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = "${teacher.attendancePercentage.toInt()}%",
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF1F3A7F),
                                    fontSize = 14.sp
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(Spacing.md))

            // Attendance Stats
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(Spacing.sm)
            ) {
                AttendanceStatBadge(
                    icon = "✓",
                    label = "Hadir",
                    value = teacher.hadir.toString(),
                    color = Color(0xFF4CAF50),
                    modifier = Modifier.weight(1f)
                )
                AttendanceStatBadge(
                    icon = "✕",
                    label = "Tidak Hadir",
                    value = teacher.tidakHadir.toString(),
                    color = Color(0xFFEF5350),
                    modifier = Modifier.weight(1f)
                )
                AttendanceStatBadge(
                    icon = "📋",
                    label = "Izin",
                    value = teacher.izin.toString(),
                    color = Color(0xFF29B6F6),
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
fun AttendanceStatBadge(
    icon: String,
    label: String,
    value: String,
    color: Color,
    modifier: Modifier = Modifier
) {
    Surface(
        color = color.copy(alpha = 0.1f),
        shape = RoundedCornerShape(8.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, color.copy(alpha = 0.3f)),
        modifier = modifier
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(Spacing.sm),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = icon,
                fontSize = 16.sp
            )
            Text(
                text = value,
                style = MaterialTheme.typography.labelSmall,
                color = color,
                fontWeight = FontWeight.Bold,
                fontSize = 10.sp
            )
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                color = Color.Gray,
                fontSize = 8.sp
            )
        }
    }
}

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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kelasxi.aplikasimonitoringkelas.data.api.RetrofitClient
import com.kelasxi.aplikasimonitoringkelas.data.repository.AppRepositoryNew
import com.kelasxi.aplikasimonitoringkelas.ui.theme.Spacing
import com.kelasxi.aplikasimonitoringkelas.utils.SharedPrefManager
import kotlinx.coroutines.launch

@Composable
fun AdminListJadwalScreen() {
    val context = LocalContext.current
    val sharedPrefManager = remember { SharedPrefManager.getInstance(context) }
    val token = sharedPrefManager.getToken()
    val repository = remember { AppRepositoryNew(RetrofitClient.apiService) }
    val scope = rememberCoroutineScope()

    var jadwalList by remember { mutableStateOf<List<Any>>(emptyList()) }
    var isLoading by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        if (token != null) {
            isLoading = true
            scope.launch {
                try {
                    repository.getSchedules(token, null, null).onSuccess { response ->
                        jadwalList = response.data ?: emptyList()
                        println("DEBUG AdminListJadwal: Schedules loaded - count: ${jadwalList.size}")
                        jadwalList.forEachIndexed { index, schedule ->
                            val sched = schedule as? com.kelasxi.aplikasimonitoringkelas.data.model.Schedule
                            if (sched != null) {
                                println("DEBUG AdminListJadwal: Schedule[$index] - Kelas: ${sched.kelas}, Guru: ${sched.guru?.name ?: "N/A"}, Mata Pelajaran: ${sched.mata_pelajaran}")
                            }
                        }
                    }.onFailure { error ->
                        println("DEBUG AdminListJadwal: Error loading schedules - ${error.message}")
                    }
                } catch (e: Exception) {
                    println("DEBUG AdminListJadwal: Exception - ${e.message}")
                    Toast.makeText(context, "Error loading schedules", Toast.LENGTH_SHORT).show()
                } finally {
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
                        Surface(
                            color = Color(0xFFC5B3FF),
                            shape = RoundedCornerShape(50),
                            modifier = Modifier.size(50.dp)
                        ) {
                            Box(
                                contentAlignment = Alignment.Center,
                                modifier = Modifier.fillMaxSize()
                            ) {
                                Icon(
                                    Icons.AutoMirrored.Filled.ViewList,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                        }
                        Column {
                            Text(
                                text = "List Jadwal",
                                style = MaterialTheme.typography.headlineSmall,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                            Text(
                                text = "Total: ${jadwalList.size} jadwal",
                                style = MaterialTheme.typography.bodySmall,
                                color = Color.White.copy(alpha = 0.8f)
                            )
                        }
                    }
                }
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
                    CircularProgressIndicator(color = Color(0xFF1F3A7F))
                }
            }
        } else {
            items(jadwalList) { schedule ->
                val sched = schedule as? com.kelasxi.aplikasimonitoringkelas.data.model.Schedule
                if (sched != null) {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = Spacing.lg)
                            .padding(bottom = Spacing.sm),
                        shape = RoundedCornerShape(8.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(Spacing.md),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(Spacing.md),
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.weight(1f)
                            ) {
                                Surface(
                                    color = Color(0xFFC5B3FF),
                                    shape = RoundedCornerShape(8.dp),
                                    modifier = Modifier.size(40.dp)
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Text(
                                            sched.mata_pelajaran.take(1).uppercase(),
                                            fontWeight = FontWeight.Bold,
                                            color = Color.White,
                                            fontSize = 12.sp
                                        )
                                    }
                                }
                                Column {
                                    Text(
                                        sched.mata_pelajaran,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 12.sp
                                    )
                                    Text(
                                        "Kelas: ${sched.kelas}",
                                        fontSize = 9.sp,
                                        color = Color.Gray
                                    )
                                    Text(
                                        "Guru: ${sched.guru?.name ?: "N/A"}",
                                        fontSize = 9.sp,
                                        color = Color.Gray
                                    )
                                    Text(
                                        "${sched.jam_mulai} - ${sched.jam_selesai}",
                                        fontSize = 9.sp,
                                        color = Color.Gray
                                    )
                                    Text(
                                        "Hari: ${sched.hari}",
                                        fontSize = 9.sp,
                                        color = Color.Gray
                                    )
                                }
                            }
                            IconButton(onClick = {}) {
                                Icon(
                                    Icons.Default.Delete,
                                    null,
                                    tint = Color(0xFFEB3B5A),
                                    modifier = Modifier.size(20.dp)
                                )
                            }
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

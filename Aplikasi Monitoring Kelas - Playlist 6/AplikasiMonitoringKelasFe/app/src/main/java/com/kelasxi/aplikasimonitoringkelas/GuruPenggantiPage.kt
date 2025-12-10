package com.kelasxi.aplikasimonitoringkelas

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.kelasxi.aplikasimonitoringkelas.data.model.*
import com.kelasxi.aplikasimonitoringkelas.ui.theme.AplikasiMonitoringKelasTheme
import com.kelasxi.aplikasimonitoringkelas.utils.SharedPrefManager
import com.kelasxi.aplikasimonitoringkelas.viewmodel.*
import java.text.SimpleDateFormat
import java.util.*
import java.util.Calendar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GuruPenggantiPage(viewModel: ScheduleViewModel = viewModel()) {
    val context = LocalContext.current
    val sharedPrefManager = remember { SharedPrefManager.getInstance(context) }
    val token = sharedPrefManager.getToken()
    
    var selectedKelas by remember { mutableStateOf("") }
    var isKelasDropdownExpanded by remember { mutableStateOf(false) }
    
    val schedules by viewModel.schedules
    val isLoading by viewModel.isLoading
    val errorMessage by viewModel.errorMessage
    
    // Get today's day name in Indonesian
    val todayHari = remember {
        val calendar = Calendar.getInstance()
        val days = listOf("Minggu", "Senin", "Selasa", "Rabu", "Kamis", "Jumat", "Sabtu")
        days[calendar.get(Calendar.DAY_OF_WEEK) - 1]
    }
    
    // Load all schedules initially
    LaunchedEffect(Unit) {
        token?.let { viewModel.loadSchedules(it) }
    }
    
    // Filter only schedules with guru_pengganti for TODAY only, and match by kelas
    val replacementSchedules = remember(schedules, selectedKelas, todayHari) {
        schedules
            .filter { it.guru_pengganti != null && !it.guru_pengganti?.guruPengganti?.name.isNullOrEmpty() }
            .filter { it.hari == todayHari }
            .filter { selectedKelas.isEmpty() || it.kelas == selectedKelas }
            .sortedWith(compareBy({ it.kelas }, { it.jam_mulai }))
    }
    
    // Get unique kelas from schedules with replacement TODAY
    val kelasList = remember(schedules, todayHari) {
        schedules
            .filter { it.guru_pengganti != null && !it.guru_pengganti?.guruPengganti?.name.isNullOrEmpty() }
            .filter { it.hari == todayHari }
            .map { it.kelas }
            .distinct()
            .sorted()
    }
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Header dengan info hari
        Column(modifier = Modifier.padding(bottom = 16.dp)) {
            Text(
                text = "Informasi Guru Pengganti",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = "Hari $todayHari",
                fontSize = 14.sp,
                color = Color(0xFF666666),
                modifier = Modifier.padding(top = 4.dp)
            )
        }
        
        // Kelas Filter Dropdown
        if (kelasList.isNotEmpty()) {
            ExposedDropdownMenuBox(
                expanded = isKelasDropdownExpanded,
                onExpandedChange = { isKelasDropdownExpanded = !isKelasDropdownExpanded },
                modifier = Modifier.padding(bottom = 16.dp)
            ) {
                OutlinedTextField(
                    value = selectedKelas.ifEmpty { "Semua Kelas" },
                    onValueChange = { },
                    readOnly = true,
                    label = { Text("Filter Kelas") },
                    trailingIcon = {
                        ExposedDropdownMenuDefaults.TrailingIcon(expanded = isKelasDropdownExpanded)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .menuAnchor()
                )
                ExposedDropdownMenu(
                    expanded = isKelasDropdownExpanded,
                    onDismissRequest = { isKelasDropdownExpanded = false }
                ) {
                    DropdownMenuItem(
                        text = { Text("Semua Kelas") },
                        onClick = {
                            selectedKelas = ""
                            isKelasDropdownExpanded = false
                        }
                    )
                    kelasList.forEach { kelas ->
                        DropdownMenuItem(
                            text = { Text(kelas) },
                            onClick = {
                                selectedKelas = kelas
                                isKelasDropdownExpanded = false
                            }
                        )
                    }
                }
            }
        }
        
        // Status indicators
        when {
            isLoading -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
            
            errorMessage != null -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Error: $errorMessage",
                        color = MaterialTheme.colorScheme.error
                    )
                }
            }
            
            replacementSchedules.isNotEmpty() -> {
                Text(
                    text = "Ditemukan ${replacementSchedules.size} guru pengganti",
                    fontSize = 14.sp,
                    color = Color(0xFFE65100),
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.padding(bottom = 12.dp)
                )
                
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(replacementSchedules) { schedule ->
                        GuruPenggantiCard(schedule = schedule)
                    }
                }
            }
            
            else -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = if (token != null) "Tidak ada guru pengganti hari ini" else "Silakan login terlebih dahulu",
                        fontSize = 16.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

@Composable
fun GuruPenggantiCard(schedule: Schedule) {
    val replacement = schedule.guru_pengganti
    
    if (replacement == null) return
    
    // Parse jam dari datetime
    val jamMulai = extractTime(schedule.jam_mulai)
    val jamSelesai = extractTime(schedule.jam_selesai)
    
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .border(
                width = 2.dp,
                color = Color(0xFFD32F2F),
                shape = RoundedCornerShape(8.dp)
            ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFFFFEBEE)
        ),
        shape = RoundedCornerShape(8.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Header dengan warning icon
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = schedule.kelas,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFD32F2F)
                    )
                    Text(
                        text = "${schedule.hari} • $jamMulai - $jamSelesai",
                        fontSize = 12.sp,
                        color = Color(0xFF666666),
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }
                Text(
                    text = "⚠",
                    fontSize = 32.sp,
                    color = Color(0xFFD32F2F)
                )
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            Divider(color = Color(0xFFFFCDD2), thickness = 1.dp)
            Spacer(modifier = Modifier.height(12.dp))
            
            // Mata Pelajaran
            Text(
                text = schedule.mata_pelajaran,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF333333),
                modifier = Modifier.padding(bottom = 12.dp)
            )
            
            // Guru Asli (dengan strikethrough)
            Text(
                text = "Guru Asli:",
                fontSize = 11.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color(0xFF999999)
            )
            Text(
                text = replacement.guruAsli?.name ?: schedule.guru?.name ?: "Guru (${schedule.guru_id})",
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF666666),
                style = MaterialTheme.typography.bodyMedium.copy(
                    textDecoration = TextDecoration.LineThrough
                ),
                modifier = Modifier.padding(bottom = 12.dp)
            )
            
            // Keterangan / Alasan
            if (!replacement.keterangan.isNullOrEmpty()) {
                Text(
                    text = "Alasan Penggantian:",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFFF57C00)
                )
                Text(
                    text = replacement.keterangan!!,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color(0xFFE65100),
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            color = Color(0xFFFFF3E0),
                            shape = RoundedCornerShape(4.dp)
                        )
                        .padding(8.dp),
                    maxLines = 3
                )
                Spacer(modifier = Modifier.height(12.dp))
            }
            
            // Guru Pengganti
            Text(
                text = "Guru Pengganti:",
                fontSize = 11.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color(0xFF1B5E20)
            )
            Text(
                text = replacement.guruPengganti?.name ?: "Guru Pengganti",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFFFFFFFF),
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        color = Color(0xFF2E7D32),
                        shape = RoundedCornerShape(4.dp)
                    )
                    .padding(10.dp)
            )
        }
    }
}

fun extractTime(datetime: String): String {
    return try {
        // Expected format: "2025-12-08 13:00:00"
        val parts = datetime.split(" ")
        if (parts.size >= 2) {
            val time = parts[1]
            time.substringBeforeLast(":") // Remove seconds
        } else {
            datetime
        }
    } catch (e: Exception) {
        datetime
    }
}

@Preview(showBackground = true)
@Composable
fun GuruPenggantiPagePreview() {
    AplikasiMonitoringKelasTheme {
        GuruPenggantiPage()
    }
}

package com.kelasxi.aplikasimonitoringkelas

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.kelasxi.aplikasimonitoringkelas.data.model.*
import com.kelasxi.aplikasimonitoringkelas.ui.theme.AplikasiMonitoringKelasTheme
import com.kelasxi.aplikasimonitoringkelas.ui.theme.Spacing
import com.kelasxi.aplikasimonitoringkelas.utils.SharedPrefManager
import com.kelasxi.aplikasimonitoringkelas.viewmodel.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun JadwalPage(userRole: String = "Siswa", viewModel: ScheduleViewModel = viewModel()) {
    val context = LocalContext.current
    val sharedPrefManager = remember { SharedPrefManager.getInstance(context) }
    val token = sharedPrefManager.getToken()
    
    var selectedHari by remember { mutableStateOf("") }
    var selectedKelas by remember { mutableStateOf("") }
    var isHariDropdownExpanded by remember { mutableStateOf(false) }
    var isKelasDropdownExpanded by remember { mutableStateOf(false) }
    
    val schedules by viewModel.schedules
    val isLoading by viewModel.isLoading
    val errorMessage by viewModel.errorMessage
    
    // Load all schedules initially
    LaunchedEffect(Unit) {
        token?.let { viewModel.loadSchedules(it) }
    }
    
    // Handle error messages
    LaunchedEffect(errorMessage) {
        errorMessage?.let { message ->
            // In a real app, you might want to show this in a more user-friendly way
            println("Error: $message")
            viewModel.clearError()
        }
    }
    
    // Filter schedules based on selected day and class
    val filteredSchedules = remember(schedules, selectedHari, selectedKelas) {
        schedules.filter { schedule ->
            (selectedHari.isEmpty() || schedule.hari == selectedHari) &&
            (selectedKelas.isEmpty() || schedule.kelas == selectedKelas)
        }
    }
    
    val hariList = listOf("Senin", "Selasa", "Rabu", "Kamis", "Jumat", "Sabtu", "Minggu")
    // Ambil daftar kelas unik dari schedules yang ada di backend
    val kelasList = remember(schedules) {
        schedules.map { it.kelas }.distinct().sorted()
    }
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF8F9FA))
    ) {
        // Header
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    brush = androidx.compose.ui.graphics.Brush.verticalGradient(
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
                Text(
                    text = "Jadwal Pelajaran",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    fontSize = 24.sp
                )
                Spacer(modifier = Modifier.height(Spacing.md))
                
                // Filter Row
                Row(
                    modifier = Modifier
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(Spacing.md)
                ) {
                    // Hari Filter
                    Box(modifier = Modifier.weight(1f)) {
                        ExposedDropdownMenuBox(
                            expanded = isHariDropdownExpanded,
                            onExpandedChange = { isHariDropdownExpanded = !isHariDropdownExpanded }
                        ) {
                            OutlinedTextField(
                                value = selectedHari.ifEmpty { "Semua Hari" },
                                onValueChange = { },
                                readOnly = true,
                                label = { Text("Hari", fontSize = 12.sp) },
                                trailingIcon = {
                                    ExposedDropdownMenuDefaults.TrailingIcon(expanded = isHariDropdownExpanded)
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .menuAnchor()
                                    .height(48.dp),
                                textStyle = MaterialTheme.typography.bodySmall,
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = Color.White,
                                    unfocusedBorderColor = Color.White.copy(alpha = 0.5f),
                                    focusedTextColor = Color.White,
                                    unfocusedTextColor = Color.White,
                                    cursorColor = Color.White
                                )
                            )
                            ExposedDropdownMenu(
                                expanded = isHariDropdownExpanded,
                                onDismissRequest = { isHariDropdownExpanded = false },
                                modifier = Modifier.background(Color(0xFF2D3748))
                            ) {
                                DropdownMenuItem(
                                    text = { Text("Semua Hari", color = Color.White) },
                                    onClick = {
                                        selectedHari = ""
                                        isHariDropdownExpanded = false
                                    }
                                )
                                hariList.forEach { hari ->
                                    DropdownMenuItem(
                                        text = { Text(hari, color = Color.White) },
                                        onClick = {
                                            selectedHari = hari
                                            isHariDropdownExpanded = false
                                        }
                                    )
                                }
                            }
                        }
                    }
                    
                    // Kelas Filter
                    Box(modifier = Modifier.weight(1f)) {
                        ExposedDropdownMenuBox(
                            expanded = isKelasDropdownExpanded,
                            onExpandedChange = { isKelasDropdownExpanded = !isKelasDropdownExpanded }
                        ) {
                            OutlinedTextField(
                                value = selectedKelas.ifEmpty { "Semua Kelas" },
                                onValueChange = { },
                                readOnly = true,
                                label = { Text("Kelas", fontSize = 12.sp) },
                                trailingIcon = {
                                    ExposedDropdownMenuDefaults.TrailingIcon(expanded = isKelasDropdownExpanded)
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .menuAnchor()
                                    .height(48.dp),
                                textStyle = MaterialTheme.typography.bodySmall,
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = Color.White,
                                    unfocusedBorderColor = Color.White.copy(alpha = 0.5f),
                                    focusedTextColor = Color.White,
                                    unfocusedTextColor = Color.White,
                                    cursorColor = Color.White
                                )
                            )
                            ExposedDropdownMenu(
                                expanded = isKelasDropdownExpanded,
                                onDismissRequest = { isKelasDropdownExpanded = false },
                                modifier = Modifier.background(Color(0xFF2D3748))
                            ) {
                                DropdownMenuItem(
                                    text = { Text("Semua Kelas", color = Color.White) },
                                    onClick = {
                                        selectedKelas = ""
                                        isKelasDropdownExpanded = false
                                    }
                                )
                                kelasList.forEach { kelas ->
                                    DropdownMenuItem(
                                        text = { Text(kelas, color = Color.White) },
                                        onClick = {
                                            selectedKelas = kelas
                                            isKelasDropdownExpanded = false
                                        }
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
        
        // Content
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(Spacing.lg)
        ) {
            when {
                isLoading -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(color = Color(0xFF5E72E4))
                    }
                }
                
                errorMessage != null -> {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .align(Alignment.TopCenter),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = Color(0xFFFFEBEE)
                        )
                    ) {
                        Column(
                            modifier = Modifier.padding(Spacing.md),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            androidx.compose.material.icons.Icons.Default.ErrorOutline
                            Text(
                                text = "Error: $errorMessage",
                                color = Color(0xFFC62828),
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                    }
                }
                
                filteredSchedules.isNotEmpty() -> {
                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(Spacing.md)
                    ) {
                        items(filteredSchedules) { schedule ->
                            JadwalCardModern(schedule = schedule)
                        }
                    }
                }
                
                else -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(
                                Icons.Default.EventNote,
                                contentDescription = "Tidak ada jadwal",
                                tint = Color(0xFFCAD1DC),
                                modifier = Modifier.size(48.dp)
                            )
                            Spacer(modifier = Modifier.height(Spacing.md))
                            Text(
                                text = "Tidak ada jadwal tersedia",
                                style = MaterialTheme.typography.bodySmall,
                                color = Color(0xFF718096)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun JadwalCardModern(schedule: Schedule) {
    val isBreakTime = schedule.mata_pelajaran.contains("Istirahat", ignoreCase = true) ||
                     schedule.mata_pelajaran.contains("Break", ignoreCase = true) ||
                     schedule.mata_pelajaran.isBlank()
    
    val hasReplacement = schedule.guru_pengganti != null
    
    if (isBreakTime) {
        Card(
            modifier = Modifier
                .fillMaxWidth(),
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color(0xFFF0F1F3)
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(Spacing.md),
                contentAlignment = Alignment.Center
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(Spacing.sm)
                ) {
                    Icon(
                        Icons.Default.Schedule,
                        contentDescription = "Istirahat",
                        tint = Color(0xFF718096),
                        modifier = Modifier.size(18.dp)
                    )
                    Text(
                        text = "Jam ${schedule.jam_mulai} - ${schedule.jam_selesai} • Istirahat",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color(0xFF718096)
                    )
                }
            }
        }
    } else {
        Card(
            modifier = Modifier
                .fillMaxWidth(),
            shape = RoundedCornerShape(14.dp),
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
                // Time and Subject Row
                Row(
                    modifier = Modifier
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(Spacing.md),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Time Badge
                    Box(
                        modifier = Modifier
                            .background(
                                color = if (hasReplacement) Color(0xFFEF5350).copy(alpha = 0.15f) else Color(0xFF5E72E4).copy(alpha = 0.15f),
                                shape = RoundedCornerShape(12.dp)
                            )
                            .padding(horizontal = 12.dp, vertical = 8.dp)
                    ) {
                        Text(
                            text = "${schedule.jam_mulai} - ${schedule.jam_selesai}",
                            style = MaterialTheme.typography.labelSmall,
                            color = if (hasReplacement) Color(0xFFEF5350) else Color(0xFF5E72E4),
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp
                        )
                    }
                    
                    // Subject Title
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = schedule.mata_pelajaran,
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Bold,
                            color = if (hasReplacement) Color(0xFFB71C1C) else Color(0xFF2D3748),
                            maxLines = 1
                        )
                    }
                    
                    // Replacement Badge
                    if (hasReplacement) {
                        Surface(
                            color = Color(0xFFEF5350),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(
                                text = "Diganti",
                                style = MaterialTheme.typography.labelSmall,
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 9.sp,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }
                }
                
                Spacer(modifier = Modifier.height(Spacing.sm))
                Divider(color = Color(0xFFF0F1F3), thickness = 1.dp)
                Spacer(modifier = Modifier.height(Spacing.sm))
                
                // Guru Info
                if (hasReplacement && schedule.guru_pengganti != null) {
                    // Guru Asli (Strikethrough)
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                color = Color(0xFFF8F9FA),
                                shape = RoundedCornerShape(10.dp)
                            )
                            .padding(Spacing.sm)
                    ) {
                        Text(
                            text = "Guru Asli",
                            style = MaterialTheme.typography.labelSmall,
                            color = Color(0xFF999999),
                            fontSize = 10.sp
                        )
                        Text(
                            text = schedule.guru_pengganti.guruAsli?.name ?: schedule.guru?.name ?: "Guru (${schedule.guru_id})",
                            style = MaterialTheme.typography.bodySmall.copy(
                                textDecoration = TextDecoration.LineThrough
                            ),
                            fontWeight = FontWeight.Medium,
                            color = Color(0xFF999999),
                            fontSize = 12.sp
                        )
                    }
                    
                    Spacer(modifier = Modifier.height(Spacing.sm))
                    
                    // Alasan Penggantian
                    if (!schedule.guru_pengganti.keterangan.isNullOrEmpty()) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(
                                    color = Color(0xFFFFF3E0),
                                    shape = RoundedCornerShape(10.dp)
                                )
                                .padding(Spacing.sm)
                        ) {
                            Text(
                                text = "Alasan",
                                style = MaterialTheme.typography.labelSmall,
                                color = Color(0xFFF57C00),
                                fontSize = 10.sp
                            )
                            Text(
                                text = schedule.guru_pengganti.keterangan!!,
                                style = MaterialTheme.typography.bodySmall,
                                color = Color(0xFFE65100),
                                fontSize = 12.sp
                            )
                        }
                        Spacer(modifier = Modifier.height(Spacing.sm))
                    }
                    
                    // Guru Pengganti (Green)
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                color = Color(0xFFF1F8E9),
                                shape = RoundedCornerShape(10.dp)
                            )
                            .padding(Spacing.sm)
                    ) {
                        Text(
                            text = "Guru Pengganti",
                            style = MaterialTheme.typography.labelSmall,
                            color = Color(0xFF1B5E20),
                            fontSize = 10.sp
                        )
                        Text(
                            text = schedule.guru_pengganti.guruPengganti?.name ?: "Guru Pengganti",
                            style = MaterialTheme.typography.bodySmall,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF2E7D32),
                            fontSize = 12.sp
                        )
                    }
                } else {
                    // Normal Guru
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                color = Color(0xFFF0F1F3),
                                shape = RoundedCornerShape(10.dp)
                            )
                            .padding(Spacing.sm)
                    ) {
                        Text(
                            text = "Guru Pengajar",
                            style = MaterialTheme.typography.labelSmall,
                            color = Color(0xFF718096),
                            fontSize = 10.sp
                        )
                        Text(
                            text = schedule.guru?.name ?: "Guru (${schedule.guru_id})",
                            style = MaterialTheme.typography.bodySmall,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF2D3748),
                            fontSize = 12.sp
                        )
                    }
                }
                
                Spacer(modifier = Modifier.height(Spacing.sm))
                
                // Room Info
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    modifier = Modifier.fillMaxWidth()
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
                        color = Color(0xFF718096),
                        fontSize = 11.sp
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun JadwalPagePreview() {
    AplikasiMonitoringKelasTheme {
        JadwalPage("Siswa")
    }
}
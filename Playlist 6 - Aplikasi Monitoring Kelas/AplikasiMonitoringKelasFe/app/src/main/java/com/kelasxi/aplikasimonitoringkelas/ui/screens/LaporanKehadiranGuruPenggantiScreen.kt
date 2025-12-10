package com.kelasxi.aplikasimonitoringkelas.ui.screens

import androidx.compose.foundation.background
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kelasxi.aplikasimonitoringkelas.data.model.ReplacementTeacherReportItem
import com.kelasxi.aplikasimonitoringkelas.ui.theme.Spacing
import com.kelasxi.aplikasimonitoringkelas.utils.SharedPrefManager
import com.kelasxi.aplikasimonitoringkelas.viewmodel.GuruPenggantiViewModel
import com.kelasxi.aplikasimonitoringkelas.viewmodel.GuruPenggantiReportUiState

@Composable
fun LaporanKehadiranGuruPenggantiScreen() {
    val context = LocalContext.current
    val sharedPrefManager = remember { SharedPrefManager.getInstance(context) }
    val token = sharedPrefManager.getToken()
    val viewModel = remember { GuruPenggantiViewModel() }

    val reportState by viewModel.reportState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(Unit) {
        if (token != null) {
            viewModel.getReplacementTeacherReport(token)
        }
    }

    // Extract data from Success state - No casting needed!
    // Use local val to avoid delegated property smart cast issue
    val currentReportState = reportState
    val reportList: List<ReplacementTeacherReportItem> = when (currentReportState) {
        is GuruPenggantiReportUiState.Success -> currentReportState.data
        else -> emptyList()
    }

    val isLoading = currentReportState is GuruPenggantiReportUiState.Loading
    val errorMessage = (currentReportState as? GuruPenggantiReportUiState.Error)?.message

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFF1A1A2E))
                .padding(paddingValues)
        ) {
            // Header
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFF7B68EE))
                    .padding(Spacing.lg)
            ) {
                Text(
                    text = "Laporan Kehadiran Guru Pengganti",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }

            // Content
            when {
                isLoading -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(color = Color(0xFF7B68EE))
                    }
                }

                errorMessage != null -> {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(Spacing.lg),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = errorMessage,
                            color = Color.White,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }

                reportList.isEmpty() -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Tidak ada data konfirmasi",
                            color = Color.White.copy(alpha = 0.6f),
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }

                else -> {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(Spacing.lg),
                        verticalArrangement = Arrangement.spacedBy(Spacing.md)
                    ) {
                        items(reportList) { report ->
                            ReportTableRow(report = report)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ReportTableRow(report: ReplacementTeacherReportItem) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = Spacing.sm),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF2A2A2E))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(Spacing.md)
        ) {
            DetailRow(label = "Guru Pengganti", value = report.guru_pengganti_nama)
            DetailRow(label = "Guru Asli", value = report.guru_asli_nama)
            DetailRow(label = "Kelas", value = report.kelas)
            DetailRow(label = "Jam", value = "${report.jam_mulai} - ${report.jam_selesai}")
            DetailRow(label = "Mata Pelajaran", value = report.mata_pelajaran)
            DetailRow(label = "Tanggal", value = report.tanggal)
            DetailRow(label = "Siswa Konfirmasi", value = report.siswa_nama)
            DetailRow(label = "Waktu Konfirmasi", value = formatConfirmationTime(report.confirmed_at))
        }
    }
}

@Composable
fun DetailRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = Color.White.copy(alpha = 0.6f),
            fontSize = 11.sp
        )
        Text(
            text = value,
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.SemiBold,
            color = Color.White,
            fontSize = 11.sp
        )
    }
}

private fun formatConfirmationTime(datetime: String): String {
    return try {
        val parts = datetime.split(" ")
        if (parts.size >= 2) {
            val timeParts = parts[1].split(":")
            if (timeParts.size >= 2) {
                "${timeParts[0]}:${timeParts[1]}"
            } else {
                datetime
            }
        } else {
            datetime
        }
    } catch (e: Exception) {
        datetime
    }
}

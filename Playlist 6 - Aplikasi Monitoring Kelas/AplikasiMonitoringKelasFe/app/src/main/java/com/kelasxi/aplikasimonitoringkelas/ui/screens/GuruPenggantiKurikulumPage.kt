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
import com.kelasxi.aplikasimonitoringkelas.data.model.GuruPengganti
import com.kelasxi.aplikasimonitoringkelas.data.repository.AppRepositoryNew
import com.kelasxi.aplikasimonitoringkelas.ui.theme.*
import com.kelasxi.aplikasimonitoringkelas.utils.SharedPrefManager
import kotlinx.coroutines.launch

@Composable
fun GuruPenggantiKurikulumPage() {
    val context = LocalContext.current
    val sharedPrefManager = remember { SharedPrefManager.getInstance(context) }
    val token = sharedPrefManager.getToken()
    val repository = remember { AppRepositoryNew(RetrofitClient.apiService) }
    val scope = rememberCoroutineScope()

    var guruPenggantiList by remember { mutableStateOf<List<GuruPengganti>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var filterStatus by remember { mutableStateOf("aktif") }
    
    var showCreateDialog by remember { mutableStateOf(false) }
    var selectedGuruPengganti by remember { mutableStateOf<GuruPengganti?>(null) }

    // Form states
    var selectedJadwalId by remember { mutableStateOf<Int?>(null) }
    var selectedTeacherId by remember { mutableStateOf<Int?>(null) }
    var formStatus by remember { mutableStateOf("aktif") }

    LaunchedEffect(Unit) {
        if (token != null) {
            scope.launch {
                try {
                    // Load guru pengganti - sesuaikan dengan endpoint backend
                    repository.getGuruPengganti(token).onSuccess { response ->
                        guruPenggantiList = response.data ?: emptyList()
                        if (guruPenggantiList.isEmpty()) {
                            Toast.makeText(context, "Tidak ada data guru pengganti", Toast.LENGTH_SHORT).show()
                        }
                    }.onFailure { error ->
                        Toast.makeText(context, "Error loading: ${error.message}", Toast.LENGTH_LONG).show()
                        guruPenggantiList = emptyList()
                    }
                    
                    isLoading = false
                } catch (e: Exception) {
                    Toast.makeText(context, "Exception: ${e.message}", Toast.LENGTH_LONG).show()
                    guruPenggantiList = emptyList()
                    isLoading = false
                }
            }
        } else {
            isLoading = false
            Toast.makeText(context, "Token tidak ditemukan", Toast.LENGTH_SHORT).show()
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
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = Spacing.md),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Guru Pengganti",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold
                )
                
                FloatingActionButton(
                    onClick = { showCreateDialog = true },
                    containerColor = SMKPrimary,
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(
                        Icons.Default.Add,
                        contentDescription = "Tambah",
                        tint = Color.White
                    )
                }
            }
        }

        // Status Filter
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = Spacing.lg),
                horizontalArrangement = Arrangement.spacedBy(Spacing.sm)
            ) {
                listOf("aktif", "selesai").forEach { status ->
                    FilterChip(
                        selected = filterStatus == status,
                        onClick = { filterStatus = status },
                        label = {
                            Text(
                                text = if (status == "aktif") "Aktif" else "Selesai",
                                fontSize = 12.sp
                            )
                        },
                        modifier = Modifier.height(36.dp),
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = SMKPrimary,
                            selectedLabelColor = Color.White
                        )
                    )
                }
            }
        }

        // Guru Pengganti List
        if (guruPenggantiList.isEmpty()) {
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
                            Icons.Default.PersonAdd,
                            contentDescription = "No Data",
                            tint = NeutralGray400,
                            modifier = Modifier.size(48.dp)
                        )
                        Spacer(modifier = Modifier.height(Spacing.md))
                        Text(
                            text = "Belum ada guru pengganti",
                            style = MaterialTheme.typography.bodyMedium,
                            color = NeutralGray500
                        )
                    }
                }
            }
        } else {
            items(guruPenggantiList) { guru ->
                if (guru.guruPengganti != null) {
                    GuruPenggantiCard(
                        guru = guru,
                        onEdit = {
                            selectedGuruPengganti = guru
                            showCreateDialog = true
                        },
                        onDelete = {
                            scope.launch {
                                try {
                                    // TODO: Implement delete API call
                                    // repository.deleteGuruPengganti(token, guru.id)
                                    guruPenggantiList = guruPenggantiList.filter { it.id != guru.id }
                                    Toast.makeText(
                                        context,
                                        "Guru pengganti dihapus",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                } catch (e: Exception) {
                                    Toast.makeText(
                                        context,
                                        "Error: ${e.message}",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            }
                        }
                    )
                }
                Spacer(modifier = Modifier.height(Spacing.md))
            }
        }
    }

    // Create/Edit Dialog
    if (showCreateDialog) {
        AlertDialog(
            onDismissRequest = { 
                showCreateDialog = false
                selectedGuruPengganti = null
            },
            title = {
                Text(
                    text = if (selectedGuruPengganti != null) "Edit Guru Pengganti" else "Tambah Guru Pengganti",
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(Spacing.md)
                ) {
                    OutlinedTextField(
                        value = selectedTeacherId?.toString() ?: "",
                        onValueChange = { selectedTeacherId = it.toIntOrNull() },
                        label = { Text("Guru ID") },
                        modifier = Modifier.fillMaxWidth(),
                        keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(
                            keyboardType = androidx.compose.ui.text.input.KeyboardType.Number
                        )
                    )
                    
                    OutlinedTextField(
                        value = selectedJadwalId?.toString() ?: "",
                        onValueChange = { selectedJadwalId = it.toIntOrNull() },
                        label = { Text("Jadwal ID") },
                        modifier = Modifier.fillMaxWidth(),
                        keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(
                            keyboardType = androidx.compose.ui.text.input.KeyboardType.Number
                        )
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        scope.launch {
                            try {
                                // TODO: Implement create/update API call
                                // if (selectedGuruPengganti != null) {
                                //     repository.updateGuruPengganti(token, ...)
                                // } else {
                                //     repository.createGuruPengganti(token, ...)
                                // }
                                
                                showCreateDialog = false
                                selectedGuruPengganti = null
                                Toast.makeText(
                                    context,
                                    "Berhasil disimpan",
                                    Toast.LENGTH_SHORT
                                ).show()
                            } catch (e: Exception) {
                                Toast.makeText(
                                    context,
                                    "Error: ${e.message}",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = SMKPrimary)
                ) {
                    Text("Simpan", color = Color.White)
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        showCreateDialog = false
                        selectedGuruPengganti = null
                    }
                ) {
                    Text("Batal")
                }
            }
        )
    }
}

@Composable
fun GuruPenggantiCard(
    guru: GuruPengganti,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(Spacing.md)
        ) {
            // Header: Tanggal + Status
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = Spacing.md),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = guru.tanggal ?: "-",
                    style = MaterialTheme.typography.bodySmall,
                    color = NeutralGray500
                )
                
                Surface(
                    color = Color(0xFF4CAF50),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Text(
                        text = "Disetujui",
                        style = MaterialTheme.typography.labelSmall,
                        color = Color.White,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
                        fontSize = 10.sp
                    )
                }
            }
            
            // Mata Pelajaran / Kelas
            Text(
                text = guru.mata_pelajaran ?: "-",
                style = MaterialTheme.typography.bodySmall,
                color = NeutralGray600,
                fontWeight = FontWeight.SemiBold
            )
            
            Spacer(modifier = Modifier.height(Spacing.sm))
            
            // Guru Asli → Pengganti
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = Spacing.md),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(Spacing.md)
            ) {
                // Guru Asli (Left side)
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = "Guru Asli",
                        style = MaterialTheme.typography.labelSmall,
                        color = NeutralGray500,
                        fontSize = 10.sp
                    )
                    Text(
                        text = guru.guruAsli?.name ?: "Tidak diketahui",
                        style = MaterialTheme.typography.bodyMedium,
                        color = SMKOnSurface,
                        fontWeight = FontWeight.SemiBold,
                        maxLines = 2
                    )
                }
                
                // Arrow
                Icon(
                    Icons.Filled.KeyboardArrowRight,
                    contentDescription = "Arrow",
                    modifier = Modifier.size(24.dp),
                    tint = SMKPrimary
                )
                
                // Guru Pengganti (Right side)
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = "Pengganti",
                        style = MaterialTheme.typography.labelSmall,
                        color = NeutralGray500,
                        fontSize = 10.sp
                    )
                    Text(
                        text = guru.guruPengganti?.name ?: "Tidak diketahui",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color(0xFF4CAF50),
                        fontWeight = FontWeight.SemiBold,
                        maxLines = 2
                    )
                }
            }
            
            // Alasan
            if (!guru.keterangan.isNullOrEmpty()) {
                Spacer(modifier = Modifier.height(Spacing.sm))
                Text(
                    text = "Alasan: ${guru.keterangan}",
                    style = MaterialTheme.typography.bodySmall,
                    color = NeutralGray600
                )
            }
            
            Spacer(modifier = Modifier.height(Spacing.md))
            
            // Action Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(Spacing.sm)
            ) {
                OutlinedButton(
                    onClick = onEdit,
                    modifier = Modifier
                        .weight(1f)
                        .height(36.dp),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = SMKPrimary
                    ),
                    border = androidx.compose.foundation.BorderStroke(1.dp, SMKPrimary)
                ) {
                    Icon(
                        Icons.Default.Edit,
                        contentDescription = "Edit",
                        modifier = Modifier.size(14.dp),
                        tint = SMKPrimary
                    )
                    Spacer(modifier = Modifier.width(Spacing.xs))
                    Text("Edit", fontSize = 11.sp)
                }
                
                Button(
                    onClick = onDelete,
                    modifier = Modifier
                        .weight(1f)
                        .height(36.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFEF5350)
                    )
                ) {
                    Icon(
                        Icons.Default.Delete,
                        contentDescription = "Delete",
                        modifier = Modifier.size(14.dp),
                        tint = Color.White
                    )
                    Spacer(modifier = Modifier.width(Spacing.xs))
                    Text("Hapus", fontSize = 11.sp, color = Color.White)
                }
            }
        }
    }
}

@Composable
fun StatusBadgeSubstitute(status: String) {
    val (bgColor, label) = when (status) {
        "aktif" -> Pair(Color(0xFF4CAF50), "Aktif")
        "selesai" -> Pair(Color(0xFF9E9E9E), "Selesai")
        else -> Pair(NeutralGray300, "Unknown")
    }
    
    Surface(
        color = bgColor,
        shape = RoundedCornerShape(20.dp)
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = Color.White,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
            fontWeight = FontWeight.Medium,
            fontSize = 10.sp
        )
    }
}

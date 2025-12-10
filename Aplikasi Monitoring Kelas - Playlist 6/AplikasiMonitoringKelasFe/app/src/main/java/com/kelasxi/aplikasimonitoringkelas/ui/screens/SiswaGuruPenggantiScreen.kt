package com.kelasxi.aplikasimonitoringkelas.ui.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import com.kelasxi.aplikasimonitoringkelas.data.model.GuruPengganti
import com.kelasxi.aplikasimonitoringkelas.data.api.RetrofitClient
import com.kelasxi.aplikasimonitoringkelas.data.repository.AppRepositoryNew
import com.kelasxi.aplikasimonitoringkelas.ui.theme.*
import com.kelasxi.aplikasimonitoringkelas.utils.SharedPrefManager
import com.kelasxi.aplikasimonitoringkelas.viewmodel.GuruPenggantiViewModel
import com.kelasxi.aplikasimonitoringkelas.viewmodel.GuruPenggantiUiState
import com.kelasxi.aplikasimonitoringkelas.viewmodel.ConfirmationUiState
import kotlinx.coroutines.launch

@Composable
fun SiswaGuruPenggantiScreen() {
    val context = LocalContext.current
    val sharedPrefManager = remember { SharedPrefManager.getInstance(context) }
    val token = sharedPrefManager.getToken()
    val userId = sharedPrefManager.getUserId()
    val viewModel = remember { GuruPenggantiViewModel() }
    val scope = rememberCoroutineScope()

    var searchQuery by remember { mutableStateOf("") }
    var selectedGuruPengganti by remember { mutableStateOf<GuruPengganti?>(null) }
    var showConfirmationDialog by remember { mutableStateOf(false) }

    val guruPenggantiState by viewModel.guruPenggantiListState.collectAsState()
    val confirmationState by viewModel.confirmationState.collectAsState()

    // Snackbar state
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(Unit) {
        if (token != null) {
            viewModel.getGuruPengganti(token)
        }
    }

    // Handle confirmation state changes
    LaunchedEffect(confirmationState) {
        when (confirmationState) {
            is ConfirmationUiState.Success -> {
                snackbarHostState.showSnackbar(
                    "Terima kasih! Konfirmasi kehadiran guru pengganti telah dikirim.",
                    duration = SnackbarDuration.Long
                )
                viewModel.resetConfirmationState()
                showConfirmationDialog = false
                selectedGuruPengganti = null
                // Refresh list
                if (token != null) {
                    viewModel.getGuruPengganti(token)
                }
            }
            is ConfirmationUiState.AlreadyConfirmed -> {
                snackbarHostState.showSnackbar(
                    "Anda sudah mengkonfirmasi guru pengganti ini",
                    duration = SnackbarDuration.Long
                )
                viewModel.resetConfirmationState()
                showConfirmationDialog = false
            }
            is ConfirmationUiState.Error -> {
                val errorMsg = (confirmationState as ConfirmationUiState.Error).message
                snackbarHostState.showSnackbar(
                    errorMsg,
                    duration = SnackbarDuration.Long
                )
                viewModel.resetConfirmationState()
            }
            else -> {}
        }
    }

    // Use local val to avoid delegated property smart cast issue
    val currentGuruPenggantiState = guruPenggantiState
    val guruPenggantiList: List<GuruPengganti> = when (currentGuruPenggantiState) {
        is GuruPenggantiUiState.Success -> currentGuruPenggantiState.data
        else -> emptyList()
    }

    val isLoading = currentGuruPenggantiState is GuruPenggantiUiState.Loading
    val errorMessage = when (currentGuruPenggantiState) {
        is GuruPenggantiUiState.Error -> (currentGuruPenggantiState as GuruPenggantiUiState.Error).message
        else -> null
    }

    val filteredGuruPengganti = guruPenggantiList.filter { gp ->
        (gp.kelas.contains(searchQuery, ignoreCase = true) ||
                gp.mata_pelajaran.contains(searchQuery, ignoreCase = true) ||
                (gp.guruPengganti?.name?.contains(searchQuery, ignoreCase = true) ?: false) ||
                (gp.guruAsli?.name?.contains(searchQuery, ignoreCase = true) ?: false))
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFF8F9FA))
                .padding(paddingValues)
        ) {
            // Header - Modern Design
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
                    Text(
                        text = "Guru Pengganti",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        fontSize = 24.sp
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Informasi guru pengganti untuk anda",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.White.copy(alpha = 0.85f),
                        fontSize = 13.sp
                    )

                    Spacer(modifier = Modifier.height(Spacing.md))

                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        label = { Text("Cari kelas, guru...", fontSize = 12.sp) },
                        leadingIcon = {
                            Icon(Icons.Default.Search, contentDescription = "Cari", modifier = Modifier.size(18.dp))
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(44.dp),
                        shape = RoundedCornerShape(10.dp),
                        singleLine = true,
                        textStyle = MaterialTheme.typography.bodySmall,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color.White,
                            unfocusedBorderColor = Color.White.copy(alpha = 0.5f),
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            cursorColor = Color.White
                        )
                    )
                }
            }

            // Content
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(Spacing.lg),
                verticalArrangement = Arrangement.spacedBy(Spacing.md)
            ) {
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
                } else if (errorMessage != null) {
                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(14.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = Color(0xFFFFEBEE)
                            )
                        ) {
                            Column(
                                modifier = Modifier.padding(Spacing.md),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Icon(
                                    Icons.Default.ErrorOutline,
                                    contentDescription = "Error",
                                    tint = Color(0xFFEF5350),
                                    modifier = Modifier.size(48.dp)
                                )
                                Spacer(modifier = Modifier.height(Spacing.sm))
                                Text(
                                    text = errorMessage,
                                    color = Color(0xFFEF5350),
                                    textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                                    style = MaterialTheme.typography.bodySmall
                                )
                            }
                        }
                    }
                } else if (filteredGuruPengganti.isEmpty()) {
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
                                    Icons.Default.PersonOff,
                                    contentDescription = "Tidak ada guru pengganti",
                                    tint = Color(0xFFCAD1DC),
                                    modifier = Modifier.size(48.dp)
                                )
                                Spacer(modifier = Modifier.height(Spacing.sm))
                                Text(
                                    text = if (searchQuery.isEmpty()) "Tidak ada guru pengganti" else "Guru pengganti tidak ditemukan",
                                    color = Color(0xFF718096),
                                    style = MaterialTheme.typography.bodySmall,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }
                    }
                } else {
                    items(filteredGuruPengganti) { guruPengganti ->
                        GuruPenggantiCardModern(
                            guruPengganti = guruPengganti,
                            onCardClick = {
                                selectedGuruPengganti = guruPengganti
                                showConfirmationDialog = true
                            }
                        )
                    }
                }
            }
        }
    }

    // Confirmation Dialog
    if (showConfirmationDialog && selectedGuruPengganti != null) {
        GuruPenggantiConfirmationDialog(
            guruPengganti = selectedGuruPengganti!!,
            onConfirm = {
                if (token != null && userId != null) {
                    viewModel.confirmReplacementTeacher(
                        token,
                        selectedGuruPengganti!!.id,
                        userId
                    )
                }
            },
            onDismiss = {
                showConfirmationDialog = false
                selectedGuruPengganti = null
            },
            isLoading = confirmationState is ConfirmationUiState.Loading
        )
    }
}

@Composable
fun GuruPenggantiCardModern(
    guruPengganti: GuruPengganti,
    onCardClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(enabled = true, onClick = onCardClick),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(Spacing.md)
        ) {
            // Guru Section - Guru Asli → Guru Pengganti
            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(Spacing.sm),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Guru Asli
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .background(
                            color = Color(0xFFF0F1F3),
                            shape = RoundedCornerShape(12.dp)
                        )
                        .padding(Spacing.sm),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        Icons.Default.Person,
                        contentDescription = "Guru Asli",
                        tint = Color(0xFF718096),
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "Guru Asli",
                        style = MaterialTheme.typography.labelSmall,
                        color = Color(0xFF718096),
                        fontSize = 9.sp
                    )
                    Text(
                        text = guruPengganti.guruAsli?.name ?: "-",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF2D3748),
                        maxLines = 1,
                        fontSize = 10.sp
                    )
                }

                // Arrow
                Icon(
                    Icons.Default.ArrowRightAlt,
                    contentDescription = "Diganti oleh",
                    tint = Color(0xFF5E72E4),
                    modifier = Modifier.size(24.dp)
                )

                // Guru Pengganti
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .background(
                            color = Color(0xFFE3F2FD),
                            shape = RoundedCornerShape(12.dp)
                        )
                        .padding(Spacing.sm),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        Icons.Default.CheckCircle,
                        contentDescription = "Guru Pengganti",
                        tint = Color(0xFF5E72E4),
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "Pengganti",
                        style = MaterialTheme.typography.labelSmall,
                        color = Color(0xFF5E72E4),
                        fontSize = 9.sp
                    )
                    Text(
                        text = guruPengganti.guruPengganti?.name ?: "-",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF5E72E4),
                        maxLines = 1,
                        fontSize = 10.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(Spacing.sm))
            Divider(color = Color(0xFFF0F1F3), thickness = 1.dp)
            Spacer(modifier = Modifier.height(Spacing.sm))

            // Info Row 1: Kelas & Keterangan
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(Spacing.sm),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.Default.School,
                        contentDescription = "Kelas",
                        tint = Color(0xFF718096),
                        modifier = Modifier.size(16.dp)
                    )
                    Text(
                        text = guruPengganti.kelas,
                        style = MaterialTheme.typography.labelSmall,
                        color = Color(0xFF4A5568),
                        fontWeight = FontWeight.Medium
                    )
                }

                // Keterangan if exists
                if (guruPengganti.keterangan?.isNotEmpty() == true) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(Spacing.sm),
                        verticalAlignment = Alignment.Top
                    ) {
                        Icon(
                            Icons.Default.Info,
                            contentDescription = "Keterangan",
                            tint = Color(0xFFF57C00),
                            modifier = Modifier.size(16.dp)
                        )
                        Text(
                            text = guruPengganti.keterangan,
                            style = MaterialTheme.typography.labelSmall,
                            color = Color(0xFFE65100),
                            fontSize = 10.sp,
                            maxLines = 2
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(Spacing.sm))

            // Info Row 2: Time & Tanggal
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(Spacing.md)
            ) {
                Row(
                    modifier = Modifier.weight(1f),
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.Default.Schedule,
                        contentDescription = "Jam",
                        tint = Color(0xFF718096),
                        modifier = Modifier.size(14.dp)
                    )
                    Text(
                        text = "${guruPengganti.jam_mulai} - ${guruPengganti.jam_selesai}",
                        style = MaterialTheme.typography.labelSmall,
                        color = Color(0xFF4A5568),
                        fontWeight = FontWeight.Medium
                    )
                }

                Row(
                    modifier = Modifier.weight(1f),
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.Default.CalendarToday,
                        contentDescription = "Tanggal",
                        tint = Color(0xFF718096),
                        modifier = Modifier.size(14.dp)
                    )
                    Text(
                        text = guruPengganti.tanggal,
                        style = MaterialTheme.typography.labelSmall,
                        color = Color(0xFF4A5568),
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            Spacer(modifier = Modifier.height(Spacing.md))

            // Button
            Button(
                onClick = onCardClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(40.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF5E72E4)),
                shape = RoundedCornerShape(10.dp)
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.Default.Visibility,
                        contentDescription = "Lihat",
                        modifier = Modifier.size(16.dp),
                        tint = Color.White
                    )
                    Text(
                        text = "Konfirmasi Kehadiran",
                        color = Color.White,
                        style = MaterialTheme.typography.labelMedium
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GuruPenggantiConfirmationDialog(
    guruPengganti: GuruPengganti,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
    isLoading: Boolean = false
) {
    BasicAlertDialog(
        onDismissRequest = onDismiss,
        modifier = Modifier
            .fillMaxWidth(0.9f)
            .background(Color(0xFF2A2A2A), RoundedCornerShape(16.dp))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(Spacing.lg)
        ) {
            // Header
            Text(
                text = "Konfirmasi Kehadiran Guru Pengganti",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )

            Spacer(modifier = Modifier.height(Spacing.md))

            // Content - Scrollable for small screens
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 400.dp),
                verticalArrangement = Arrangement.spacedBy(Spacing.sm)
            ) {
                DialogDetailItem(
                    label = "Guru Asli",
                    value = guruPengganti.guruAsli?.name ?: "-"
                )
                DialogDetailItem(
                    label = "Guru Pengganti",
                    value = guruPengganti.guruPengganti?.name ?: "-"
                )
                DialogDetailItem(
                    label = "Kelas",
                    value = guruPengganti.kelas
                )
                DialogDetailItem(
                    label = "Mata Pelajaran",
                    value = guruPengganti.mata_pelajaran
                )
                DialogDetailItem(
                    label = "Tanggal",
                    value = guruPengganti.tanggal
                )
                DialogDetailItem(
                    label = "Jam",
                    value = "${guruPengganti.jam_mulai} - ${guruPengganti.jam_selesai}"
                )
                DialogDetailItem(
                    label = "Ruang",
                    value = guruPengganti.ruang ?: "-"
                )
                DialogDetailItem(
                    label = "Keterangan",
                    value = guruPengganti.keterangan ?: "-"
                )
            }

            Spacer(modifier = Modifier.height(Spacing.lg))

            // Buttons
            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(Spacing.md)
            ) {
                OutlinedButton(
                    onClick = onDismiss,
                    modifier = Modifier
                        .weight(1f)
                        .height(40.dp),
                    enabled = !isLoading,
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text("Tutup")
                }

                Button(
                    onClick = onConfirm,
                    modifier = Modifier
                        .weight(1f)
                        .height(40.dp),
                    enabled = !isLoading,
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF51CF66)),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(20.dp),
                            color = Color.White,
                            strokeWidth = 2.dp
                        )
                    } else {
                        Text(
                            "Konfirmasi",
                            color = Color.White,
                            style = MaterialTheme.typography.labelMedium
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun DialogDetailItem(label: String, value: String) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFF1A1A2E), RoundedCornerShape(8.dp))
            .padding(Spacing.md)
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = Color.White.copy(alpha = 0.6f)
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = value,
            style = MaterialTheme.typography.bodySmall,
            fontWeight = FontWeight.SemiBold,
            color = Color.White
        )
    }
}

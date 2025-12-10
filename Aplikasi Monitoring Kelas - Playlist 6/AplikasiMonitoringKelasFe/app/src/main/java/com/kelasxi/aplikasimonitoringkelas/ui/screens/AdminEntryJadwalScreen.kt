package com.kelasxi.aplikasimonitoringkelas.ui.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import com.kelasxi.aplikasimonitoringkelas.data.model.ScheduleRequest
import com.kelasxi.aplikasimonitoringkelas.data.repository.AppRepositoryNew
import com.kelasxi.aplikasimonitoringkelas.ui.theme.Spacing
import com.kelasxi.aplikasimonitoringkelas.utils.SharedPrefManager
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminEntryJadwalScreen() {
    val context = LocalContext.current
    val sharedPrefManager = remember { SharedPrefManager.getInstance(context) }
    val token = sharedPrefManager.getToken()
    val repository = remember { AppRepositoryNew(RetrofitClient.apiService) }
    val scope = rememberCoroutineScope()

    var hari by remember { mutableStateOf("Senin") }
    var kelas by remember { mutableStateOf("10 RPL") }
    var mataPelajaran by remember { mutableStateOf("IPA") }
    var jamMulai by remember { mutableStateOf("") }
    var jamSelesai by remember { mutableStateOf("") }
    var ruangan by remember { mutableStateOf("") }
    var guruId by remember { mutableStateOf(1) }
    var isLoading by remember { mutableStateOf(false) }
    
    var guruList by remember { mutableStateOf(emptyList<Pair<Int, String>>()) }
    var showGuruDropdown by remember { mutableStateOf(false) }
    var selectedGuruName by remember { mutableStateOf("") }
    
    // Load guru list on composition
    LaunchedEffect(Unit) {
        if (token != null) {
            repository.getTeachers(token).onSuccess { response ->
                val gurus = response.data?.map { user -> Pair(user.id, user.name) } ?: emptyList()
                guruList = gurus
                if (gurus.isNotEmpty()) {
                    guruId = gurus[0].first
                    selectedGuruName = gurus[0].second
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
                                    Icons.Default.DateRange,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                        }
                        Column {
                            Text(
                                text = "Entry Jadwal",
                                style = MaterialTheme.typography.headlineSmall,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                            Text(
                                text = "Tambah jadwal pelajaran",
                                style = MaterialTheme.typography.bodySmall,
                                color = Color.White.copy(alpha = 0.8f)
                            )
                        }
                    }
                }
            }
        }

        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(Spacing.lg),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(Spacing.lg)) {
                    var hariExpanded by remember { mutableStateOf(false) }
                    Text("Hari", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(Spacing.sm))
                    ExposedDropdownMenuBox(
                        expanded = hariExpanded,
                        onExpandedChange = { hariExpanded = !hariExpanded }
                    ) {
                        TextField(
                            value = hari,
                            onValueChange = {},
                            readOnly = true,
                            leadingIcon = { Icon(Icons.Default.DateRange, null) },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = hariExpanded) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .menuAnchor(),
                            colors = TextFieldDefaults.colors(
                                unfocusedContainerColor = Color(0xFFF5F5F5),
                                focusedContainerColor = Color.White
                            )
                        )
                        ExposedDropdownMenu(
                            expanded = hariExpanded,
                            onDismissRequest = { hariExpanded = false }
                        ) {
                            listOf("Senin", "Selasa", "Rabu", "Kamis", "Jumat").forEach { day ->
                                DropdownMenuItem(
                                    text = { Text(day) },
                                    onClick = { hari = day; hariExpanded = false }
                                )
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(Spacing.md))

                    var kelasExpanded by remember { mutableStateOf(false) }
                    Text("Kelas", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(Spacing.sm))
                    ExposedDropdownMenuBox(
                        expanded = kelasExpanded,
                        onExpandedChange = { kelasExpanded = !kelasExpanded }
                    ) {
                        TextField(
                            value = kelas,
                            onValueChange = {},
                            readOnly = true,
                            leadingIcon = { Icon(Icons.Default.Home, null) },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = kelasExpanded) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .menuAnchor(),
                            colors = TextFieldDefaults.colors(
                                unfocusedContainerColor = Color(0xFFF5F5F5),
                                focusedContainerColor = Color.White
                            )
                        )
                        ExposedDropdownMenu(
                            expanded = kelasExpanded,
                            onDismissRequest = { kelasExpanded = false }
                        ) {
                            listOf("10 RPL", "11 RPL", "12 RPL").forEach { cls ->
                                DropdownMenuItem(
                                    text = { Text(cls) },
                                    onClick = { kelas = cls; kelasExpanded = false }
                                )
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(Spacing.md))

                    var mapelExpanded by remember { mutableStateOf(false) }
                    Text("Mata Pelajaran", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(Spacing.sm))
                    ExposedDropdownMenuBox(
                        expanded = mapelExpanded,
                        onExpandedChange = { mapelExpanded = !mapelExpanded }
                    ) {
                        TextField(
                            value = mataPelajaran,
                            onValueChange = {},
                            readOnly = true,
                            leadingIcon = { Icon(Icons.AutoMirrored.Filled.MenuBook, null) },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = mapelExpanded) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .menuAnchor(),
                            colors = TextFieldDefaults.colors(
                                unfocusedContainerColor = Color(0xFFF5F5F5),
                                focusedContainerColor = Color.White
                            )
                        )
                        ExposedDropdownMenu(
                            expanded = mapelExpanded,
                            onDismissRequest = { mapelExpanded = false }
                        ) {
                            listOf("IPA", "IPS", "Bahasa", "Matematika").forEach { subject ->
                                DropdownMenuItem(
                                    text = { Text(subject) },
                                    onClick = { mataPelajaran = subject; mapelExpanded = false }
                                )
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(Spacing.md))

                    var guruExpanded by remember { mutableStateOf(false) }
                    Text("Nama Guru", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(Spacing.sm))
                    Box(modifier = Modifier.fillMaxWidth()) {
                        TextField(
                            value = selectedGuruName,
                            onValueChange = {},
                            readOnly = true,
                            leadingIcon = { Icon(Icons.Default.Person, null) },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = showGuruDropdown) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { showGuruDropdown = true },
                            colors = TextFieldDefaults.colors(
                                unfocusedContainerColor = Color(0xFFF5F5F5),
                                focusedContainerColor = Color.White
                            )
                        )
                        DropdownMenu(
                            expanded = showGuruDropdown,
                            onDismissRequest = { showGuruDropdown = false },
                            modifier = Modifier.fillMaxWidth(0.9f)
                        ) {
                            guruList.forEach { (id, name) ->
                                DropdownMenuItem(
                                    text = { Text(name) },
                                    onClick = {
                                        guruId = id
                                        selectedGuruName = name
                                        showGuruDropdown = false
                                    }
                                )
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(Spacing.md))

                    TextField(
                        value = jamMulai,
                        onValueChange = { jamMulai = it },
                        label = { Text("Jam Mulai (contoh: 07:00)") },
                        leadingIcon = { Icon(Icons.Default.Schedule, null) },
                        modifier = Modifier.fillMaxWidth(),
                        colors = TextFieldDefaults.colors(
                            unfocusedContainerColor = Color(0xFFF5F5F5),
                            focusedContainerColor = Color.White
                        )
                    )
                    Spacer(modifier = Modifier.height(Spacing.md))

                    TextField(
                        value = jamSelesai,
                        onValueChange = { jamSelesai = it },
                        label = { Text("Jam Selesai (contoh: 08:00)") },
                        leadingIcon = { Icon(Icons.Default.Schedule, null) },
                        modifier = Modifier.fillMaxWidth(),
                        colors = TextFieldDefaults.colors(
                            unfocusedContainerColor = Color(0xFFF5F5F5),
                            focusedContainerColor = Color.White
                        )
                    )
                    Spacer(modifier = Modifier.height(Spacing.md))

                    TextField(
                        value = ruangan,
                        onValueChange = { ruangan = it },
                        label = { Text("Ruangan (opsional)") },
                        leadingIcon = { Icon(Icons.Default.LocationOn, null) },
                        modifier = Modifier.fillMaxWidth(),
                        colors = TextFieldDefaults.colors(
                            unfocusedContainerColor = Color(0xFFF5F5F5),
                            focusedContainerColor = Color.White
                        )
                    )

                    Spacer(modifier = Modifier.height(Spacing.lg))
                    Button(
                        onClick = {
                            if (jamMulai.isEmpty() || jamSelesai.isEmpty()) {
                                Toast.makeText(context, "Jam mulai dan jam selesai harus diisi", Toast.LENGTH_SHORT).show()
                                return@Button
                            }
                            isLoading = true
                            scope.launch {
                                try {
                                    if (token != null) {
                                        val request = ScheduleRequest(
                                            hari = hari,
                                            kelas = kelas,
                                            mata_pelajaran = mataPelajaran,
                                            guru_id = guruId,
                                            jam_mulai = jamMulai,
                                            jam_selesai = jamSelesai,
                                            ruang = ruangan.ifEmpty { null }
                                        )
                                        repository.createSchedule(token, request).onSuccess {
                                            Toast.makeText(context, "Jadwal berhasil ditambahkan", Toast.LENGTH_SHORT).show()
                                            jamMulai = ""
                                            jamSelesai = ""
                                            ruangan = ""
                                        }.onFailure { error ->
                                            Toast.makeText(context, "Error: ${error.message}", Toast.LENGTH_SHORT).show()
                                        }
                                    }
                                } catch (e: Exception) {
                                    Toast.makeText(context, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                                } finally {
                                    isLoading = false
                                }
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1F3A7F)),
                        enabled = !isLoading
                    ) {
                        if (isLoading) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(20.dp),
                                color = Color.White,
                                strokeWidth = 2.dp
                            )
                        } else {
                            Text("Simpan Jadwal", fontWeight = FontWeight.Bold)
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

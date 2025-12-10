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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kelasxi.aplikasimonitoringkelas.data.api.RetrofitClient
import com.kelasxi.aplikasimonitoringkelas.data.model.Teacher
import com.kelasxi.aplikasimonitoringkelas.data.repository.AppRepositoryNew
import com.kelasxi.aplikasimonitoringkelas.ui.theme.*
import com.kelasxi.aplikasimonitoringkelas.utils.SharedPrefManager
import kotlinx.coroutines.launch

@Composable
fun DaftarGuruKurikulumPage() {
    val context = LocalContext.current
    val sharedPrefManager = remember { SharedPrefManager.getInstance(context) }
    val token = sharedPrefManager.getToken()
    val repository = remember { AppRepositoryNew(RetrofitClient.apiService) }
    val scope = rememberCoroutineScope()

    var teacherList by remember { mutableStateOf<List<Teacher>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var searchQuery by remember { mutableStateOf("") }
    var filterSubject by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(Unit) {
        if (token != null) {
            scope.launch {
                try {
                    // Load teachers
                    repository.getTeachers(token).onSuccess { response ->
                        teacherList = response.data?.map { user ->
                            Teacher(
                                id = user.id,
                                name = user.name,
                                email = user.email,
                                mata_pelajaran = user.mata_pelajaran
                            )
                        } ?: emptyList()
                    }.onFailure { error ->
                        Toast.makeText(context, "Error: ${error.message}", Toast.LENGTH_SHORT).show()
                    }
                    
                    isLoading = false
                } catch (e: Exception) {
                    Toast.makeText(context, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                    isLoading = false
                }
            }
        }
    }

    val filteredTeachers = teacherList.filter { teacher ->
        val matchesSearch = teacher.name?.contains(searchQuery, ignoreCase = true) ?: false
        val matchesSubject = filterSubject == null || teacher.mata_pelajaran == filterSubject
        matchesSearch && matchesSubject
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
            Text(
                text = "Daftar Guru",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = Spacing.md)
            )
        }

        // Search Bar
        item {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                label = { Text("Cari guru...") },
                leadingIcon = {
                    Icon(Icons.Default.Search, contentDescription = "Cari")
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .padding(bottom = Spacing.md),
                shape = RoundedCornerShape(12.dp),
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = SMKPrimary,
                    unfocusedBorderColor = NeutralGray300
                )
            )
        }

        // Stats Card
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = Spacing.lg),
                horizontalArrangement = Arrangement.spacedBy(Spacing.md)
            ) {
                StatCard2(
                    label = "Total Guru",
                    value = teacherList.size,
                    color = SMKPrimary,
                    icon = Icons.Default.Person,
                    modifier = Modifier.weight(1f)
                )
                
                StatCard2(
                    label = "Aktif Hari Ini",
                    value = teacherList.count { it.email?.contains("@") ?: false },
                    color = SMKSecondary,
                    icon = Icons.Default.CheckCircle,
                    modifier = Modifier.weight(1f)
                )
            }
        }

        // Teacher List
        if (filteredTeachers.isEmpty()) {
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
                            Icons.Default.PersonOff,
                            contentDescription = "No Teachers",
                            tint = NeutralGray400,
                            modifier = Modifier.size(48.dp)
                        )
                        Spacer(modifier = Modifier.height(Spacing.md))
                        Text(
                            text = if (searchQuery.isEmpty()) 
                                "Belum ada data guru" 
                            else 
                                "Guru tidak ditemukan",
                            style = MaterialTheme.typography.bodyMedium,
                            color = NeutralGray500
                        )
                    }
                }
            }
        } else {
            items(filteredTeachers) { teacher ->
                TeacherCard(teacher)
                Spacer(modifier = Modifier.height(Spacing.md))
            }
        }
    }
}

@Composable
fun StatCard2(
    label: String,
    value: Int,
    color: Color,
    icon: ImageVector,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .height(90.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = color.copy(alpha = 0.1f)
        ),
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            color.copy(alpha = 0.2f)
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(Spacing.md),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = value.toString(),
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = color,
                    fontSize = 22.sp
                )
                Text(
                    text = label,
                    style = MaterialTheme.typography.labelSmall,
                    color = NeutralGray600,
                    fontSize = 10.sp
                )
            }
            
            Box(
                modifier = Modifier
                    .size(50.dp)
                    .background(
                        color = color.copy(alpha = 0.15f),
                        shape = RoundedCornerShape(25.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    icon,
                    contentDescription = label,
                    tint = color,
                    modifier = Modifier.size(24.dp)
                )
            }
        }
    }
}

@Composable
fun TeacherCard(teacher: Teacher) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = SMKSurface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(Spacing.md)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Avatar & Name
                Row(
                    modifier = Modifier.weight(1f),
                    horizontalArrangement = Arrangement.spacedBy(Spacing.md),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .background(
                                color = SMKPrimary.copy(alpha = 0.2f),
                                shape = RoundedCornerShape(24.dp)
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = (teacher.name?.firstOrNull() ?: "G").toString(),
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold,
                            color = SMKPrimary,
                            fontSize = 18.sp
                        )
                    }
                    
                    Column {
                        Text(
                            text = teacher.name ?: "Guru",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = SMKOnSurface
                        )
                        Text(
                            text = teacher.mata_pelajaran ?: "-",
                            style = MaterialTheme.typography.bodySmall,
                            color = NeutralGray600
                        )
                    }
                }
                
                // Status Indicator
                Box(
                    modifier = Modifier
                        .size(12.dp)
                        .background(
                            color = Color(0xFF4CAF50),
                            shape = RoundedCornerShape(6.dp)
                        )
                )
            }

            Spacer(modifier = Modifier.height(Spacing.md))

            // Email Section
            if (!teacher.email.isNullOrEmpty()) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(Spacing.sm),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.Default.Email,
                        contentDescription = "Email",
                        tint = NeutralGray500,
                        modifier = Modifier.size(16.dp)
                    )
                    Text(
                        text = teacher.email ?: "-",
                        style = MaterialTheme.typography.labelSmall,
                        color = NeutralGray600,
                        fontSize = 11.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(Spacing.sm))

            // Details Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(Spacing.sm)
            ) {
                DetailChip(
                    icon = Icons.Default.Schedule,
                    label = "Jadwal",
                    value = "4",
                    modifier = Modifier.weight(1f)
                )
                
                DetailChip(
                    icon = Icons.Default.CheckCircle,
                    label = "Kehadiran",
                    value = "92%",
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
fun DetailChip(
    icon: ImageVector,
    label: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.height(48.dp),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(
            containerColor = SMKPrimary.copy(alpha = 0.05f)
        ),
        border = androidx.compose.foundation.BorderStroke(
            0.5.dp,
            SMKPrimary.copy(alpha = 0.1f)
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(Spacing.sm),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                icon,
                contentDescription = label,
                tint = SMKPrimary,
                modifier = Modifier.size(14.dp)
            )
            Spacer(modifier = Modifier.width(Spacing.xs))
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = label,
                    style = MaterialTheme.typography.labelSmall,
                    color = NeutralGray600,
                    fontSize = 8.sp
                )
                Text(
                    text = value,
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    color = SMKPrimary,
                    fontSize = 10.sp
                )
            }
        }
    }
}

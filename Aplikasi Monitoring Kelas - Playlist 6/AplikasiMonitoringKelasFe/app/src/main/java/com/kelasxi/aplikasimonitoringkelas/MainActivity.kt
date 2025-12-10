package com.kelasxi.aplikasimonitoringkelas

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.kelasxi.aplikasimonitoringkelas.ui.theme.AplikasiMonitoringKelasTheme
import com.kelasxi.aplikasimonitoringkelas.ui.theme.Spacing
import com.kelasxi.aplikasimonitoringkelas.viewmodel.AuthViewModel
import com.kelasxi.aplikasimonitoringkelas.utils.SharedPrefManager
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AplikasiMonitoringKelasTheme {
                LoginScreen()
            }
        }
    }
}

data class DemoAccount(
    val email: String,
    val password: String,
    val role: String,
    val name: String
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(viewModel: AuthViewModel = viewModel()) {
    val context = LocalContext.current
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var showDemoAccounts by remember { mutableStateOf(true) }

    val isLoading by viewModel.isLoading
    val loginSuccess by viewModel.loginSuccess
    val errorMessage by viewModel.errorMessage
    val user by viewModel.user
    val token by viewModel.token

    val sharedPrefManager = remember { SharedPrefManager.getInstance(context) }

    // Demo accounts sesuai role dari backend
    val demoAccounts = listOf(
        DemoAccount(
            email = "siswa@sekolah.com",
            password = "password123",
            role = "siswa",
            name = "Siswa Dummy"
        ),
        DemoAccount(
            email = "kurikulum@sekolah.com",
            password = "password123",
            role = "kurikulum",
            name = "Guru Kurikulum"
        ),
        DemoAccount(
            email = "admin@sekolah.com",
            password = "password123",
            role = "admin",
            name = "Admin Sekolah"
        ),
        DemoAccount(
            email = "kepala@sekolah.com",
            password = "password123",
            role = "kepala_sekolah",
            name = "Kepala Sekolah"
        )
    )

    // Handle login success
    LaunchedEffect(loginSuccess) {
        if (loginSuccess && user != null && token != null) {
            sharedPrefManager.saveLoginData(
                token = token!!,
                userId = user!!.id,
                name = user!!.name,
                email = user!!.email,
                role = user!!.role,
                userClass = user!!.kelas
            )

            val intent = when (user!!.role.lowercase()) {
                "siswa" -> Intent(context, SiswaActivity::class.java)
                "guru", "kurikulum" -> Intent(context, KurikulumActivity::class.java)
                "admin" -> Intent(context, AdminActivity::class.java)
                "kepala_sekolah" -> Intent(context, KepalaSekolahActivity::class.java)
                else -> {
                    Toast.makeText(context, "Role tidak dikenali", Toast.LENGTH_LONG).show()
                    null
                }
            }

            intent?.let {
                context.startActivity(it)
                if (context is ComponentActivity) {
                    context.finish()
                }
            }

            viewModel.resetLoginState()
        }
    }

    LaunchedEffect(errorMessage) {
        errorMessage?.let { message ->
            Toast.makeText(context, message, Toast.LENGTH_LONG).show()
            viewModel.clearError()
        }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF5F5F5)),
        horizontalAlignment = Alignment.CenterHorizontally,
        contentPadding = PaddingValues(Spacing.lg)
    ) {
        item {
            Spacer(modifier = Modifier.height(Spacing.xl))
        }

        // Header Logo
        item {
            Surface(
                color = Color(0xFF1F3A7F),
                shape = RoundedCornerShape(50),
                modifier = Modifier.size(80.dp)
            ) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier.fillMaxSize()
                ) {
                    Icon(
                        Icons.Default.School,
                        contentDescription = "School",
                        tint = Color.White,
                        modifier = Modifier.size(40.dp)
                    )
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(Spacing.md))
            Text(
                text = "Monitoring Kelas",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF1F3A7F)
            )
            Text(
                text = "Sistem Monitoring Kehadiran",
                style = MaterialTheme.typography.bodySmall,
                color = Color.Gray
            )
        }

        item {
            Spacer(modifier = Modifier.height(Spacing.xl))
        }

        // Login Form Card
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = Spacing.md),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(Spacing.lg),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Login",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1F3A7F)
                    )

                    Spacer(modifier = Modifier.height(Spacing.lg))

                    TextField(
                        value = email,
                        onValueChange = { email = it },
                        label = { Text("Email") },
                        leadingIcon = { Icon(Icons.Default.Email, null) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                        colors = TextFieldDefaults.colors(
                            unfocusedContainerColor = Color(0xFFF5F5F5),
                            focusedContainerColor = Color.White,
                            unfocusedIndicatorColor = Color.Transparent,
                            focusedIndicatorColor = Color(0xFF1F3A7F)
                        ),
                        shape = RoundedCornerShape(8.dp)
                    )

                    Spacer(modifier = Modifier.height(Spacing.md))

                    TextField(
                        value = password,
                        onValueChange = { password = it },
                        label = { Text("Password") },
                        leadingIcon = { Icon(Icons.Default.Lock, null) },
                        visualTransformation = PasswordVisualTransformation(),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                        colors = TextFieldDefaults.colors(
                            unfocusedContainerColor = Color(0xFFF5F5F5),
                            focusedContainerColor = Color.White,
                            unfocusedIndicatorColor = Color.Transparent,
                            focusedIndicatorColor = Color(0xFF1F3A7F)
                        ),
                        shape = RoundedCornerShape(8.dp)
                    )

                    Spacer(modifier = Modifier.height(Spacing.lg))

                    Button(
                        onClick = {
                            if (email.isEmpty() || password.isEmpty()) {
                                Toast.makeText(context, "Email dan password harus diisi", Toast.LENGTH_SHORT).show()
                                return@Button
                            }
                            viewModel.login(email, password)
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
                            Text("Login", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                        }
                    }
                }
            }
        }

        // Demo Accounts Section
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = Spacing.lg),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Akun Demo",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1F3A7F)
                )
                IconButton(
                    onClick = { showDemoAccounts = !showDemoAccounts },
                    modifier = Modifier.size(28.dp)
                ) {
                    Icon(
                        if (showDemoAccounts) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                        contentDescription = "Toggle Demo Accounts",
                        tint = Color(0xFF1F3A7F)
                    )
                }
            }
        }

        // Demo Accounts List
        if (showDemoAccounts) {
            items(demoAccounts) { account ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = Spacing.sm),
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
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = account.name,
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp,
                                color = Color(0xFF1F3A7F)
                            )
                            Text(
                                text = account.email,
                                fontSize = 10.sp,
                                color = Color.Gray
                            )
                            Surface(
                                color = when (account.role) {
                                    "siswa" -> Color(0xFF4CAF50).copy(alpha = 0.2f)
                                    "guru", "kurikulum" -> Color(0xFF2196F3).copy(alpha = 0.2f)
                                    "admin" -> Color(0xFFFF9800).copy(alpha = 0.2f)
                                    "kepala_sekolah" -> Color(0xFF9C27B0).copy(alpha = 0.2f)
                                    else -> Color.Gray.copy(alpha = 0.2f)
                                },
                                shape = RoundedCornerShape(4.dp),
                                modifier = Modifier.padding(top = Spacing.xs)
                            ) {
                                Text(
                                    text = account.role.replace("_", " ").uppercase(),
                                    fontSize = 8.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = when (account.role) {
                                        "siswa" -> Color(0xFF4CAF50)
                                        "guru", "kurikulum" -> Color(0xFF2196F3)
                                        "admin" -> Color(0xFFFF9800)
                                        "kepala_sekolah" -> Color(0xFF9C27B0)
                                        else -> Color.Gray
                                    },
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }

                        Button(
                            onClick = {
                                email = account.email
                                password = account.password
                                viewModel.login(account.email, account.password)
                            },
                            modifier = Modifier
                                .height(36.dp)
                                .width(70.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1F3A7F)),
                            contentPadding = PaddingValues(4.dp),
                            enabled = !isLoading
                        ) {
                            Text("Login", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(Spacing.xl))
        }
    }
}

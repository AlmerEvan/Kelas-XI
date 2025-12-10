package com.example.firebasetutorialapp

import android.Manifest
import android.os.Build
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.util.Log
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.crashlytics.ktx.crashlytics
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.ktx.Firebase


class MainActivity : AppCompatActivity() {

    // 1. Deklarasi Instance Firebase Services
    private lateinit var analytics: FirebaseAnalytics

    // Permission launcher untuk notifikasi (Android 13+)
    private val notificationPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            Log.d("FCM_PERMISSION", "Notification permission granted")
        } else {
            Log.d("FCM_PERMISSION", "Notification permission denied")
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // 2. Inisialisasi Firebase Services
        analytics = Firebase.analytics

        // 3. Request notification permission untuk Android 13+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
        }

        // 4. Get FCM Token
        createFCMToken()

        val logEventButton: Button = findViewById(R.id.log_event_button)
        val crashButton: Button = findViewById(R.id.crash_button)

        // 5. Set Listener untuk Analytics Button
        logEventButton.setOnClickListener {
            logCustomEvent()
        }

        // 6. Set Listener untuk Crash Button
        crashButton.setOnClickListener {
            simulateFatalCrash()
        }
    }

    private fun createFCMToken() {
        // Dapatkan FCM Token
        FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
            if (!task.isSuccessful) {
                Log.w("FCM_TOKEN", "Fetching FCM token failed", task.exception)
                updateTokenUI("Error fetching token")
                return@addOnCompleteListener
            }

            // Get token
            val token = task.result
            Log.d("FCM_TOKEN", "Token: $token")
            
            // Tampilkan token untuk testing
            Log.i("FCM_TOKEN_INFO", "Use this token to send test messages: $token")
            
            // PENTING: Simpan token ke database/backend Anda di sini
            // Contoh: updateBackendWithToken(token)
            
            // Tampilkan di UI
            updateTokenUI(token)
        }
    }

    private fun updateTokenUI(token: String) {
        // Update TextView dengan FCM Token
        val tokenTextView = findViewById<TextView>(R.id.tv_fcm_token)
        tokenTextView.text = "FCM Token:\n$token"
        Log.d("FCM_UI", "Token displayed in UI")
    }

    private fun logCustomEvent() {
        // Catat Custom Event saat tombol diklik
        val bundle = Bundle().apply {
            putString("item_id", "main_button_1")
            putString("item_name", "Log Event Button")
            putString("content_type", "button")
        }
        
        analytics.logEvent("button_tapped", bundle)
        
        // Output konfirmasi ke Logcat
        Log.d("FirebaseAnalytics", "Analytic Event Logged: button_tapped")
        Log.d("FirebaseAnalytics", "Parameters: item_id=main_button_1, item_name=Log Event Button, content_type=button")
    }

    private fun simulateFatalCrash() {
        // Catat pesan ke Crashlytics sebelum crash
        Firebase.crashlytics.log("User clicked Crash button - Simulating fatal crash")
        
        Log.d("Crashlytics", "About to throw RuntimeException for crash simulation")
        
        // Lempar exception untuk memicu crash fatal
        throw RuntimeException("Test Crash - Firebase Crashlytics Simulation")
    }
}
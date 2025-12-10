package com.example.firebasetutorialapp

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import kotlin.random.Random

class MyFirebaseMessagingService : FirebaseMessagingService() {

    companion object {
        private const val CHANNEL_ID = "firebase_notification_channel"
        private const val CHANNEL_NAME = "Firebase Notifications"
    }

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        // Panggil ketika token baru dibuat (misal: setelah uninstall/reinstall)
        Log.d("FCM_TOKEN_UPDATE", "Refreshed token: $token")
        
        // Kirim token ke server backend jika diperlukan
        sendTokenToServer(token)
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)
        
        Log.d("FCM_MESSAGE", "Message received from: ${remoteMessage.from}")

        // Periksa jika pesan memiliki notification payload
        remoteMessage.notification?.let {
            val title = it.title ?: "Firebase Notification"
            val body = it.body ?: "You have a new message"
            showNotification(title, body)
        }

        // Periksa jika pesan memiliki data payload
        if (remoteMessage.data.isNotEmpty()) {
            Log.d("FCM_DATA", "Message data: ${remoteMessage.data}")
        }
    }

    private fun showNotification(title: String, messageBody: String) {
        // 1. Buat Intent untuk membuka MainActivity ketika notifikasi diklik
        val intent = Intent(this, MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        
        val pendingIntent = PendingIntent.getActivity(
            this, 0, intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        // 2. Buat Notification Channel (Wajib untuk Android 8.0/API 26+)
        createNotificationChannel()

        // 3. Buat NotificationCompat.Builder dengan channel ID
        val notificationBuilder = NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher_foreground) // Ganti dengan icon aplikasi Anda
            .setContentTitle(title)
            .setContentText(messageBody)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .setPriority(NotificationCompat.PRIORITY_HIGH)

        // 4. Tampilkan notifikasi
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val notificationId = Random.nextInt(0, 10000)
        notificationManager.notify(notificationId, notificationBuilder.build())
        
        Log.d("FCM_NOTIFICATION", "Notification displayed: ID=$notificationId, Title=$title, Body=$messageBody")
    }

    private fun createNotificationChannel() {
        // Buat notification channel hanya untuk Android 8.0 (API 26) ke atas
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel(CHANNEL_ID, CHANNEL_NAME, importance).apply {
                description = "Receive Firebase Cloud Messaging notifications"
            }
            
            val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun sendTokenToServer(token: String) {
        // TODO: Implementasi pengiriman token ke server backend Anda
        // Contoh: POST ke API endpoint dengan token
        Log.d("FCM_BACKEND", "TODO: Send token to backend server: $token")
    }
}

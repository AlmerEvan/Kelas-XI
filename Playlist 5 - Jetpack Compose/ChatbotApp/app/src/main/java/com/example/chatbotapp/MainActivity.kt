package com.example.chatbotapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import com.example.chatbotapp.ui.ChatScreen
import com.example.chatbotapp.ui.theme.ChatBotAppTheme
import com.example.chatbotapp.BuildConfig

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ChatBotAppTheme {
                Surface(color = MaterialTheme.colorScheme.background) {
                    ChatScreen(apiKey = BuildConfig.GENERATIVE_AI_API_KEY)
                }
            }
        }
    }
}
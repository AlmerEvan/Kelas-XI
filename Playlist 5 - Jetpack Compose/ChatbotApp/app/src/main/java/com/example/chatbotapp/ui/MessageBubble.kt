package com.example.chatbotapp.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.shape.RoundedCornerShape
import com.example.chatbotapp.model.ChatMessage

@Composable
fun MessageBubble(message: ChatMessage) {
    val bubbleColor = if (message.isUser) Color(0xFF0D47A1) else Color(0xFFE0E0E0)
    val textColor = if (message.isUser) Color.White else Color.Black
    val alignment = if (message.isUser) Arrangement.End else Arrangement.Start

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp),
        horizontalArrangement = alignment
    ) {
        Text(
            text = message.text,
            color = textColor,
            modifier = Modifier
                .background(bubbleColor, RoundedCornerShape(12.dp))
                .padding(12.dp)
                .widthIn(max = 250.dp),
            textAlign = if (message.isUser) TextAlign.End else TextAlign.Start
        )
    }
}

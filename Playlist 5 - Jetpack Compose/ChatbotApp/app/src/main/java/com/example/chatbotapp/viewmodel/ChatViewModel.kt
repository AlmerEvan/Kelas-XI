package com.example.chatbotapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.chatbotapp.model.ChatMessage
import com.google.ai.client.generativeai.GenerativeModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ChatViewModel(apiKey: String) : ViewModel() {

    private val _messages = MutableStateFlow<List<ChatMessage>>(emptyList())
    val messages: StateFlow<List<ChatMessage>> = _messages

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    // FIX 1: pakai model terbaru
    private val model = GenerativeModel(
        modelName = "gemini-1.5-flash-latest",
        apiKey = apiKey
    )

    // FIX 2: gunakan startChat() untuk percakapan
    private val chat = model.startChat()

    fun sendMessage(prompt: String) {
        if (prompt.isBlank()) return

        // Add user message
        _messages.value = _messages.value + ChatMessage(prompt, true)
        _isLoading.value = true

        viewModelScope.launch {
            try {
                // FIX 3: gunakan chat.sendMessage(), bukan model.generateContent()
                val response = chat.sendMessage(prompt)

                val responseText = response.text ?: "No response from AI"
                _messages.value = _messages.value + ChatMessage(responseText, false)

            } catch (e: Exception) {
                _messages.value = _messages.value + ChatMessage("Error: ${e.localizedMessage}", false)
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun clearChat() {
        _messages.value = emptyList()
    }
}

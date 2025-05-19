package com.example.tbssmarthub.ui.chatbot

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.speech.tts.TextToSpeech
import android.util.Base64
import android.util.Log
import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.util.*

class ChatViewModel : ViewModel() {
    // OpenAI client
    private val openAIClient = OpenAIClient("YOUR_OPENAI_API_KEY") // Replace with your actual API key

    // Message list
    private val _messages = mutableStateListOf<ChatMessage>()
    val messages: List<ChatMessage> = _messages

    // Loading state
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    // Text-to-speech instance
    private var textToSpeech: TextToSpeech? = null

    // Initialize TTS
    fun initTextToSpeech(context: Context, onInit: (Boolean) -> Unit) {
        textToSpeech = TextToSpeech(context) { status ->
            val isSuccess = status == TextToSpeech.SUCCESS
            onInit(isSuccess)
        }
    }

    // Send a text message
    fun sendTextMessage(content: String) {
        if (content.isBlank()) return

        // Add user message to UI
        val userMessage = ChatMessage(
            content = content,
            isFromUser = true
        )
        _messages.add(userMessage)

        // Get AI response
        _isLoading.value = true
        viewModelScope.launch {
            try {
                // Add a placeholder for the AI's response
                val aiMessageId = UUID.randomUUID().toString()
                _messages.add(ChatMessage(
                    id = aiMessageId,
                    content = "Thinking...",
                    isFromUser = false
                ))

                // Get response from OpenAI
                val response = openAIClient.sendMessage(content)

                // Replace placeholder with actual response
                val responseIndex = _messages.indexOfFirst { it.id == aiMessageId }
                if (responseIndex != -1) {
                    _messages[responseIndex] = ChatMessage(
                        id = aiMessageId,
                        content = response,
                        isFromUser = false
                    )
                }
            } catch (e: Exception) {
                Log.e("ChatViewModel", "Error sending message", e)
                _messages.add(ChatMessage(
                    content = "Sorry, I encountered an error. Please try again.",
                    isFromUser = false
                ))
            } finally {
                _isLoading.value = false
            }
        }
    }

    // Send an image message
    fun sendImageMessage(uri: Uri, context: Context, prompt: String = "Please analyze this image") {
        // Add user message with image to UI
        val userMessage = ChatMessage(
            content = prompt,
            isFromUser = true,
            imageUri = uri
        )
        _messages.add(userMessage)

        // Process image and get AI response
        _isLoading.value = true
        viewModelScope.launch {
            try {
                // Add a placeholder for the AI's response
                val aiMessageId = UUID.randomUUID().toString()
                _messages.add(ChatMessage(
                    id = aiMessageId,
                    content = "Analyzing image...",
                    isFromUser = false
                ))

                // Convert image to base64
                val inputStream = context.contentResolver.openInputStream(uri)
                val bytes = inputStream?.readBytes()
                inputStream?.close()

                if (bytes != null) {
                    val base64Image = Base64.encodeToString(bytes, Base64.DEFAULT)

                    // Get response from OpenAI Vision API
                    val response = openAIClient.analyzeImage(base64Image, prompt)

                    // Replace placeholder with actual response
                    val responseIndex = _messages.indexOfFirst { it.id == aiMessageId }
                    if (responseIndex != -1) {
                        _messages[responseIndex] = ChatMessage(
                            id = aiMessageId,
                            content = response,
                            isFromUser = false
                        )
                    }
                } else {
                    throw Exception("Could not read image data")
                }
            } catch (e: Exception) {
                Log.e("ChatViewModel", "Error processing image", e)
                _messages.add(ChatMessage(
                    content = "Sorry, I couldn't analyze the image. Please try again.",
                    isFromUser = false
                ))
            } finally {
                _isLoading.value = false
            }
        }
    }

    // Speak text using TTS
    fun speakText(text: String): Boolean {
        return textToSpeech?.let {
            if (it.isSpeaking) {
                it.stop()
            }
            it.speak(text, TextToSpeech.QUEUE_FLUSH, null, "MessageId_${UUID.randomUUID()}")
            true
        } ?: false
    }

    // Save conversation to local storage
    fun saveConversation(context: Context) {
        viewModelScope.launch {
            try {
                val fileName = "conversation_${System.currentTimeMillis()}.json"
                val file = File(context.filesDir, fileName)

                // Create a JSON array of messages
                val jsonArray = org.json.JSONArray()
                messages.forEach { message ->
                    val jsonObject = org.json.JSONObject()
                    jsonObject.put("id", message.id)
                    jsonObject.put("content", message.content)
                    jsonObject.put("isFromUser", message.isFromUser)
                    jsonObject.put("timestamp", message.timestamp)
                    // We don't save image URIs as they might not be valid later
                    jsonArray.put(jsonObject)
                }

                // Write to file
                FileOutputStream(file).use { output ->
                    output.write(jsonArray.toString().toByteArray())
                }

                Log.d("ChatViewModel", "Conversation saved to $fileName")
            } catch (e: Exception) {
                Log.e("ChatViewModel", "Error saving conversation", e)
            }
        }
    }

    // Load conversation from local storage
    fun loadConversation(context: Context, fileName: String) {
        viewModelScope.launch {
            try {
                val file = File(context.filesDir, fileName)
                if (!file.exists()) {
                    Log.e("ChatViewModel", "Conversation file not found: $fileName")
                    return@launch
                }

                val jsonString = file.readText()
                val jsonArray = org.json.JSONArray(jsonString)

                // Clear current messages
                _messages.clear()

                // Parse JSON and add messages
                for (i in 0 until jsonArray.length()) {
                    val jsonObject = jsonArray.getJSONObject(i)
                    val message = ChatMessage(
                        id = jsonObject.getString("id"),
                        content = jsonObject.getString("content"),
                        isFromUser = jsonObject.getBoolean("isFromUser"),
                        timestamp = jsonObject.getLong("timestamp")
                    )
                    _messages.add(message)
                }

                Log.d("ChatViewModel", "Loaded ${_messages.size} messages from $fileName")
            } catch (e: Exception) {
                Log.e("ChatViewModel", "Error loading conversation", e)
            }
        }
    }

    // Clear conversation
    fun clearConversation() {
        _messages.clear()
        openAIClient.clearConversation()
    }

    override fun onCleared() {
        super.onCleared()
        textToSpeech?.stop()
        textToSpeech?.shutdown()
    }
}

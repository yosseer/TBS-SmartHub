package com.example.tbssmarthub.ui.chatbot

import android.util.Log
import com.example.tbssmarthub.ui.chatbot.ChatMessage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.io.IOException
import java.util.concurrent.TimeUnit

class OpenAIClient(private val apiKey: String) {
    private val client = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .build()

    private val baseUrl = "https://api.openai.com/v1/chat/completions"

    // Store conversation history
    private val conversationHistory = mutableListOf<Map<String, String>>()

    // Initialize with system message for university context
    init {
        conversationHistory.add(mapOf(
            "role" to "system",
            "content" to "You are an AI assistant for university students. " +
                    "You specialize in helping with academic questions, research, " +
                    "study techniques, and university-related problems. " +
                    "Provide concise, accurate, and helpful responses. " +
                    "If you're unsure about something, acknowledge it rather than providing incorrect information."
        ))
    }

    suspend fun sendMessage(userMessage: String): String {
        // Add user message to history
        conversationHistory.add(mapOf("role" to "user", "content" to userMessage))

        // Prepare request JSON
        val requestJson = JSONObject().apply {
            put("model", "gpt-4")
            put("messages", JSONArray().apply {
                conversationHistory.forEach { message ->
                    put(JSONObject().apply {
                        put("role", message["role"])
                        put("content", message["content"])
                    })
                }
            })
            put("temperature", 0.7)
            put("max_tokens", 500)
        }

        // Create request
        val requestBody = requestJson.toString()
            .toRequestBody("application/json".toMediaTypeOrNull())

        val request = Request.Builder()
            .url(baseUrl)
            .addHeader("Authorization", "Bearer $apiKey")
            .addHeader("Content-Type", "application/json")
            .post(requestBody)
            .build()

        return withContext(Dispatchers.IO) {
            try {
                client.newCall(request).execute().use { response ->
                    if (!response.isSuccessful) {
                        Log.e("OpenAIClient", "API call failed: ${response.code} ${response.message}")
                        return@withContext "Sorry, I encountered an error. Please try again later."
                    }

                    val responseBody = response.body?.string() ?: ""
                    val jsonResponse = JSONObject(responseBody)
                    val choices = jsonResponse.getJSONArray("choices")

                    if (choices.length() > 0) {
                        val assistantMessage = choices.getJSONObject(0)
                            .getJSONObject("message")
                            .getString("content")

                        // Add assistant response to history
                        conversationHistory.add(mapOf("role" to "assistant", "content" to assistantMessage))

                        return@withContext assistantMessage
                    } else {
                        return@withContext "Sorry, I couldn't generate a response. Please try again."
                    }
                }
            } catch (e: Exception) {
                Log.e("OpenAIClient", "Exception during API call", e)
                return@withContext "Sorry, I encountered an error: ${e.message}"
            }
        }
    }

    suspend fun analyzeImage(base64Image: String, prompt: String): String {
        val visionUrl = "https://api.openai.com/v1/chat/completions"

        // Prepare request JSON for vision API
        val requestJson = JSONObject().apply {
            put("model", "gpt-4-vision-preview")
            put("messages", JSONArray().apply {
                put(JSONObject().apply {
                    put("role", "user")
                    put("content", JSONArray().apply {
                        // Text prompt
                        put(JSONObject().apply {
                            put("type", "text")
                            put("text", "Please analyze and summarize this university document or image: $prompt")
                        })
                        // Image content
                        put(JSONObject().apply {
                            put("type", "image_url")
                            put("image_url", JSONObject().apply {
                                put("url", "data:image/jpeg;base64,$base64Image")
                            })
                        })
                    })
                })
            })
            put("max_tokens", 500)
        }

        // Create request
        val requestBody = requestJson.toString()
            .toRequestBody("application/json".toMediaTypeOrNull())

        val request = Request.Builder()
            .url(visionUrl)
            .addHeader("Authorization", "Bearer $apiKey")
            .addHeader("Content-Type", "application/json")
            .post(requestBody)
            .build()

        return withContext(Dispatchers.IO) {
            try {
                client.newCall(request).execute().use { response ->
                    if (!response.isSuccessful) {
                        Log.e("OpenAIClient", "Vision API call failed: ${response.code} ${response.message}")
                        return@withContext "Sorry, I couldn't analyze the image. Please try again later."
                    }

                    val responseBody = response.body?.string() ?: ""
                    val jsonResponse = JSONObject(responseBody)
                    val choices = jsonResponse.getJSONArray("choices")

                    if (choices.length() > 0) {
                        val analysisResult = choices.getJSONObject(0)
                            .getJSONObject("message")
                            .getString("content")

                        // Add this interaction to conversation history
                        conversationHistory.add(mapOf("role" to "user", "content" to "I shared an image with prompt: $prompt"))
                        conversationHistory.add(mapOf("role" to "assistant", "content" to analysisResult))

                        return@withContext analysisResult
                    } else {
                        return@withContext "Sorry, I couldn't analyze the image. Please try again."
                    }
                }
            } catch (e: Exception) {
                Log.e("OpenAIClient", "Exception during Vision API call", e)
                return@withContext "Sorry, I encountered an error analyzing the image: ${e.message}"
            }
        }
    }

    fun clearConversation() {
        // Keep only the system message
        val systemMessage = conversationHistory.first()
        conversationHistory.clear()
        conversationHistory.add(systemMessage)
    }
}

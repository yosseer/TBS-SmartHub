package com.example.tbssmarthub.ui.chatbot

import android.Manifest
import android.content.pm.PackageManager
import android.net.Uri
import android.speech.tts.TextToSpeech
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.navigation.compose.rememberNavController
import coil.compose.rememberAsyncImagePainter
import com.example.tbssmarthub.MyAppTheme
import com.example.tbssmarthub.R
import com.example.tbssmarthub.ui.HomeScreen
import com.example.tbssmarthub.ui.auth.LoginScreen
import kotlinx.coroutines.launch
import java.io.File
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

// Data class for chat messages
data class ChatMessage(
    val id: String = UUID.randomUUID().toString(),
    val content: String,
    val isFromUser: Boolean,
    val timestamp: Long = System.currentTimeMillis(),
    val imageUri: Uri? = null,
    val isProcessingVoice: Boolean = false
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatScreen() {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    // State for messages
    val messages = remember { mutableStateListOf<ChatMessage>() }

    // State for text input
    var textInput by remember { mutableStateOf("") }

    // State for selected image
    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }

    // State for recording voice
    var isRecording by remember { mutableStateOf(false) }

    // State for text-to-speech
    var tts by remember { mutableStateOf<TextToSpeech?>(null) }

    // Initialize TTS
    DisposableEffect(Unit) {
        val textToSpeech = TextToSpeech(context) { status ->
            if (status == TextToSpeech.SUCCESS) {
                //tts = textToSpeech
            }
        }

        onDispose {
            textToSpeech.stop()
            textToSpeech.shutdown()
        }
    }

    // Scroll state for LazyColumn
    val scrollState = rememberLazyListState()

    // Auto-scroll to bottom when new messages arrive
    LaunchedEffect(messages.size) {
        if (messages.isNotEmpty()) {
            scrollState.animateScrollToItem(messages.size - 1)
        }
    }

    // Permission launcher for camera
    val cameraPermissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            // Handle camera access
        }
    }

    // Permission launcher for microphone
    val microphonePermissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            isRecording = true
            // In a real app, start recording here
        }
    }

    // Image picker launcher
    val imagePickerLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri ->
        uri?.let {
            selectedImageUri = it
        }
    }

    // Function to send a message
    fun sendMessage(content: String, imageUri: Uri? = null) {
        if (content.isNotBlank() || imageUri != null) {
            val userMessage = ChatMessage(
                content = content,
                isFromUser = true,
                imageUri = imageUri
            )
            messages.add(userMessage)

            // Clear input and selected image
            textInput = ""
            selectedImageUri = null

            // Simulate AI response (in a real app, this would call the OpenAI API)
            coroutineScope.launch {
                // Add a placeholder for the AI's response
                val aiMessageId = UUID.randomUUID().toString()
                messages.add(ChatMessage(
                    id = aiMessageId,
                    content = "Thinking...",
                    isFromUser = false,
                    isProcessingVoice = false
                ))

                // Simulate API delay
                kotlinx.coroutines.delay(1000)

                // Replace placeholder with actual response
                val responseIndex = messages.indexOfFirst { it.id == aiMessageId }
                if (responseIndex != -1) {
                    val response = if (imageUri != null) {
                        "I've analyzed the image you sent. This appears to be a document related to university coursework. Would you like me to summarize its contents?"
                    } else if (content.contains("SWOT", ignoreCase = true)) {
                        "A SWOT analysis is a framework used to evaluate a company's competitive position. It stands for Strength, Weaknesses, Opportunities, and Threats."
                    } else {
                        "I'm your TBS university assistant. How can I help you with your academic questions today?"
                    }

                    messages[responseIndex] = ChatMessage(
                        id = aiMessageId,
                        content = response,
                        isFromUser = false
                    )
                }
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF5F5F5))
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            // Top App Bar
            TopAppBar(
                title = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.logo_tbs),
                            contentDescription = "TBS Logo",
                            modifier = Modifier
                                .size(24.dp)
                                .padding(end = 8.dp)
                        )
                        Column {
                            Text(
                                text = "TBS ChatBot",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "Active now",
                                fontSize = 12.sp,
                                color = Color.Gray
                            )
                        }
                    }
                },
                navigationIcon = {
                    IconButton(onClick = { /* Handle back navigation */ }) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { /* Handle menu */ }) {
                        Icon(
                            imageVector = Icons.Default.Menu,
                            contentDescription = "Menu"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.White
                )
            )

            // Chat messages
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                state = scrollState,
                contentPadding = PaddingValues(vertical = 8.dp)
            ) {
                items(messages) { message ->
                    ChatMessageItem(
                        message = message,
                        onPlayVoice = { content ->
                            tts?.speak(content, TextToSpeech.QUEUE_FLUSH, null, "MessageId_${message.id}")
                        }
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }

            // Selected image preview
            selectedImageUri?.let { uri ->
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(120.dp)
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                        .background(Color.White, RoundedCornerShape(8.dp))
                ) {
                    Image(
                        painter = rememberAsyncImagePainter(uri),
                        contentDescription = "Selected Image",
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(4.dp),
                        contentScale = ContentScale.Fit
                    )

                    IconButton(
                        onClick = { selectedImageUri = null },
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .size(24.dp)
                            .background(Color.White, CircleShape)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Remove Image",
                            tint = Color.Black,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }

            // Input area
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shadowElevation = 8.dp,
                color = Color.White
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Image attachment button
                    IconButton(
                        onClick = {
                            imagePickerLauncher.launch("image/*")
                        }
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.baseline_image_search_24),
                            contentDescription = "Attach Image",
                            tint = Color.Gray
                        )
                    }

                    // Text input field
                    TextField(
                        value = textInput,
                        onValueChange = { textInput = it },
                        placeholder = { Text("Message...") },
                        modifier = Modifier
                            .weight(1f)
                            .padding(horizontal = 8.dp),
                        colors = TextFieldDefaults.textFieldColors(
                            containerColor = Color.White,
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent
                        ),
                        singleLine = false,
                        maxLines = 3
                    )

                    // Voice input button
                    IconButton(
                        onClick = {
                            when {
                                ContextCompat.checkSelfPermission(
                                    context,
                                    Manifest.permission.RECORD_AUDIO
                                ) == PackageManager.PERMISSION_GRANTED -> {
                                    isRecording = true
                                    // In a real app, start recording here
                                }
                                else -> {
                                    microphonePermissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
                                }
                            }
                        }
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.baseline_mic_24),
                            contentDescription = "Voice Input",
                            tint = if (isRecording) Color.Red else Color.Gray
                        )
                    }

                    // Send button
                    IconButton(
                        onClick = {
                            sendMessage(textInput, selectedImageUri)
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Send,
                            contentDescription = "Send Message",
                            tint = Color(0xFF1877F2) // Facebook blue
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ChatMessageItem(
    message: ChatMessage,
    onPlayVoice: (String) -> Unit
) {
    val isFromUser = message.isFromUser
    val backgroundColor = if (isFromUser) Color.Black else Color.LightGray
    val textColor = if (isFromUser) Color.White else Color.Black
    val alignment = if (isFromUser) Alignment.End else Alignment.Start

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = alignment
    ) {
        // Display timestamp
        val dateFormat = SimpleDateFormat("h:mm a", Locale.getDefault())
        val formattedTime = dateFormat.format(Date(message.timestamp))

        if (!isFromUser) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(bottom = 4.dp)
            ) {
                // Bot avatar
                Image(
                    painter = painterResource(id = R.drawable.logo_tbs),
                    contentDescription = "Bot Avatar",
                    modifier = Modifier
                        .size(24.dp)
                        .clip(CircleShape)
                        .border(1.dp, Color.LightGray, CircleShape)
                )

                Spacer(modifier = Modifier.width(8.dp))

                Text(
                    text = "TBS ChatBot • $formattedTime",
                    fontSize = 12.sp,
                    color = Color.Gray
                )
            }
        }

        Row(
            verticalAlignment = Alignment.Bottom,
            horizontalArrangement = if (isFromUser) Arrangement.End else Arrangement.Start,
            modifier = Modifier.fillMaxWidth()
        ) {
            if (isFromUser) {
                // Time for user messages
                Text(
                    text = formattedTime,
                    fontSize = 10.sp,
                    color = Color.Gray,
                    modifier = Modifier.padding(end = 8.dp, bottom = 8.dp)
                )
            }

            Column(
                horizontalAlignment = alignment,
                modifier = Modifier.widthIn(max = 280.dp)
            ) {
                // Image if present
                message.imageUri?.let { uri ->
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = backgroundColor,
                        modifier = Modifier
                            .padding(vertical = 2.dp)
                            .clip(RoundedCornerShape(12.dp))
                    ) {
                        Image(
                            painter = rememberAsyncImagePainter(uri),
                            contentDescription = "Attached Image",
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(180.dp)
                                .padding(4.dp),
                            contentScale = ContentScale.Fit
                        )
                    }
                }

                // Message content
                if (message.content.isNotBlank()) {
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = backgroundColor,
                        modifier = Modifier.padding(vertical = 2.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
                        ) {
                            Text(
                                text = message.content,
                                color = textColor,
                                modifier = Modifier.weight(1f)
                            )

                            if (!isFromUser) {
                                Spacer(modifier = Modifier.width(8.dp))

                                // Play voice button for bot messages
                                IconButton(
                                    onClick = { onPlayVoice(message.content) },
                                    modifier = Modifier.size(24.dp)
                                ) {
                                    Icon(
                                        painter = painterResource (id= R.drawable.baseline_volume_up_24),
                                        contentDescription = "Play Voice",
                                        tint = Color.DarkGray,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }

            if (!isFromUser) {
                // Time for bot messages
                Text(
                    text = formattedTime,
                    fontSize = 10.sp,
                    color = Color.Gray,
                    modifier = Modifier.padding(start = 8.dp, bottom = 8.dp)
                )
            }
        }
    }
}


@Preview(showBackground = true)
@Composable
fun ChatScreenPreview() {
    MyAppTheme {
        ChatScreen()
    }
}



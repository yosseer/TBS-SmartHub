package com.example.tbssmarthub.ui.feedback

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.tbssmarthub.data.model.Feedback
import com.example.tbssmarthub.data.repository.UserRepository
import com.example.tbssmarthub.ui.components.DrawerScaffold
import com.example.tbssmarthub.ui.theme.TbsPrimaryGold
import kotlinx.coroutines.launch
import java.util.*

/**
 * Anonymous Feedback Screen
 * Allows users to submit feedback anonymously to the university
 * 
 * @param navController Navigation controller for screen navigation
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AnonymousFeedbackScreen(navController: NavController) {
    // Get user repository instance
    val userRepository = UserRepository.getInstance()
    
    // State for feedback message
    var feedbackMessage by remember { mutableStateOf("") }
    
    // State for showing snackbar
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    
    // Drawer scaffold for consistent UI
    DrawerScaffold(
        navController = navController,
        title = "Anonymous Feedback",
        snackbarHostState = snackbarHostState,
        content = { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Feedback header
                Text(
                    text = "Submit Anonymous Feedback",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold
                )
                
                // Feedback description
                Text(
                    text = "Your feedback helps us improve the university experience. All submissions are completely anonymous.",
                    fontSize = 16.sp
                )
                
                // Feedback card
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        // Feedback input field
                        OutlinedTextField(
                            value = feedbackMessage,
                            onValueChange = { feedbackMessage = it },
                            label = { Text("Your Feedback") },
                            placeholder = { Text("Share your thoughts, suggestions, or concerns...") },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(200.dp),
                            maxLines = 10
                        )
                        
                        // Character count
                        Text(
                            text = "${feedbackMessage.length}/500 characters",
                            modifier = Modifier.align(Alignment.End),
                            style = MaterialTheme.typography.bodySmall,
                            color = if (feedbackMessage.length > 500) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        
                        // Submit button
                        Button(
                            onClick = {
                                if (feedbackMessage.isNotBlank() && feedbackMessage.length <= 500) {
                                    // Create feedback object
                                    val feedback = Feedback(
                                        feedbackId = UUID.randomUUID().toString(),
                                        userId = "anonymous", // Keep it anonymous
                                        message = feedbackMessage,
                                        timestamp = System.currentTimeMillis()
                                    )
                                    
                                    // In a real app, this would be sent to a repository
                                    // For now, just show success message
                                    
                                    // Reset field
                                    feedbackMessage = ""
                                    
                                    // Show success message
                                    scope.launch {
                                        snackbarHostState.showSnackbar("Thank you for your feedback!")
                                    }
                                }
                            },
                            modifier = Modifier.align(Alignment.End),
                            enabled = feedbackMessage.isNotBlank() && feedbackMessage.length <= 500
                        ) {
                            Icon(
                                imageVector = Icons.Default.Send,
                                contentDescription = "Submit Feedback"
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Submit Feedback")
                        }
                    }
                }
                
                // Privacy notice
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant
                    )
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Security,
                            contentDescription = "Privacy",
                            tint = TbsPrimaryGold
                        )
                        
                        Spacer(modifier = Modifier.width(16.dp))
                        
                        Text(
                            text = "Your privacy is important to us. All feedback is submitted anonymously and cannot be traced back to individual users.",
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
                
                // Feedback categories
                Text(
                    text = "Common Feedback Categories",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(top = 16.dp)
                )
                
                // Category chips
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    SuggestionChip(
                        onClick = {
                            feedbackMessage = "I'd like to suggest improvements to the course materials..."
                        },
                        label = { Text("Courses") }
                    )
                    
                    SuggestionChip(
                        onClick = {
                            feedbackMessage = "The facilities could be improved by..."
                        },
                        label = { Text("Facilities") }
                    )
                    
                    SuggestionChip(
                        onClick = {
                            feedbackMessage = "I have feedback about a professor..."
                        },
                        label = { Text("Faculty") }
                    )
                }
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    SuggestionChip(
                        onClick = {
                            feedbackMessage = "The app could be improved by..."
                        },
                        label = { Text("App") }
                    )
                    
                    SuggestionChip(
                        onClick = {
                            feedbackMessage = "I'd like to suggest a new feature..."
                        },
                        label = { Text("Feature Request") }
                    )
                    
                    SuggestionChip(
                        onClick = {
                            feedbackMessage = "I encountered a problem with..."
                        },
                        label = { Text("Issue") }
                    )
                }
                
                // Bottom spacing
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    )
}

package com.example.tbssmarthub.ui.auth

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.tbssmarthub.R
import com.example.tbssmarthub.data.model.UserRole
import com.example.tbssmarthub.data.repository.FirebaseAuthRepository
import com.example.tbssmarthub.navigation.NavRoutes
import com.example.tbssmarthub.ui.theme.TbsPrimaryGold
import kotlinx.coroutines.launch

/**
 * Login screen for user authentication
 * Integrates with Firebase for real user authentication
 * 
 * @param navController Navigation controller for screen navigation
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(navController: NavController) {
    // Firebase auth repository
    val firebaseAuthRepository = remember { FirebaseAuthRepository() }
    
    // State for form fields
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    
    // State for loading and error
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    
    // Coroutine scope for async operations
    val scope = rememberCoroutineScope()
    
    // Background image
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF5F5F5))
    ) {
        // Background image
        Image(
            painter = painterResource(id = R.drawable.background),
            contentDescription = "Background",
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )
        
        // Content
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Logo
            Image(
                painter = painterResource(id = R.drawable.logo_tbs),
                contentDescription = "TBS Logo",
                modifier = Modifier
                    .size(120.dp)
                    .padding(bottom = 24.dp)
            )
            
            // Login card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Title
                    Text(
                        text = "Welcome Back !",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(bottom = 24.dp)
                    )
                    
                    // Email field
                    OutlinedTextField(
                        value = email,
                        onValueChange = { email = it },
                        label = { Text("Email or ID") },
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 16.dp),
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.Email,
                                contentDescription = "Email"
                            )
                        },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
                    )
                    
                    // Password field
                    OutlinedTextField(
                        value = password,
                        onValueChange = { password = it },
                        label = { Text("Password") },
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 24.dp),
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.Lock,
                                contentDescription = "Password"
                            )
                        },
                        trailingIcon = {
                            IconButton(onClick = { passwordVisible = !passwordVisible }) {
                                Icon(
                                    imageVector = if (passwordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                    contentDescription = if (passwordVisible) "Hide Password" else "Show Password"
                                )
                            }
                        },
                        visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password)
                    )
                    
                    // Error message
                    if (errorMessage != null) {
                        Text(
                            text = errorMessage!!,
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodySmall,
                            modifier = Modifier.padding(bottom = 16.dp)
                        )
                    }
                    
                    // Login button
                    Button(
                        onClick = {
                            // Validate inputs
                            if (email.isBlank() || password.isBlank()) {
                                errorMessage = "Please fill in all fields"
                                return@Button
                            }
                            
                            // Show loading
                            isLoading = true
                            errorMessage = null
                            
                            // Handle login with Firebase
                            scope.launch {
                                try {
                                    val result = firebaseAuthRepository.signIn(email, password)
                                    
                                    result.fold(
                                        onSuccess = { user ->
                                            // Navigate based on user role
                                            when (user.role) {
                                                UserRole.ADMIN -> navController.navigate(NavRoutes.ADMIN_DASHBOARD) {
                                                    popUpTo(NavRoutes.LOGIN) { inclusive = true }
                                                }
                                                else -> navController.navigate(NavRoutes.HOME) {
                                                    popUpTo(NavRoutes.LOGIN) { inclusive = true }
                                                }
                                            }
                                        },
                                        onFailure = { exception ->
                                            errorMessage = exception.message ?: "Authentication failed"
                                            isLoading = false
                                        }
                                    )
                                } catch (e: Exception) {
                                    errorMessage = e.message ?: "Authentication failed"
                                    isLoading = false
                                }
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp),
                        enabled = !isLoading
                    ) {
                        if (isLoading) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(24.dp),
                                color = Color.White
                            )
                        } else {
                            Text("LOGIN")
                        }
                    }
                    
                    // Sign up link
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 16.dp),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Don't have an account?")
                        TextButton(onClick = { navController.navigate(NavRoutes.SIGNUP) }) {
                            Text("Sign Up")
                        }
                    }
                    
                    // Or divider
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Divider(
                            modifier = Modifier
                                .weight(1f)
                                .padding(end = 8.dp)
                        )
                        Text("or")
                        Divider(
                            modifier = Modifier
                                .weight(1f)
                                .padding(start = 8.dp)
                        )
                    }
                    
                    // Google sign in button
                    OutlinedButton(
                        onClick = { /* Handle Google sign in */ },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = Color.Black
                        )
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.google_),
                                contentDescription = "Google",
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Continue with Google")
                        }
                    }
                }
            }
        }
    }
}

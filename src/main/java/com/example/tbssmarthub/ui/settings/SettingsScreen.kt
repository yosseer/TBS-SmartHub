package com.example.tbssmarthub.ui.settings

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
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.tbssmarthub.data.repository.UserRepository
import com.example.tbssmarthub.ui.components.DrawerScaffold
import kotlinx.coroutines.launch

/**
 * Settings screen that allows users to modify their profile information
 * Includes options to change name and password
 * 
 * @param navController Navigation controller for screen navigation
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(navController: NavController) {
    // Get user repository instance
    val userRepository = UserRepository.getInstance()
    
    // Get current user
    val currentUser = userRepository.currentUser.collectAsState().value
    
    // State for form fields
    var name by remember { mutableStateOf(currentUser?.name ?: "") }
    var currentPassword by remember { mutableStateOf("") }
    var newPassword by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    
    // State for password visibility
    var currentPasswordVisible by remember { mutableStateOf(false) }
    var newPasswordVisible by remember { mutableStateOf(false) }
    var confirmPasswordVisible by remember { mutableStateOf(false) }
    
    // State for showing snackbar
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    
    // Drawer scaffold for consistent UI
    DrawerScaffold(
        navController = navController,
        title = "Settings",
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
                // Settings header
                Text(
                    text = "Profile Settings",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold
                )
                
                // Profile section
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
                        // Section title
                        Text(
                            text = "Personal Information",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )
                        
                        // Name field
                        OutlinedTextField(
                            value = name,
                            onValueChange = { name = it },
                            label = { Text("Name") },
                            modifier = Modifier.fillMaxWidth(),
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Default.Person,
                                    contentDescription = "Name"
                                )
                            }
                        )
                        
                        // Email field (read-only)
                        OutlinedTextField(
                            value = currentUser?.email ?: "",
                            onValueChange = { },
                            label = { Text("Email") },
                            modifier = Modifier.fillMaxWidth(),
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Default.Email,
                                    contentDescription = "Email"
                                )
                            },
                            readOnly = true,
                            enabled = false
                        )
                        
                        // User ID field (read-only)
                        OutlinedTextField(
                            value = currentUser?.userId ?: "",
                            onValueChange = { },
                            label = { Text("User ID") },
                            modifier = Modifier.fillMaxWidth(),
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Default.Badge,
                                    contentDescription = "User ID"
                                )
                            },
                            readOnly = true,
                            enabled = false
                        )
                        
                        // Save button
                        Button(
                            onClick = {
                                if (currentUser != null && name.isNotBlank()) {
                                    userRepository.updateUserProfile(
                                        userId = currentUser.userId,
                                        name = name
                                    )
                                    scope.launch {
                                        snackbarHostState.showSnackbar("Name updated successfully")
                                    }
                                }
                            },
                            modifier = Modifier.align(Alignment.End),
                            enabled = name.isNotBlank() && name != currentUser?.name
                        ) {
                            Icon(
                                imageVector = Icons.Default.Save,
                                contentDescription = "Save"
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Save Changes")
                        }
                    }
                }
                
                // Password section
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
                        // Section title
                        Text(
                            text = "Change Password",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )
                        
                        // Current password field
                        OutlinedTextField(
                            value = currentPassword,
                            onValueChange = { currentPassword = it },
                            label = { Text("Current Password") },
                            modifier = Modifier.fillMaxWidth(),
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Default.Lock,
                                    contentDescription = "Current Password"
                                )
                            },
                            trailingIcon = {
                                IconButton(onClick = { currentPasswordVisible = !currentPasswordVisible }) {
                                    Icon(
                                        imageVector = if (currentPasswordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                        contentDescription = if (currentPasswordVisible) "Hide Password" else "Show Password"
                                    )
                                }
                            },
                            visualTransformation = if (currentPasswordVisible) VisualTransformation.None else PasswordVisualTransformation()
                        )
                        
                        // New password field
                        OutlinedTextField(
                            value = newPassword,
                            onValueChange = { newPassword = it },
                            label = { Text("New Password") },
                            modifier = Modifier.fillMaxWidth(),
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Default.Lock,
                                    contentDescription = "New Password"
                                )
                            },
                            trailingIcon = {
                                IconButton(onClick = { newPasswordVisible = !newPasswordVisible }) {
                                    Icon(
                                        imageVector = if (newPasswordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                        contentDescription = if (newPasswordVisible) "Hide Password" else "Show Password"
                                    )
                                }
                            },
                            visualTransformation = if (newPasswordVisible) VisualTransformation.None else PasswordVisualTransformation()
                        )
                        
                        // Confirm password field
                        OutlinedTextField(
                            value = confirmPassword,
                            onValueChange = { confirmPassword = it },
                            label = { Text("Confirm New Password") },
                            modifier = Modifier.fillMaxWidth(),
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Default.Lock,
                                    contentDescription = "Confirm New Password"
                                )
                            },
                            trailingIcon = {
                                IconButton(onClick = { confirmPasswordVisible = !confirmPasswordVisible }) {
                                    Icon(
                                        imageVector = if (confirmPasswordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                        contentDescription = if (confirmPasswordVisible) "Hide Password" else "Show Password"
                                    )
                                }
                            },
                            visualTransformation = if (confirmPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                            isError = newPassword != confirmPassword && confirmPassword.isNotEmpty()
                        )
                        
                        // Error message for password mismatch
                        if (newPassword != confirmPassword && confirmPassword.isNotEmpty()) {
                            Text(
                                text = "Passwords do not match",
                                color = MaterialTheme.colorScheme.error,
                                style = MaterialTheme.typography.bodySmall,
                                modifier = Modifier.padding(start = 16.dp)
                            )
                        }
                        
                        // Change password button
                        Button(
                            onClick = {
                                if (currentUser != null && 
                                    currentPassword.isNotBlank() && 
                                    newPassword.isNotBlank() && 
                                    newPassword == confirmPassword) {
                                    
                                    // Verify current password
                                    if (currentPassword == currentUser.passwordHash) {
                                        userRepository.updateUserProfile(
                                            userId = currentUser.userId,
                                            password = newPassword
                                        )
                                        
                                        // Reset fields
                                        currentPassword = ""
                                        newPassword = ""
                                        confirmPassword = ""
                                        
                                        scope.launch {
                                            snackbarHostState.showSnackbar("Password updated successfully")
                                        }
                                    } else {
                                        scope.launch {
                                            snackbarHostState.showSnackbar("Current password is incorrect")
                                        }
                                    }
                                }
                            },
                            modifier = Modifier.align(Alignment.End),
                            enabled = currentPassword.isNotBlank() && 
                                     newPassword.isNotBlank() && 
                                     newPassword == confirmPassword
                        ) {
                            Icon(
                                imageVector = Icons.Default.VpnKey,
                                contentDescription = "Change Password"
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Change Password")
                        }
                    }
                }
                
                // App preferences section
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
                        // Section title
                        Text(
                            text = "App Preferences",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )
                        
                        // Notification switch
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Notifications,
                                    contentDescription = "Notifications",
                                    modifier = Modifier.size(24.dp)
                                )
                                Spacer(modifier = Modifier.width(16.dp))
                                Text("Enable Notifications")
                            }
                            
                            Switch(
                                checked = true,
                                onCheckedChange = { }
                            )
                        }
                        
                        // Dark mode switch
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.DarkMode,
                                    contentDescription = "Dark Mode",
                                    modifier = Modifier.size(24.dp)
                                )
                                Spacer(modifier = Modifier.width(16.dp))
                                Text("Dark Mode")
                            }
                            
                            Switch(
                                checked = false,
                                onCheckedChange = { }
                            )
                        }
                    }
                }
                
                // Bottom spacing
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    )
}

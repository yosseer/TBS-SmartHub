package com.example.tbssmarthub.ui.auth

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.tbssmarthub.MyAppTheme
import com.example.tbssmarthub.R
import com.example.tbssmarthub.ui.components.AuthForm
import com.example.tbssmarthub.ui.components.AuthField
import com.example.tbssmarthub.utils.validatePassword
import com.example.tbssmarthub.utils.validateUsername

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SignUpScreen(navController: NavController) {
    var firstName by remember { mutableStateOf("") }
    var lastName by remember { mutableStateOf("") }
    var id by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    val context = LocalContext.current

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF5F5F5))
    ) {
        Image(
            painter = painterResource(id = R.drawable.background),
            contentDescription = "Background",
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )

        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Image(
                painter = painterResource(id = R.drawable.logo_tbs),
                contentDescription = "App Logo",
                modifier = Modifier
                    .size(120.dp)
                    .padding(bottom = 32.dp)
            )

            AuthForm(
                title = "Create Account",
                fields = listOf(
                    AuthField(
                        value = firstName,
                        onValueChange = { firstName = it },
                        label = "First Name",
                        icon = Icons.Default.Person,
                        isPassword = false
                    ),
                    AuthField(
                        value = lastName,
                        onValueChange = { lastName = it },
                        label = "Last Name",
                        icon = Icons.Default.Person,
                        isPassword = false
                    ),
                    AuthField(
                        value = id,
                        onValueChange = { id = it },
                        label = "ID",
                        icon = Icons.Default.Person,
                        isPassword = false,
                        validator = ::validateUsername,
                        errorMessage = "Must start with 0/1 and be 8 digits"
                    ),
                    AuthField(
                        value = email,
                        onValueChange = { email = it },
                        label = "Email",
                        icon = Icons.Default.Email,
                        isPassword = false
                    ),
                    AuthField(
                        value = password,
                        onValueChange = { password = it },
                        label = "Password",
                        icon = Icons.Default.Lock,
                        isPassword = true,
                        validator = ::validatePassword,
                        errorMessage = "Requires: 1 uppercase, 1 lowercase, 1 digit, 1 special char"
                    ),
                    AuthField(
                        value = confirmPassword,
                        onValueChange = { confirmPassword = it },
                        label = "Confirm Password",
                        icon = Icons.Default.Lock,
                        isPassword = true
                    )
                ),
                onSubmit = {
                    if (password == confirmPassword) {
                        Toast.makeText(context, "Account created!", Toast.LENGTH_SHORT).show()
                        navController.popBackStack()
                    } else {
                        Toast.makeText(context, "Passwords don't match!", Toast.LENGTH_SHORT).show()
                    }
                },
                submitText = "S I G N  U P",
                navController = navController,
                alternateActionText = "Already have an account? Login",
                alternateDestination = "login"
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun LoginScreenPreview() {
    MyAppTheme {
        LoginScreen(rememberNavController())
    }
}

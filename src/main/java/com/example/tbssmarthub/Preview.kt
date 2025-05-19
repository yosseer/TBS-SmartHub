package com.example.tbssmarthub


import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.rememberNavController
import com.example.tbssmarthub.ui.auth.LoginScreen
import com.example.tbssmarthub.ui.auth.SignUpScreen
import androidx.compose.runtime.Composable
import com.example.tbssmarthub.ui.HomeScreen

@Preview(showBackground = true)
@Composable
fun LoginScreenPreview() {
    MyAppTheme {
        LoginScreen(rememberNavController())
    }
}

@Preview(showBackground = true)
@Composable
fun SignUpScreenPreview() {
    MyAppTheme {
        SignUpScreen(rememberNavController())
    }
}

@Preview(showBackground = true)
@Composable
fun HomeScreenPreview() {
    MyAppTheme {
        HomeScreen()
    }
}



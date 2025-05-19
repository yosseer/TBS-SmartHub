package com.example.tbssmarthub.utils

fun validateUsername(input: String): Boolean {
    val emailPattern = Regex("^[A-Za-z0-9+_.-]+@(.+)\$")
    val idPattern = Regex("^[01]\\d{7}\$")
    return input.matches(emailPattern) || input.matches(idPattern)
}

fun validatePassword(input: String): Boolean {
    val passwordPattern = Regex("^(?=.*[A-Z])(?=.*[a-z])(?=.*\\d)(?=.*[@\$!%*?&])[A-Za-z\\d@\$!%*?&]{8,}\$")
    return input.matches(passwordPattern)
}
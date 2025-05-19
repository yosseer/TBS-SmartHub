package com.example.tbssmarthub.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class AuthViewModel : ViewModel() {
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()

    private val _authState = MutableStateFlow<AuthState>(AuthState.Idle)
    val authState: StateFlow<AuthState> = _authState

    private val _currentUser = MutableStateFlow<FirebaseUser?>(auth.currentUser)
    val currentUser: StateFlow<FirebaseUser?> = _currentUser

    init {
        // Check if user is already signed in
        _currentUser.value = auth.currentUser
    }

    fun signUp(firstName: String, lastName: String, studentId: String, email: String, password: String) {
        viewModelScope.launch {
            try {
                _authState.value = AuthState.Loading

                // Create user with email and password
                val authResult = auth.createUserWithEmailAndPassword(email, password).await()

                // Update user profile with display name (first and last name)
                val profileUpdates = UserProfileChangeRequest.Builder()
                    .setDisplayName("$firstName $lastName")
                    .build()

                authResult.user?.updateProfile(profileUpdates)?.await()

                // Store additional user data in Firestore
                val user = hashMapOf(
                    "firstName" to firstName,
                    "lastName" to lastName,
                    "studentId" to studentId,
                    "email" to email,
                    "createdAt" to System.currentTimeMillis()
                )

                authResult.user?.uid?.let { uid ->
                    firestore.collection("users").document(uid).set(user).await()
                }

                // Update current user
                _currentUser.value = authResult.user
                _authState.value = AuthState.Success

            } catch (e: Exception) {
                Log.e("AuthViewModel", "Sign up failed", e)
                _authState.value = AuthState.Error(e.message ?: "Sign up failed")
            }
        }
    }

    fun signIn(emailOrId: String, password: String) {
        viewModelScope.launch {
            try {
                _authState.value = AuthState.Loading

                // Check if input is email or student ID
                if (emailOrId.contains("@")) {
                    // Sign in with email and password
                    val authResult = auth.signInWithEmailAndPassword(emailOrId, password).await()
                    _currentUser.value = authResult.user
                    _authState.value = AuthState.Success
                } else {
                    // Sign in with student ID
                    // First, query Firestore to find the user with this student ID
                    val querySnapshot = firestore.collection("users")
                        .whereEqualTo("studentId", emailOrId)
                        .get()
                        .await()

                    if (querySnapshot.documents.isNotEmpty()) {
                        // Get the email associated with this student ID
                        val userDoc = querySnapshot.documents[0]
                        val email = userDoc.getString("email")

                        if (email != null) {
                            // Sign in with the found email and password
                            val authResult = auth.signInWithEmailAndPassword(email, password).await()
                            _currentUser.value = authResult.user
                            _authState.value = AuthState.Success
                        } else {
                            _authState.value = AuthState.Error("Invalid student ID or password")
                        }
                    } else {
                        _authState.value = AuthState.Error("Invalid student ID or password")
                    }
                }

            } catch (e: Exception) {
                Log.e("AuthViewModel", "Sign in failed", e)
                _authState.value = AuthState.Error(e.message ?: "Sign in failed")
            }
        }
    }

    fun signOut() {
        auth.signOut()
        _currentUser.value = null
        _authState.value = AuthState.Idle
    }

    fun resetAuthState() {
        _authState.value = AuthState.Idle
    }
}

sealed class AuthState {
    object Idle : AuthState()
    object Loading : AuthState()
    object Success : AuthState()
    data class Error(val message: String) : AuthState()
}

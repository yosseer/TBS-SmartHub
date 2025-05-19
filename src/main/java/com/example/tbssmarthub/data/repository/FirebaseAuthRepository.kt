package com.example.tbssmarthub.data.repository

import android.util.Log
import com.example.tbssmarthub.data.model.Student
import com.example.tbssmarthub.data.model.User
import com.example.tbssmarthub.data.model.UserRole
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Repository for handling Firebase authentication and user data
 */
class FirebaseAuthRepository {
    private val auth = FirebaseAuth.getInstance()
    private val firestore = FirebaseFirestore.getInstance()
    
    // Current authenticated user
    private val _currentUser = MutableStateFlow<User?>(null)
    val currentUser: StateFlow<User?> = _currentUser.asStateFlow()
    
    // Current student data (if user is a student)
    private val _currentStudent = MutableStateFlow<Student?>(null)
    val currentStudent: StateFlow<Student?> = _currentStudent.asStateFlow()
    
    init {
        // Check if user is already signed in
        auth.currentUser?.let { firebaseUser ->
            fetchUserData(firebaseUser)
        }
        
        // Listen for auth state changes
        auth.addAuthStateListener { firebaseAuth ->
            val user = firebaseAuth.currentUser
            if (user == null) {
                _currentUser.value = null
                _currentStudent.value = null
            } else {
                fetchUserData(user)
            }
        }
    }
    
    /**
     * Sign in with email and password
     */
    suspend fun signIn(email: String, password: String): Result<User> = withContext(Dispatchers.IO) {
        try {
            val authResult = auth.signInWithEmailAndPassword(email, password).await()
            val firebaseUser = authResult.user
            
            if (firebaseUser != null) {
                val user = fetchUserData(firebaseUser)
                Result.success(user)
            } else {
                Result.failure(Exception("Authentication failed"))
            }
        } catch (e: Exception) {
            Log.e("FirebaseAuthRepo", "Sign in failed", e)
            Result.failure(e)
        }
    }
    
    /**
     * Sign out the current user
     */
    fun signOut() {
        auth.signOut()
        _currentUser.value = null
        _currentStudent.value = null
    }
    
    /**
     * Fetch user data from Firestore
     */
    private suspend fun fetchUserData(firebaseUser: FirebaseUser): User = withContext(Dispatchers.IO) {
        try {
            // Check if user is admin (you can store admin emails in a separate collection)
            val isAdmin = firebaseUser.email == "admin@tbsuniversity.edu"
            
            if (isAdmin) {
                // Create admin user
                val adminUser = User(
                    userId = firebaseUser.uid,
                    name = firebaseUser.displayName ?: "Administrator",
                    email = firebaseUser.email ?: "",
                    passwordHash = "", // Don't store password hash
                    isEmailVerified = firebaseUser.isEmailVerified,
                    role = UserRole.ADMIN,
                    language = "en"
                )
                _currentUser.value = adminUser
                return@withContext adminUser
            } else {
                // Try to fetch student data
                val studentDoc = firestore.collection("students")
                    .whereEqualTo("email", firebaseUser.email)
                    .get()
                    .await()
                
                if (!studentDoc.isEmpty) {
                    val document = studentDoc.documents[0]
                    val studentData = document.data
                    
                    if (studentData != null) {
                        val student = Student(
                            studentId = studentData["id"] as String,
                            userId = firebaseUser.uid,
                            name = studentData["fullName"] as String,
                            level = studentData["level"] as String,
                            cumulativeGPA = (studentData["cumulativeGPA"] as? Number)?.toFloat() ?: 0.0f,
                            enrolledCourses = (studentData["enrolledCourses"] as? List<String>) ?: emptyList()
                        )
                        
                        val user = User(
                            userId = firebaseUser.uid,
                            name = student.name,
                            email = firebaseUser.email ?: "",
                            passwordHash = "", // Don't store password hash
                            isEmailVerified = firebaseUser.isEmailVerified,
                            role = UserRole.STUDENT,
                            language = "en"
                        )
                        
                        _currentUser.value = user
                        _currentStudent.value = student
                        return@withContext user
                    }
                }
                
                // Default user if no specific role found
                val defaultUser = User(
                    userId = firebaseUser.uid,
                    name = firebaseUser.displayName ?: "User",
                    email = firebaseUser.email ?: "",
                    passwordHash = "", // Don't store password hash
                    isEmailVerified = firebaseUser.isEmailVerified,
                    role = UserRole.STUDENT,
                    language = "en"
                )
                _currentUser.value = defaultUser
                return@withContext defaultUser
            }
        } catch (e: Exception) {
            Log.e("FirebaseAuthRepo", "Error fetching user data", e)
            
            // Return basic user info if Firestore fetch fails
            val basicUser = User(
                userId = firebaseUser.uid,
                name = firebaseUser.displayName ?: "User",
                email = firebaseUser.email ?: "",
                passwordHash = "", // Don't store password hash
                isEmailVerified = firebaseUser.isEmailVerified,
                role = UserRole.STUDENT,
                language = "en"
            )
            _currentUser.value = basicUser
            return@withContext basicUser
        }
    }
}

package com.example.tbssmarthub.data.repository

import com.example.tbssmarthub.data.model.User
import com.example.tbssmarthub.data.model.UserRole
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Repository for handling user authentication and profile management
 * Implements a simple in-memory backend for demonstration purposes
 */
class UserRepository {
    // In-memory database of users
    private val users = mutableListOf(
        User(
            userId = "admin",
            name = "Administrator",
            email = "admin@tbsuniversity.edu",
            passwordHash = "admin123",
            isEmailVerified = true,
            role = UserRole.ADMIN,
            language = "en"
        ),
        User(
            userId = "student1",
            name = "Yosser",
            email = "yosser@tbsuniversity.edu",
            passwordHash = "password123",
            isEmailVerified = true,
            role = UserRole.STUDENT,
            language = "en"
        ),
        User(
            userId = "prof1",
            name = "Elynn Lee",
            email = "elynn@tbsuniversity.edu",
            passwordHash = "professor123",
            isEmailVerified = true,
            role = UserRole.PROFESSOR,
            language = "en"
        )
    )

    // Current authenticated user
    private val _currentUser = MutableStateFlow<User?>(null)
    val currentUser: StateFlow<User?> = _currentUser.asStateFlow()

    /**
     * Authenticate a user with userId/email and password
     * @param userIdOrEmail The user ID or email for authentication
     * @param password The user's password
     * @return The authenticated user or null if authentication fails
     */
    fun login(userIdOrEmail: String, password: String): User? {
        val user = users.find { 
            (it.userId == userIdOrEmail || it.email == userIdOrEmail) && it.passwordHash == password 
        }
        
        if (user != null) {
            _currentUser.value = user
        }
        
        return user
    }

    /**
     * Register a new user in the system
     * @param userId The unique user ID
     * @param name The user's full name
     * @param email The user's email address
     * @param password The user's password (will be stored as-is for demo purposes)
     * @param role The user's role in the system
     * @return The newly created user or null if registration fails
     */
    fun register(
        userId: String,
        name: String,
        email: String,
        password: String,
        role: UserRole = UserRole.STUDENT
    ): User? {
        // Check if user already exists
        if (users.any { it.userId == userId || it.email == email }) {
            return null
        }
        
        // Create new user
        val newUser = User(
            userId = userId,
            name = name,
            email = email,
            passwordHash = password,
            isEmailVerified = false,
            role = role,
            language = "en"
        )
        
        // Add to in-memory database
        users.add(newUser)
        
        // Set as current user
        _currentUser.value = newUser
        
        return newUser
    }

    /**
     * Log out the current user
     */
    fun logout() {
        _currentUser.value = null
    }

    /**
     * Update user profile information
     * @param userId The ID of the user to update
     * @param name The new name (optional)
     * @param email The new email (optional)
     * @param password The new password (optional)
     * @return The updated user or null if update fails
     */
    fun updateUserProfile(
        userId: String,
        name: String? = null,
        email: String? = null,
        password: String? = null
    ): User? {
        val userIndex = users.indexOfFirst { it.userId == userId }
        
        if (userIndex == -1) {
            return null
        }
        
        val currentUser = users[userIndex]
        
        // Create updated user
        val updatedUser = currentUser.copy(
            name = name ?: currentUser.name,
            email = email ?: currentUser.email,
            passwordHash = password ?: currentUser.passwordHash
        )
        
        // Update in-memory database
        users[userIndex] = updatedUser
        
        // Update current user if this is the logged-in user
        if (_currentUser.value?.userId == userId) {
            _currentUser.value = updatedUser
        }
        
        return updatedUser
    }

    /**
     * Get a user by ID
     * @param userId The ID of the user to retrieve
     * @return The user or null if not found
     */
    fun getUserById(userId: String): User? {
        return users.find { it.userId == userId }
    }

    /**
     * Get all users with a specific role
     * @param role The role to filter by
     * @return List of users with the specified role
     */
    fun getUsersByRole(role: UserRole): List<User> {
        return users.filter { it.role == role }
    }

    companion object {
        // Singleton instance
        private var INSTANCE: UserRepository? = null
        
        fun getInstance(): UserRepository {
            return INSTANCE ?: synchronized(this) {
                val instance = UserRepository()
                INSTANCE = instance
                instance
            }
        }
    }
}

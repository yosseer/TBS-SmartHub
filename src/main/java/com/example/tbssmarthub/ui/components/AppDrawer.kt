package com.example.tbssmarthub.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.tbssmarthub.R
import com.example.tbssmarthub.data.model.UserRole
import com.example.tbssmarthub.data.repository.FirebaseAuthRepository
import com.example.tbssmarthub.navigation.NavRoutes
import com.example.tbssmarthub.navigation.navigateSafely
import com.example.tbssmarthub.navigation.navigateToComingSoon
import com.example.tbssmarthub.ui.theme.TbsPrimaryGold
import com.example.tbssmarthub.ui.theme.TbsSecondaryDark
import com.example.tbssmarthub.ui.theme.TbsSecondaryLight
import com.example.tbssmarthub.ui.theme.TbsTertiaryBlue

/**
 * Enhanced AppDrawer component with additional features
 * Includes anonymous feedback, settings options, and dark mode toggle
 * Adapts menu items based on user role (admin vs student)
 * 
 * @param navController Navigation controller for screen navigation
 * @param onCloseDrawer Callback to close the drawer
 * @param isDarkMode Current dark mode state
 * @param onDarkModeChange Callback when dark mode toggle is changed
 */
@Composable
fun AppDrawer(
    navController: NavController,
    onCloseDrawer: () -> Unit,
    isDarkMode: Boolean,
    onDarkModeChange: (Boolean) -> Unit
) {
    // Get Firebase auth repository instance
    val firebaseAuthRepository = FirebaseAuthRepository()
    val currentUser = firebaseAuthRepository.currentUser.value
    
    // Determine if user is admin
    val isAdmin = currentUser?.role == UserRole.ADMIN
    
    Column(
        modifier = Modifier
            .fillMaxHeight()
            .width(300.dp)
            .background(if (isDarkMode) TbsSecondaryDark else Color.White)
    ) {
        // Drawer Header with User Profile
        DrawerHeader(
            userName = currentUser?.name ?: "Guest User",
            profileImageRes = R.drawable.profile_picture,
            onProfileClick = {
                onCloseDrawer()
                navController.navigateToComingSoon("Profile")
            },
            isDarkMode = isDarkMode
        )

        Divider(color = if (isDarkMode) Color.DarkGray else Color.LightGray, thickness = 1.dp)

        // Drawer Menu Items
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .padding(vertical = 8.dp)
        ) {
            // Dashboard Item - different for admin and student
            DrawerMenuItem(
                icon = Icons.Default.Home,
                title = if (isAdmin) "Admin Dashboard" else "Dashboard",
                onClick = {
                    onCloseDrawer()
                    if (isAdmin) {
                        navController.navigateSafely(NavRoutes.ADMIN_DASHBOARD)
                    } else {
                        navController.navigateSafely(NavRoutes.HOME)
                    }
                },
                isDarkMode = isDarkMode
            )

            // Calendar Item
            DrawerMenuItem(
                icon = Icons.Default.DateRange,
                title = "Calendar",
                onClick = {
                    onCloseDrawer()
                    navController.navigateSafely(NavRoutes.CALENDAR)
                },
                isDarkMode = isDarkMode
            )

            // Chatbot Item with special highlight
            DrawerMenuItem(
                icon = Icons.Default.Chat,
                title = "TBS ChatBot",
                isHighlighted = true,
                onClick = {
                    onCloseDrawer()
                    navController.navigateSafely(NavRoutes.CHATBOT)
                },
                isDarkMode = isDarkMode
            )

            // Teachers Item
            DrawerMenuItem(
                icon = Icons.Default.People,
                title = "My Teachers",
                onClick = {
                    onCloseDrawer()
                    navController.navigateSafely(NavRoutes.TEACHERS)
                },
                isDarkMode = isDarkMode
            )

            // Events Item
            DrawerMenuItem(
                icon = Icons.Default.Event,
                title = "Upcoming Events",
                onClick = {
                    onCloseDrawer()
                    navController.navigateSafely(NavRoutes.EVENTS)
                },
                isDarkMode = isDarkMode
            )
            
            // Anonymous Feedback Item
            DrawerMenuItem(
                icon = Icons.Default.Feedback,
                title = "Anonymous Feedback",
                onClick = {
                    onCloseDrawer()
                    navController.navigateSafely(NavRoutes.ANONYMOUS_FEEDBACK)
                },
                isDarkMode = isDarkMode
            )

            Divider(
                modifier = Modifier.padding(vertical = 8.dp),
                color = if (isDarkMode) Color.DarkGray else Color.LightGray,
                thickness = 0.5.dp
            )

            // Settings Item
            DrawerMenuItem(
                icon = Icons.Default.Settings,
                title = "Settings",
                onClick = {
                    onCloseDrawer()
                    navController.navigateSafely(NavRoutes.SETTINGS)
                },
                isDarkMode = isDarkMode
            )

            // Logout Item
            DrawerMenuItem(
                icon = Icons.Default.ExitToApp,
                title = "Logout",
                onClick = {
                    onCloseDrawer()
                    firebaseAuthRepository.signOut()
                    navController.navigate(NavRoutes.LOGIN) {
                        popUpTo(NavRoutes.LOGIN) { inclusive = true }
                    }
                },
                isDarkMode = isDarkMode
            )
            
            // Add more space before the dark mode toggle
            Spacer(modifier = Modifier.height(16.dp))
            
            // Dark Mode Toggle
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = if (isDarkMode) Icons.Default.DarkMode else Icons.Default.LightMode,
                        contentDescription = "Dark Mode",
                        tint = if (isDarkMode) TbsPrimaryGold else TbsSecondaryDark
                    )
                    
                    Spacer(modifier = Modifier.width(32.dp))
                    
                    Text(
                        text = "Dark Mode",
                        fontSize = 16.sp,
                        color = if (isDarkMode) Color.White else TbsSecondaryDark
                    )
                }
                
                Switch(
                    checked = isDarkMode,
                    onCheckedChange = onDarkModeChange,
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = TbsPrimaryGold,
                        checkedTrackColor = TbsPrimaryGold.copy(alpha = 0.5f),
                        uncheckedThumbColor = TbsSecondaryLight,
                        uncheckedTrackColor = TbsSecondaryLight.copy(alpha = 0.5f)
                    )
                )
            }
        }

        // App Version at the bottom
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            contentAlignment = Alignment.BottomCenter
        ) {
            Text(
                text = "TBS SmartHub v1.0",
                color = if (isDarkMode) Color.Gray else Color.Gray,
                fontSize = 12.sp
            )
        }
    }
}

@Composable
fun DrawerHeader(
    userName: String,
    profileImageRes: Int,
    onProfileClick: () -> Unit,
    isDarkMode: Boolean
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(180.dp)
            .background(
                color = TbsPrimaryGold.copy(alpha = if (isDarkMode) 0.3f else 0.2f),
                shape = RoundedCornerShape(bottomEnd = 24.dp)
            )
            .padding(16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize(),
            verticalArrangement = Arrangement.Center
        ) {
            // Logo
            Image(
                painter = painterResource(id = R.drawable.logo_tbs),
                contentDescription = "TBS Logo",
                modifier = Modifier
                    .size(40.dp)
                    .padding(bottom = 8.dp)
            )

            // User Profile
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onProfileClick() }
                    .padding(vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Profile Image
                Image(
                    painter = painterResource(id = profileImageRes),
                    contentDescription = "Profile Picture",
                    modifier = Modifier
                        .size(64.dp)
                        .clip(CircleShape),
                    contentScale = ContentScale.Crop
                )

                Spacer(modifier = Modifier.width(16.dp))

                // User Name and View Profile
                Column {
                    Text(
                        text = userName,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isDarkMode) Color.White else Color.Black
                    )

                    Text(
                        text = "View Profile",
                        fontSize = 14.sp,
                        color = TbsTertiaryBlue
                    )
                }
            }
        }
    }
}

@Composable
fun DrawerMenuItem(
    icon: ImageVector,
    title: String,
    isHighlighted: Boolean = false,
    onClick: () -> Unit,
    isDarkMode: Boolean
) {
    val backgroundColor = when {
        isHighlighted && isDarkMode -> TbsPrimaryGold.copy(alpha = 0.3f)
        isHighlighted -> TbsPrimaryGold.copy(alpha = 0.2f)
        else -> Color.Transparent
    }

    val textColor = when {
        isHighlighted -> TbsTertiaryBlue
        isDarkMode -> Color.White
        else -> Color.Black
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .background(backgroundColor)
            .padding(vertical = 12.dp, horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = title,
            tint = textColor,
            modifier = Modifier.size(24.dp)
        )

        Spacer(modifier = Modifier.width(32.dp))

        Text(
            text = title,
            fontSize = 16.sp,
            color = textColor,
            fontWeight = if (isHighlighted) FontWeight.Bold else FontWeight.Normal
        )

        if (isHighlighted) {
            Spacer(modifier = Modifier.weight(1f))

            // New badge for chatbot
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = TbsTertiaryBlue
            ) {
                Text(
                    text = "NEW",
                    color = Color.White,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                )
            }
        }
    }
}

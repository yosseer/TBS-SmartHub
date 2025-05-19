package com.example.tbssmarthub.ui.admin

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.tbssmarthub.R
import com.example.tbssmarthub.data.model.User
import com.example.tbssmarthub.data.model.UserRole
import com.example.tbssmarthub.data.repository.UserRepository
import com.example.tbssmarthub.navigation.NavRoutes
import com.example.tbssmarthub.navigation.navigateToComingSoon
import com.example.tbssmarthub.ui.components.DrawerScaffold
import com.example.tbssmarthub.ui.theme.TbsPrimaryGold
import com.example.tbssmarthub.ui.theme.TbsPrimaryGoldLight
import com.example.tbssmarthub.ui.theme.TbsSecondaryDark
import com.example.tbssmarthub.ui.theme.TbsTertiaryBlue

/**
 * Admin Dashboard Screen
 * Displays administrator-specific features and management options
 * Only accessible when logged in with admin credentials
 * 
 * @param navController Navigation controller for screen navigation
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminDashboardScreen(navController: NavController) {
    // Get user repository instance
    val userRepository = UserRepository.getInstance()
    
    // Get current user (should be admin)
    val currentUser = userRepository.currentUser.collectAsState().value
    
    // Verify admin access
    if (currentUser == null || currentUser.role != UserRole.ADMIN) {
        // Redirect to login if not admin
        LaunchedEffect(Unit) {
            navController.navigate(NavRoutes.LOGIN) {
                popUpTo(NavRoutes.LOGIN) { inclusive = true }
            }
        }
        return
    }
    
    // Admin dashboard content
    DrawerScaffold(
        navController = navController,
        title = "Admin Dashboard",
        content = { paddingValues ->
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Admin greeting
                item {
                    AdminGreetingCard(name = currentUser.name)
                }
                
                // Admin stats
                item {
                    AdminStatsRow()
                }
                
                // Admin action cards
                item {
                    Text(
                        text = "Management",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(vertical = 8.dp)
                    )
                }
                
                // Management options in a grid
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        AdminActionCard(
                            title = "Manage Users",
                            icon = Icons.Default.People,
                            modifier = Modifier.weight(1f),
                            onClick = { navController.navigateToComingSoon("User Management") }
                        )
                        
                        AdminActionCard(
                            title = "Add Event",
                            icon = Icons.Default.Event,
                            modifier = Modifier.weight(1f),
                            onClick = { navController.navigateToComingSoon("Event Management") }
                        )
                    }
                }
                
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        AdminActionCard(
                            title = "Course Management",
                            icon = Icons.Default.School,
                            modifier = Modifier.weight(1f),
                            onClick = { navController.navigateToComingSoon("Course Management") }
                        )
                        
                        AdminActionCard(
                            title = "Announcements",
                            icon = Icons.Default.Announcement,
                            modifier = Modifier.weight(1f),
                            onClick = { navController.navigateToComingSoon("Announcements") }
                        )
                    }
                }
                
                // Recent activity section
                item {
                    Text(
                        text = "Recent Activity",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(vertical = 8.dp)
                    )
                }
                
                // Sample activity items
                items(getSampleActivityItems()) { activity ->
                    ActivityItem(activity)
                }
                
                // Bottom padding
                item {
                    Spacer(modifier = Modifier.height(16.dp))
                }
            }
        }
    )
}

/**
 * Admin greeting card component
 */
@Composable
fun AdminGreetingCard(name: String) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White.copy(alpha = 0.9f)
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = "Welcome, Administrator $name!",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
                
                Spacer(modifier = Modifier.height(4.dp))
                
                Text(
                    text = "You have administrative access to manage users, courses, and system settings.",
                    fontSize = 14.sp,
                    color = Color.Gray
                )
            }
            
            // Admin icon
            Icon(
                imageVector = Icons.Default.AdminPanelSettings,
                contentDescription = "Admin",
                modifier = Modifier
                    .size(64.dp)
                    .padding(8.dp),
                tint = TbsPrimaryGold
            )
        }
    }
}

/**
 * Admin statistics row component
 */
@Composable
fun AdminStatsRow() {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        StatCard(
            title = "Students",
            value = "124",
            icon = Icons.Default.Person,
            modifier = Modifier.weight(1f)
        )
        
        StatCard(
            title = "Professors",
            value = "18",
            icon = Icons.Default.School,
            modifier = Modifier.weight(1f)
        )
        
        StatCard(
            title = "Courses",
            value = "32",
            icon = Icons.Default.Book,
            modifier = Modifier.weight(1f)
        )
    }
}

/**
 * Statistic card component
 */
@Composable
fun StatCard(
    title: String,
    value: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = icon,
                contentDescription = title,
                tint = TbsTertiaryBlue,
                modifier = Modifier.size(24.dp)
            )
            
            Spacer(modifier = Modifier.height(4.dp))
            
            Text(
                text = value,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )
            
            Text(
                text = title,
                fontSize = 12.sp,
                color = Color.Gray
            )
        }
    }
}

/**
 * Admin action card component
 */
@Composable
fun AdminActionCard(
    title: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Card(
        modifier = modifier
            .height(120.dp)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = title,
                tint = TbsPrimaryGold,
                modifier = Modifier.size(32.dp)
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = title,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )
        }
    }
}

/**
 * Activity data class for sample data
 */
data class ActivityItem(
    val title: String,
    val description: String,
    val time: String,
    val type: ActivityType
)

/**
 * Activity type enum for categorizing activities
 */
enum class ActivityType {
    USER, COURSE, EVENT, SYSTEM
}

/**
 * Get sample activity items for the admin dashboard
 */
fun getSampleActivityItems(): List<ActivityItem> {
    return listOf(
        ActivityItem(
            title = "New User Registration",
            description = "Student Ahmed Khalil registered an account",
            time = "Today, 10:23 AM",
            type = ActivityType.USER
        ),
        ActivityItem(
            title = "Course Added",
            description = "Prof. Elynn Lee added 'Advanced Mobile Development'",
            time = "Today, 09:15 AM",
            type = ActivityType.COURSE
        ),
        ActivityItem(
            title = "System Update",
            description = "System maintenance completed successfully",
            time = "Yesterday, 11:30 PM",
            type = ActivityType.SYSTEM
        ),
        ActivityItem(
            title = "Event Created",
            description = "New event 'Hackathon 2025' scheduled",
            time = "Yesterday, 03:45 PM",
            type = ActivityType.EVENT
        )
    )
}

/**
 * Activity item component
 */
@Composable
fun ActivityItem(activity: ActivityItem) {
    val iconAndColor = when (activity.type) {
        ActivityType.USER -> Icons.Default.Person to TbsTertiaryBlue
        ActivityType.COURSE -> Icons.Default.School to TbsPrimaryGold
        ActivityType.EVENT -> Icons.Default.Event to Color(0xFF4CAF50)
        ActivityType.SYSTEM -> Icons.Default.Settings to Color(0xFF9C27B0)
    }
    
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        shape = RoundedCornerShape(8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Activity icon
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(iconAndColor.second.copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = iconAndColor.first,
                    contentDescription = activity.type.name,
                    tint = iconAndColor.second,
                    modifier = Modifier.size(24.dp)
                )
            }
            
            Spacer(modifier = Modifier.width(12.dp))
            
            // Activity details
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = activity.title,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
                
                Text(
                    text = activity.description,
                    fontSize = 14.sp,
                    color = Color.Gray
                )
            }
            
            // Activity time
            Text(
                text = activity.time,
                fontSize = 12.sp,
                color = Color.Gray
            )
        }
    }
}

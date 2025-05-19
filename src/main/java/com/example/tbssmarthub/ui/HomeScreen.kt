package com.example.tbssmarthub.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowRight
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
import com.example.tbssmarthub.data.model.Event
import com.example.tbssmarthub.data.repository.EventRepository
import com.example.tbssmarthub.data.repository.FirebaseAuthRepository
import com.example.tbssmarthub.navigation.NavRoutes
import com.example.tbssmarthub.ui.components.DrawerScaffold
import com.example.tbssmarthub.ui.theme.TbsPrimaryGold
import com.example.tbssmarthub.ui.theme.TbsSecondaryLight
import com.example.tbssmarthub.ui.theme.TbsTertiaryBlue
import kotlinx.coroutines.flow.collectAsState

/**
 * Home screen displaying personalized dashboard for the logged-in user
 * Fetches user data from Firebase and displays personalized greeting
 * Shows calendar events, linked teachers, and upcoming events
 * 
 * @param navController Navigation controller for screen navigation
 */
@Composable
fun HomeScreen(navController: NavController) {
    // Get Firebase auth repository instance
    val firebaseAuthRepository = remember { FirebaseAuthRepository() }
    
    // Collect current user and student data as state
    val currentUser by firebaseAuthRepository.currentUser.collectAsState()
    val currentStudent by firebaseAuthRepository.currentStudent.collectAsState()
    
    // Get user name for greeting
    val userName = currentUser?.name ?: "Student"
    
    // Get event repository instance
    val eventRepository = remember { EventRepository.getInstance() }
    val events by eventRepository.todayEvents.collectAsState()
    
    DrawerScaffold(
        navController = navController,
        title = "Dashboard"
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Greeting card
            item {
                GreetingCard(
                    name = userName,
                    taskCount = 3,
                    onReviewClick = {
                        // Navigate to tasks or review page
                        navController.navigate(NavRoutes.COMING_SOON + "/Tasks")
                    }
                )
            }
            
            // Student level information if available
            if (currentStudent != null) {
                item {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        shape = RoundedCornerShape(16.dp),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp)
                        ) {
                            Text(
                                text = "Student Information",
                                fontWeight = FontWeight.Bold,
                                fontSize = 18.sp
                            )
                            
                            Spacer(modifier = Modifier.height(8.dp))
                            
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Column {
                                    Text(
                                        text = "Matricule ID",
                                        fontSize = 14.sp,
                                        color = Color.Gray
                                    )
                                    Text(
                                        text = currentStudent!!.studentId,
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.Medium
                                    )
                                }
                                
                                Column {
                                    Text(
                                        text = "Level",
                                        fontSize = 14.sp,
                                        color = Color.Gray
                                    )
                                    Text(
                                        text = currentStudent!!.level,
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.Medium
                                    )
                                }
                            }
                        }
                    }
                }
            }
            
            // Calendar section
            item {
                Text(
                    text = "Calendar",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(top = 8.dp, bottom = 4.dp)
                )
                
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "${events.size} events today",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Medium
                            )
                            
                            TextButton(
                                onClick = { navController.navigate(NavRoutes.CALENDAR) }
                            ) {
                                Text("View All")
                            }
                        }
                        
                        Spacer(modifier = Modifier.height(8.dp))
                        
                        // Display events
                        events.take(3).forEach { event ->
                            EventItem(
                                event = event,
                                onClick = { navController.navigate(NavRoutes.CALENDAR) }
                            )
                        }
                        
                        if (events.isEmpty()) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 16.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "No events scheduled for today",
                                    color = Color.Gray
                                )
                            }
                        }
                    }
                }
            }
            
            // Linked Teachers section
            item {
                Text(
                    text = "Linked Teachers",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(top = 8.dp, bottom = 4.dp)
                )
                
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        // Sample teachers - in a real app, these would come from Firebase
                        TeacherItem(
                            name = "Elynn Lee",
                            email = "email@fakedomain.net",
                            imageRes = R.drawable.profile_picture
                        )
                        
                        Divider(modifier = Modifier.padding(vertical = 8.dp))
                        
                        TeacherItem(
                            name = "Oscar Dum",
                            email = "email@fakedomain.net",
                            imageRes = R.drawable.profile_picture
                        )
                        
                        Divider(modifier = Modifier.padding(vertical = 8.dp))
                        
                        TeacherItem(
                            name = "Carlo Emilion",
                            email = "email@fakedomain.net",
                            imageRes = R.drawable.profile_picture
                        )
                    }
                }
            }
            
            // Upcoming Events section
            item {
                Text(
                    text = "Upcoming Events",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(top = 8.dp, bottom = 4.dp)
                )
                
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        // Sample upcoming events - in a real app, these would come from Firebase
                        UpcomingEventItem(
                            title = "Hack 'n' Slash",
                            organization = "MERIT"
                        )
                        
                        Divider(modifier = Modifier.padding(vertical = 8.dp))
                        
                        UpcomingEventItem(
                            title = "Health Pulse",
                            organization = "ATLAS Future Leader"
                        )
                        
                        Divider(modifier = Modifier.padding(vertical = 8.dp))
                        
                        UpcomingEventItem(
                            title = "Shark Tank",
                            organization = "JCI TBS"
                        )
                    }
                }
            }
            
            // Bottom spacing
            item {
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}

@Composable
fun GreetingCard(
    name: String,
    taskCount: Int,
    onReviewClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 16.dp),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = "Hello $name!",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                Text(
                    text = "You have $taskCount new tasks. It is a lot of work for today! So let's start!",
                    fontSize = 16.sp
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                TextButton(
                    onClick = onReviewClick,
                    colors = ButtonDefaults.textButtonColors(
                        contentColor = TbsTertiaryBlue
                    )
                ) {
                    Text(
                        text = "review it",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
            
            Image(
                painter = painterResource(id = R.drawable.student),
                contentDescription = "Student",
                modifier = Modifier
                    .size(120.dp)
                    .padding(start = 16.dp)
            )
        }
    }
}

@Composable
fun EventItem(
    event: Event,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Time column
        Column(
            horizontalAlignment = Alignment.End,
            modifier = Modifier.width(50.dp)
        ) {
            Text(
                text = event.formattedTime,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium
            )
        }
        
        // Colored indicator
        Box(
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .size(width = 4.dp, height = 40.dp)
                .background(TbsPrimaryGold, shape = RoundedCornerShape(2.dp))
        )
        
        // Event details
        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = event.title,
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

@Composable
fun TeacherItem(
    name: String,
    email: String,
    imageRes: Int
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Teacher image
        Image(
            painter = painterResource(id = imageRes),
            contentDescription = "Teacher Profile",
            modifier = Modifier
                .size(48.dp)
                .clip(CircleShape),
            contentScale = ContentScale.Crop
        )
        
        // Teacher details
        Column(
            modifier = Modifier
                .weight(1f)
                .padding(start = 16.dp)
        ) {
            Text(
                text = name,
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium
            )
            
            Text(
                text = email,
                fontSize = 14.sp,
                color = Color.Gray
            )
        }
    }
}

@Composable
fun UpcomingEventItem(
    title: String,
    organization: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Event details
        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = title,
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium
            )
            
            Text(
                text = organization,
                fontSize = 14.sp,
                color = Color.Gray
            )
        }
        
        // Arrow icon
        Icon(
            imageVector = Icons.Default.KeyboardArrowRight,
            contentDescription = "View Event",
            tint = TbsSecondaryLight
        )
    }
}

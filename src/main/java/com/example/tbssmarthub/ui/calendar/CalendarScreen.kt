package com.example.tbssmarthub.ui.calendar

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.tbssmarthub.data.model.Event
import com.example.tbssmarthub.data.repository.EventRepository
import com.example.tbssmarthub.ui.components.DrawerScaffold
import com.example.tbssmarthub.ui.theme.TbsPrimaryGold
import com.example.tbssmarthub.ui.theme.TbsPrimaryGoldLight
import com.example.tbssmarthub.ui.theme.TbsSecondaryDark
import com.example.tbssmarthub.ui.theme.TbsTertiaryBlue
import java.text.SimpleDateFormat
import java.util.*

/**
 * Calendar screen that displays a monthly calendar view and daily events
 * Implements a real calendar with event integration
 * 
 * @param navController Navigation controller for screen navigation
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CalendarScreen(navController: NavController) {
    // Get event repository instance
    val eventRepository = EventRepository.getInstance()
    
    // State for selected date
    val today = remember { Calendar.getInstance() }
    val selectedDate = remember { mutableStateOf(today.time) }
    
    // State for current month view
    val currentMonth = remember { mutableStateOf(Calendar.getInstance()) }
    
    // Get events for selected date
    val eventsForSelectedDate = remember(selectedDate.value) {
        eventRepository.getEventsForDate(selectedDate.value)
    }
    
    // Format for month/year display
    val monthYearFormat = remember { SimpleDateFormat("MMMM yyyy", Locale.getDefault()) }
    
    // Drawer scaffold for consistent UI
    DrawerScaffold(
        navController = navController,
        title = "Calendar",
        content = { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(16.dp)
            ) {
                // Calendar header with month navigation
                CalendarHeader(
                    currentMonth = currentMonth.value.time,
                    onPreviousMonth = {
                        currentMonth.value.add(Calendar.MONTH, -1)
                    },
                    onNextMonth = {
                        currentMonth.value.add(Calendar.MONTH, 1)
                    },
                    monthYearFormat = monthYearFormat
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // Calendar grid
                MonthCalendarGrid(
                    currentMonth = currentMonth.value,
                    selectedDate = selectedDate.value,
                    onDateSelected = { date ->
                        selectedDate.value = date
                    },
                    eventRepository = eventRepository
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // Events for selected date
                DailyEventsSection(
                    date = selectedDate.value,
                    events = eventsForSelectedDate
                )
            }
        }
    )
}

/**
 * Calendar header component with month navigation
 */
@Composable
fun CalendarHeader(
    currentMonth: Date,
    onPreviousMonth: () -> Unit,
    onNextMonth: () -> Unit,
    monthYearFormat: SimpleDateFormat
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Previous month button
        IconButton(onClick = onPreviousMonth) {
            Icon(
                imageVector = Icons.Default.ArrowBack,
                contentDescription = "Previous Month"
            )
        }
        
        // Current month/year display
        Text(
            text = monthYearFormat.format(currentMonth),
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold
        )
        
        // Next month button
        IconButton(onClick = onNextMonth) {
            Icon(
                imageVector = Icons.Default.ArrowForward,
                contentDescription = "Next Month"
            )
        }
    }
}

/**
 * Month calendar grid component
 * Displays days of the week and dates in a grid format
 */
@Composable
fun MonthCalendarGrid(
    currentMonth: Calendar,
    selectedDate: Date,
    onDateSelected: (Date) -> Unit,
    eventRepository: EventRepository
) {
    // Days of week headers
    val daysOfWeek = listOf("Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat")
    
    // Create a copy of the current month to manipulate
    val calendar = currentMonth.clone() as Calendar
    
    // Set to first day of month
    calendar.set(Calendar.DAY_OF_MONTH, 1)
    
    // Get day of week for first day (0 = Sunday, 1 = Monday, etc.)
    val firstDayOfWeek = calendar.get(Calendar.DAY_OF_WEEK) - 1
    
    // Get number of days in month
    val daysInMonth = calendar.getActualMaximum(Calendar.DAY_OF_MONTH)
    
    // Today's date for highlighting
    val today = Calendar.getInstance()
    val isCurrentMonth = today.get(Calendar.YEAR) == calendar.get(Calendar.YEAR) && 
                         today.get(Calendar.MONTH) == calendar.get(Calendar.MONTH)
    
    // Format for comparing dates
    val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    val selectedDateStr = dateFormat.format(selectedDate)
    val todayDateStr = dateFormat.format(today.time)
    
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                color = MaterialTheme.colorScheme.surface,
                shape = RoundedCornerShape(16.dp)
            )
            .padding(16.dp)
    ) {
        // Days of week header row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            daysOfWeek.forEach { day ->
                Text(
                    text = day,
                    modifier = Modifier.weight(1f),
                    textAlign = TextAlign.Center,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    color = if (day == "Sun" || day == "Sat") TbsTertiaryBlue else TbsSecondaryDark
                )
            }
        }
        
        Spacer(modifier = Modifier.height(8.dp))
        
        // Calendar grid
        for (row in 0 until 6) { // Maximum 6 rows needed for a month
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                for (col in 0 until 7) { // 7 days in a week
                    val day = row * 7 + col - firstDayOfWeek + 1
                    
                    if (day in 1..daysInMonth) {
                        // Set calendar to this day for comparison
                        calendar.set(Calendar.DAY_OF_MONTH, day)
                        val currentDate = calendar.time
                        val currentDateStr = dateFormat.format(currentDate)
                        
                        // Check if this day has events
                        val hasEvents = eventRepository.getEventsForDate(currentDate).isNotEmpty()
                        
                        // Is this day selected
                        val isSelected = currentDateStr == selectedDateStr
                        
                        // Is this day today
                        val isToday = isCurrentMonth && currentDateStr == todayDateStr
                        
                        // Day cell
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .aspectRatio(1f)
                                .padding(2.dp)
                                .clip(CircleShape)
                                .background(
                                    when {
                                        isSelected -> TbsPrimaryGold
                                        isToday -> TbsPrimaryGoldLight
                                        else -> Color.Transparent
                                    }
                                )
                                .clickable { onDateSelected(currentDate) },
                            contentAlignment = Alignment.Center
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = day.toString(),
                                    fontSize = 14.sp,
                                    fontWeight = if (isToday || isSelected) FontWeight.Bold else FontWeight.Normal,
                                    color = when {
                                        isSelected -> TbsSecondaryDark
                                        isToday -> TbsSecondaryDark
                                        col == 0 || col == 6 -> TbsTertiaryBlue // Weekend
                                        else -> MaterialTheme.colorScheme.onSurface
                                    }
                                )
                                
                                // Event indicator
                                if (hasEvents) {
                                    Box(
                                        modifier = Modifier
                                            .size(4.dp)
                                            .clip(CircleShape)
                                            .background(
                                                if (isSelected) TbsSecondaryDark else TbsPrimaryGold
                                            )
                                    )
                                }
                            }
                        }
                    } else {
                        // Empty cell for days outside current month
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .aspectRatio(1f)
                        )
                    }
                }
            }
        }
    }
}

/**
 * Daily events section component
 * Displays events for the selected date
 */
@Composable
fun DailyEventsSection(
    date: Date,
    events: List<Event>
) {
    val dateFormat = SimpleDateFormat("EEEE, MMMM d, yyyy", Locale.getDefault())
    val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
    
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                color = MaterialTheme.colorScheme.surface,
                shape = RoundedCornerShape(16.dp)
            )
            .padding(16.dp)
    ) {
        // Section header
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.DateRange,
                contentDescription = "Events",
                tint = TbsPrimaryGold
            )
            
            Spacer(modifier = Modifier.width(8.dp))
            
            Text(
                text = "Events for ${dateFormat.format(date)}",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        if (events.isEmpty()) {
            // No events message
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 32.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "No events scheduled for this day",
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )
            }
        } else {
            // Events list
            LazyColumn(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(events.sortedBy { it.startTime }) { event ->
                    EventItem(
                        event = event,
                        timeFormat = timeFormat
                    )
                }
            }
        }
    }
}

/**
 * Event item component
 * Displays a single event with time and details
 */
@Composable
fun EventItem(
    event: Event,
    timeFormat: SimpleDateFormat
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        shape = RoundedCornerShape(8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Time column
            Column(
                modifier = Modifier.width(80.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = timeFormat.format(Date(event.startTime)),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = TbsTertiaryBlue
                )
                
                Text(
                    text = "to",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )
                
                Text(
                    text = timeFormat.format(Date(event.endTime)),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = TbsTertiaryBlue
                )
            }
            
            // Vertical divider
            Divider(
                modifier = Modifier
                    .height(50.dp)
                    .width(1.dp)
                    .padding(horizontal = 8.dp),
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.2f)
            )
            
            // Event details
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = event.title,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
                
                if (event.location.isNotEmpty()) {
                    Text(
                        text = "Location: ${event.location}",
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
                    )
                }
                
                if (event.organizer.isNotEmpty()) {
                    Text(
                        text = "Organizer: ${event.organizer}",
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
                    )
                }
            }
        }
    }
}

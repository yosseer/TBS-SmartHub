package com.example.tbssmarthub.data.repository

import com.example.tbssmarthub.data.model.Event
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.UUID

/**
 * Repository for handling calendar events
 * Implements a simple in-memory backend for demonstration purposes
 */
class EventRepository {
    // In-memory database of events
    private val events = mutableListOf<Event>()
    
    // Current events flow
    private val _allEvents = MutableStateFlow<List<Event>>(emptyList())
    val allEvents: StateFlow<List<Event>> = _allEvents.asStateFlow()
    
    init {
        // Initialize with some sample events
        populateSampleEvents()
    }
    
    /**
     * Get events for a specific date
     * @param date The date to get events for
     * @return List of events on the specified date
     */
    fun getEventsForDate(date: Date): List<Event> {
        val calendar = Calendar.getInstance()
        calendar.time = date
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        val startOfDay = calendar.timeInMillis
        
        calendar.set(Calendar.HOUR_OF_DAY, 23)
        calendar.set(Calendar.MINUTE, 59)
        calendar.set(Calendar.SECOND, 59)
        calendar.set(Calendar.MILLISECOND, 999)
        val endOfDay = calendar.timeInMillis
        
        return events.filter { 
            it.startTime in startOfDay..endOfDay 
        }
    }
    
    /**
     * Add a new event
     * @param title Event title
     * @param description Event description
     * @param startTime Event start time (milliseconds)
     * @param endTime Event end time (milliseconds)
     * @param location Event location (optional)
     * @param organizer Event organizer (optional)
     * @return The newly created event
     */
    fun addEvent(
        title: String,
        description: String,
        startTime: Long,
        endTime: Long,
        location: String = "",
        organizer: String = ""
    ): Event {
        val newEvent = Event(
            eventId = UUID.randomUUID().toString(),
            title = title,
            description = description,
            startTime = startTime,
            endTime = endTime,
            location = location,
            organizer = organizer
        )
        
        events.add(newEvent)
        _allEvents.value = events.toList()
        
        return newEvent
    }
    
    /**
     * Update an existing event
     * @param eventId ID of the event to update
     * @param title New title (optional)
     * @param description New description (optional)
     * @param startTime New start time (optional)
     * @param endTime New end time (optional)
     * @param location New location (optional)
     * @param organizer New organizer (optional)
     * @return The updated event or null if not found
     */
    fun updateEvent(
        eventId: String,
        title: String? = null,
        description: String? = null,
        startTime: Long? = null,
        endTime: Long? = null,
        location: String? = null,
        organizer: String? = null
    ): Event? {
        val eventIndex = events.indexOfFirst { it.eventId == eventId }
        
        if (eventIndex == -1) {
            return null
        }
        
        val currentEvent = events[eventIndex]
        
        // Create updated event
        val updatedEvent = currentEvent.copy(
            title = title ?: currentEvent.title,
            description = description ?: currentEvent.description,
            startTime = startTime ?: currentEvent.startTime,
            endTime = endTime ?: currentEvent.endTime,
            location = location ?: currentEvent.location,
            organizer = organizer ?: currentEvent.organizer
        )
        
        // Update in-memory database
        events[eventIndex] = updatedEvent
        _allEvents.value = events.toList()
        
        return updatedEvent
    }
    
    /**
     * Delete an event
     * @param eventId ID of the event to delete
     * @return True if deleted, false if not found
     */
    fun deleteEvent(eventId: String): Boolean {
        val removed = events.removeIf { it.eventId == eventId }
        
        if (removed) {
            _allEvents.value = events.toList()
        }
        
        return removed
    }
    
    /**
     * Get upcoming events (events that haven't started yet)
     * @param limit Maximum number of events to return
     * @return List of upcoming events
     */
    fun getUpcomingEvents(limit: Int = 5): List<Event> {
        val now = System.currentTimeMillis()
        return events
            .filter { it.startTime > now }
            .sortedBy { it.startTime }
            .take(limit)
    }
    
    /**
     * Populate the repository with sample events
     */
    private fun populateSampleEvents() {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
        val calendar = Calendar.getInstance()
        
        // Reset to today's date
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        
        // Add Advanced Programming sessions for today
        addEvent(
            title = "Advanced Programming session",
            description = "Learn about advanced programming concepts and techniques",
            startTime = getTimeMillis(calendar, 8, 30),
            endTime = getTimeMillis(calendar, 10, 0),
            location = "Room A101",
            organizer = "Prof. Elynn Lee"
        )
        
        addEvent(
            title = "Advanced Programming session",
            description = "Continuation of morning session with practical exercises",
            startTime = getTimeMillis(calendar, 11, 30),
            endTime = getTimeMillis(calendar, 13, 0),
            location = "Room A101",
            organizer = "Prof. Elynn Lee"
        )
        
        addEvent(
            title = "Advanced Programming session",
            description = "Final session with project work",
            startTime = getTimeMillis(calendar, 13, 0),
            endTime = getTimeMillis(calendar, 14, 30),
            location = "Room A101",
            organizer = "Prof. Elynn Lee"
        )
        
        // Add events for tomorrow
        calendar.add(Calendar.DAY_OF_MONTH, 1)
        
        addEvent(
            title = "Database Systems",
            description = "Introduction to database design and SQL",
            startTime = getTimeMillis(calendar, 9, 0),
            endTime = getTimeMillis(calendar, 11, 0),
            location = "Room B202",
            organizer = "Prof. Oscar Dum"
        )
        
        addEvent(
            title = "Hack 'n' Slash Workshop",
            description = "Hands-on workshop for ethical hacking techniques",
            startTime = getTimeMillis(calendar, 13, 0),
            endTime = getTimeMillis(calendar, 16, 0),
            location = "Computer Lab C",
            organizer = "MERIT Club"
        )
        
        // Add events for next week
        calendar.add(Calendar.DAY_OF_MONTH, 6)
        
        addEvent(
            title = "Shark Tank Competition",
            description = "Present your business ideas to potential investors",
            startTime = getTimeMillis(calendar, 14, 0),
            endTime = getTimeMillis(calendar, 18, 0),
            location = "Main Auditorium",
            organizer = "JCI TBS"
        )
        
        // Update the flow with all events
        _allEvents.value = events.toList()
    }
    
    /**
     * Helper method to get time in milliseconds
     */
    private fun getTimeMillis(calendar: Calendar, hourOfDay: Int, minute: Int): Long {
        val newCalendar = calendar.clone() as Calendar
        newCalendar.set(Calendar.HOUR_OF_DAY, hourOfDay)
        newCalendar.set(Calendar.MINUTE, minute)
        return newCalendar.timeInMillis
    }
    
    companion object {
        // Singleton instance
        private var INSTANCE: EventRepository? = null
        
        fun getInstance(): EventRepository {
            return INSTANCE ?: synchronized(this) {
                val instance = EventRepository()
                INSTANCE = instance
                instance
            }
        }
    }
}

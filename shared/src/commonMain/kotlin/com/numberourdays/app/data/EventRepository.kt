package com.numberourdays.app.data

import kotlinx.coroutines.flow.StateFlow

/**
 * Platform-agnostic event store. Android implements this with Room;
 * iOS may implement with SQLite/file storage. UI depends only on this API.
 */
interface EventRepository {
    val events: StateFlow<List<DayEvent>>
    fun loadEvents(): List<DayEvent>
    fun getEvent(id: String): DayEvent?
    fun upsert(event: DayEvent)
    fun remove(id: String)
    fun clearAll()
    fun toggleStar(id: String)
    fun reorderStars(orderedIds: List<String>)
    fun saveAll(events: List<DayEvent>)
    fun createId(): String
    fun ensureDemoIfEmpty()
}

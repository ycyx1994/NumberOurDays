package com.numberourdays.app.data.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "day_events")
data class DayEventEntity(
    @PrimaryKey val id: String,
    val title: String,
    /** Anchor date YYYY-MM-DD */
    val date: String,
    val note: String?,
    val starred: Boolean,
    val starRank: Int?,
    val repeatUnit: String,
    val repeatInterval: Int,
    val remindEnabled: Boolean,
    val remindHour: Int = 9,
    val remindMinute: Int = 0,
    val createdAt: Long,
    val updatedAt: Long,
)

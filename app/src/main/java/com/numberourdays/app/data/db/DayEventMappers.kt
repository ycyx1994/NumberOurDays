package com.numberourdays.app.data.db

import com.numberourdays.app.data.DayEvent
import com.numberourdays.app.data.RepeatUnit

fun DayEvent.toEntity(): DayEventEntity = DayEventEntity(
    id = id,
    title = title,
    date = date,
    note = note,
    starred = starred,
    starRank = starRank,
    repeatUnit = repeatUnit.name.lowercase(),
    repeatInterval = repeatInterval,
    remindEnabled = remindEnabled,
    createdAt = createdAt,
    updatedAt = updatedAt,
)

fun DayEventEntity.toDomain(): DayEvent = DayEvent(
    id = id,
    title = title,
    date = date,
    note = note,
    starred = starred,
    starRank = starRank,
    repeatUnit = RepeatUnit.from(repeatUnit),
    repeatInterval = repeatInterval,
    remindEnabled = remindEnabled,
    createdAt = createdAt,
    updatedAt = updatedAt,
)

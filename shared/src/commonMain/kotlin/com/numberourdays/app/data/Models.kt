package com.numberourdays.app.data

import kotlinx.datetime.Clock

enum class RepeatUnit {
    NONE, DAY, WEEK, MONTH, YEAR;

    companion object {
        fun from(raw: String?): RepeatUnit = when (raw?.lowercase()) {
            "day" -> DAY
            "week" -> WEEK
            "month" -> MONTH
            "year" -> YEAR
            else -> NONE
        }
    }
}

enum class EventKind {
    COUNTDOWN, ELAPSED, TODAY, REPEAT
}

data class DayEvent(
    val id: String,
    val title: String,
    /** Anchor date YYYY-MM-DD */
    val date: String,
    val note: String? = null,
    val starred: Boolean = false,
    val starRank: Int? = null,
    val repeatUnit: RepeatUnit = RepeatUnit.NONE,
    val repeatInterval: Int = 1,
    val remindEnabled: Boolean = false,
    val remindHour: Int = 8,
    val remindMinute: Int = 0,
    val createdAt: Long = Clock.System.now().toEpochMilliseconds(),
    val updatedAt: Long = Clock.System.now().toEpochMilliseconds(),
)

data class DayEventDisplay(
    val event: DayEvent,
    val kind: EventKind,
    val days: Int,
    val elapsedDays: Int,
    val label: String,
    val nextDate: String? = null,
    val repeatLabel: String? = null,
    val sortDate: String,
) {
    val id: String get() = event.id
    val title: String get() = event.title
    val date: String get() = event.date
    val starred: Boolean get() = event.starred
    val note: String? get() = event.note
    val remindEnabled: Boolean get() = event.remindEnabled
    val remindHour: Int get() = event.remindHour
    val remindMinute: Int get() = event.remindMinute
}

data class DurationBreakdown(
    val days: Int,
    val years: Int,
    val monthsAfterYears: Int,
    val daysAfterMonthsFromYears: Int,
    val months: Int,
    val daysAfterMonths: Int,
)

data class DurationDisplay(
    val value: Int,
    val unitLabel: String,
    val rest: String,
)

enum class DurationUnit { DAY, MONTH, YEAR }

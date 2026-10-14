package com.numberourdays.app.notify

import com.numberourdays.app.data.RepeatUnit
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.LocalTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toInstant

class ReminderTimingTest {

    private val day = 86_400_000L

    private fun utcMillis(
        year: Int,
        month: Int,
        dayOfMonth: Int,
        hour: Int = 0,
        minute: Int = 0,
    ): Long = LocalDateTime(LocalDate(year, month, dayOfMonth), LocalTime(hour, minute))
        .toInstant(TimeZone.UTC)
        .toEpochMilliseconds()

    @Test
    fun none_schedulesSameDayNineAm() {
        val now = utcMillis(2026, 9, 21, 8, 0)
        val expected = utcMillis(2026, 9, 21, 9, 0)
        val at = ReminderTiming.nextReminderMillisUtc(
            anchorDate = "2026-09-21",
            unit = RepeatUnit.NONE,
            interval = 1,
            now = now,
        )
        assertEquals(expected, at)
    }

    @Test
    fun none_afterNineAm_noReminder() {
        val now = utcMillis(2026, 9, 21, 10, 0)
        val at = ReminderTiming.nextReminderMillisUtc(
            anchorDate = "2026-09-21",
            unit = RepeatUnit.NONE,
            interval = 1,
            now = now,
        )
        assertNull(at)
    }

    @Test
    fun none_pastDate_noReminder() {
        val now = utcMillis(2026, 9, 21, 8, 0)
        val at = ReminderTiming.nextReminderMillisUtc(
            anchorDate = "2026-09-20",
            unit = RepeatUnit.NONE,
            interval = 1,
            now = now,
        )
        assertNull(at)
    }

    @Test
    fun week_repeat_afterFire_movesToNextWeek() {
        val firedAt = utcMillis(2026, 9, 26, 9, 0)
        val at = ReminderTiming.nextReminderMillisUtc(
            anchorDate = "2026-09-26",
            unit = RepeatUnit.WEEK,
            interval = 1,
            now = firedAt + 1000,
            notBefore = firedAt + 1000,
        )
        assertNotNull(at)
        assertEquals(utcMillis(2026, 10, 3, 9, 0), at)
        assertEquals(7 * day, at!! - utcMillis(2026, 9, 26, 9, 0))
    }

    @Test
    fun week_repeat_sameDayBeforeFireTime_schedulesToday() {
        val now = utcMillis(2026, 9, 26, 18, 0)
        val at = ReminderTiming.nextReminderMillisUtc(
            anchorDate = "2026-09-26",
            unit = RepeatUnit.WEEK,
            interval = 1,
            now = now,
            hourOfDay = 20,
        )
        assertEquals(utcMillis(2026, 9, 26, 20, 0), at)
    }

    @Test
    fun presetHourMinute_alignment() {
        val now = utcMillis(2026, 9, 21, 8, 0)
        val at20 = ReminderTiming.nextReminderMillisUtc(
            anchorDate = "2026-09-21",
            unit = RepeatUnit.NONE,
            interval = 1,
            now = now,
            hourOfDay = 20,
            minuteOfHour = 0,
        )
        assertEquals(utcMillis(2026, 9, 21, 20, 0), at20)

        val at1230 = ReminderTiming.nextReminderMillisUtc(
            anchorDate = "2026-09-21",
            unit = RepeatUnit.NONE,
            interval = 1,
            now = now,
            hourOfDay = 12,
            minuteOfHour = 30,
        )
        assertEquals(utcMillis(2026, 9, 21, 12, 30), at1230)
    }

    @Test
    fun presetAfterFireTime_null() {
        val now = utcMillis(2026, 9, 21, 21, 0)
        val at = ReminderTiming.nextReminderMillisUtc(
            anchorDate = "2026-09-21",
            unit = RepeatUnit.NONE,
            interval = 1,
            now = now,
            hourOfDay = 20,
        )
        assertNull(at)
    }

    @Test
    fun unusedInstantHelper_compiles() {
        // keep Instant import used on all targets
        assertEquals(0L, Instant.fromEpochMilliseconds(0).toEpochMilliseconds())
    }
}

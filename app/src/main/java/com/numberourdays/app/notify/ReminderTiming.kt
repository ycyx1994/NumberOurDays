package com.numberourdays.app.notify

import com.numberourdays.app.data.RepeatUnit
import com.numberourdays.app.domain.DateCalculator
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.ZoneOffset
import kotlin.math.max

/** Pure timing rules for day-of reminders at a configurable wall-clock time. */
object ReminderTiming {
    const val HOUR_OF_DAY = 9

    /**
     * Epoch millis of the next reminder (hourOfDay:minuteOfHour) for this event, or null when none should be scheduled.
     * [notBefore] skips fire times already passed (used after a reminder fires).
     */
    fun nextReminderMillis(
        anchorDate: String,
        unit: RepeatUnit,
        interval: Int,
        now: Long = System.currentTimeMillis(),
        hourOfDay: Int = HOUR_OF_DAY,
        minuteOfHour: Int = 0,
        zone: ZoneId = ZoneId.systemDefault(),
        notBefore: Long = now,
    ): Long? {
        val anchor = DateCalculator.parse(anchorDate) ?: return null
        val zonedNow = Instant.ofEpochMilli(now).atZone(zone)
        val today = zonedNow.toLocalDate()
        val step = max(1, interval)

        val target: LocalDate = if (unit == RepeatUnit.NONE) {
            if (anchor.isBefore(today)) return null
            anchor
        } else {
            var candidate = DateCalculator.resolveNextDate(anchorDate, unit, step, today)
            if (atHour(candidate, hourOfDay, minuteOfHour, zone) <= notBefore) {
                candidate = DateCalculator.resolveNextDate(anchorDate, unit, step, today.plusDays(1))
            }
            candidate
        }

        val fireAt = atHour(target, hourOfDay, minuteOfHour, zone)
        if (fireAt <= notBefore) return null
        return fireAt
    }

    private fun atHour(date: LocalDate, hourOfDay: Int, minuteOfHour: Int, zone: ZoneId): Long =
        date.atTime(hourOfDay, minuteOfHour).atZone(zone).toInstant().toEpochMilli()

    /** Debug helper: same as [nextReminderMillis] but in UTC for tests. */
    fun nextReminderMillisUtc(
        anchorDate: String,
        unit: RepeatUnit,
        interval: Int,
        now: Long,
        hourOfDay: Int = HOUR_OF_DAY,
        minuteOfHour: Int = 0,
        notBefore: Long = now,
    ): Long? = nextReminderMillis(
        anchorDate = anchorDate,
        unit = unit,
        interval = interval,
        now = now,
        hourOfDay = hourOfDay,
        minuteOfHour = minuteOfHour,
        zone = ZoneOffset.UTC,
        notBefore = notBefore,
    )
}

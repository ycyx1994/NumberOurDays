package com.numberourdays.app.notify

import com.numberourdays.app.data.RepeatUnit
import com.numberourdays.app.domain.DateCalculator
import kotlin.math.max
import kotlinx.datetime.Clock
import kotlinx.datetime.DatePeriod
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.LocalTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.plus
import kotlinx.datetime.toInstant
import kotlinx.datetime.toLocalDateTime

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
        now: Long = Clock.System.now().toEpochMilliseconds(),
        hourOfDay: Int = HOUR_OF_DAY,
        minuteOfHour: Int = 0,
        zone: TimeZone = TimeZone.currentSystemDefault(),
        notBefore: Long = now,
    ): Long? {
        val anchor = DateCalculator.parse(anchorDate) ?: return null
        val zonedNow = Instant.fromEpochMilliseconds(now).toLocalDateTime(zone)
        val today = zonedNow.date
        val step = max(1, interval)

        val target: LocalDate = if (unit == RepeatUnit.NONE) {
            if (anchor < today) return null
            anchor
        } else {
            var candidate = DateCalculator.resolveNextDate(anchorDate, unit, step, today)
            if (atHour(candidate, hourOfDay, minuteOfHour, zone) <= notBefore) {
                candidate = DateCalculator.resolveNextDate(
                    anchorDate, unit, step, today.plus(DatePeriod(days = 1)),
                )
            }
            candidate
        }

        val fireAt = atHour(target, hourOfDay, minuteOfHour, zone)
        if (fireAt <= notBefore) return null
        return fireAt
    }

    private fun atHour(date: LocalDate, hourOfDay: Int, minuteOfHour: Int, zone: TimeZone): Long =
        LocalDateTime(date, LocalTime(hourOfDay, minuteOfHour)).toInstant(zone).toEpochMilliseconds()

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
        zone = TimeZone.UTC,
        notBefore = notBefore,
    )
}

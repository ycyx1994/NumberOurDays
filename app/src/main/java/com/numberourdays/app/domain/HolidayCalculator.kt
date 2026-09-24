package com.numberourdays.app.domain

import com.numberourdays.app.data.HolidayCountdown
import com.numberourdays.app.data.HolidayPeriod
import com.numberourdays.app.data.HolidayStatus
import java.time.LocalDate
import java.time.temporal.ChronoUnit

/**
 * Picks the nearest legal-holiday vacation:
 * - if [today] falls inside a vacation block → ONGOING (that block's name);
 * - else the next block whose **first vacation day** is after [today].
 *
 * Ordinary weekends are not holiday targets. A vacation that starts on a
 * weekend day still counts — the target is the block start, not "next weekday".
 * Day counts are natural calendar days (weekends included in the number).
 */
object HolidayCalculator {

    fun nextOrOngoing(
        periods: List<HolidayPeriod>,
        today: LocalDate,
    ): HolidayCountdown? {
        val ongoing = periods.find { !today.isBefore(it.start) && !today.isAfter(it.end) }
        if (ongoing != null) {
            return HolidayCountdown(
                period = ongoing,
                status = HolidayStatus.ONGOING,
                daysUntilStart = 0,
                label = "假期中",
            )
        }
        val upcoming = periods
            .filter { it.start.isAfter(today) }
            .minByOrNull { it.start }
            ?: return null
        val days = ChronoUnit.DAYS.between(today, upcoming.start).toInt()
        return HolidayCountdown(
            period = upcoming,
            status = HolidayStatus.UPCOMING,
            daysUntilStart = days,
            label = if (days <= 0) "今天放假" else "还有 $days 天",
        )
    }
}

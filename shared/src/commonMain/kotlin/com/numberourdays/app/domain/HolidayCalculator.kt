package com.numberourdays.app.domain

import com.numberourdays.app.data.HolidayCountdown
import com.numberourdays.app.data.HolidayPeriod
import com.numberourdays.app.data.HolidayStatus
import kotlinx.datetime.LocalDate

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
        val ongoing = periods.find { today >= it.start && today <= it.end }
        if (ongoing != null) {
            return HolidayCountdown(
                period = ongoing,
                status = HolidayStatus.ONGOING,
                daysUntilStart = 0,
                label = "假期中",
            )
        }
        val upcoming = periods
            .filter { it.start > today }
            .minByOrNull { it.start }
            ?: return null
        val days = DateCalculator.daySpan(today, upcoming.start)
        return HolidayCountdown(
            period = upcoming,
            status = HolidayStatus.UPCOMING,
            daysUntilStart = days,
            label = if (days <= 0) "今天放假" else "还有 $days 天",
        )
    }

    /**
     * Year-list order: not-yet-ended blocks first (ongoing, then nearest start),
     * then past blocks at the bottom (most recently ended first).
     * Ordinary weekends are never rows — only vacation blocks.
     */
    fun listRows(
        periods: List<HolidayPeriod>,
        today: LocalDate,
    ): List<HolidayListRow> {
        return periods.map { p ->
            val ongoing = today >= p.start && today <= p.end
            val past = p.end < today
            val days = DateCalculator.daySpan(today, p.start)
            HolidayListRow(
                period = p,
                ongoing = ongoing,
                past = past,
                daysUntilStart = days,
                daysPastEnd = if (past) DateCalculator.daySpan(p.end, today) else 0,
            )
        }.sortedWith(
            compareBy<HolidayListRow> {
                when {
                    it.ongoing -> 0
                    it.past -> 2
                    else -> 1
                }
            }.thenComparator { a, b ->
                if (a.past) b.period.start.compareTo(a.period.start)
                else a.period.start.compareTo(b.period.start)
            },
        )
    }
}

data class HolidayListRow(
    val period: HolidayPeriod,
    val ongoing: Boolean,
    val past: Boolean,
    val daysUntilStart: Int,
    /** Days since vacation end; 0 unless [past]. */
    val daysPastEnd: Int = 0,
) {
    val vacationDays: Int
        get() = DateCalculator.daySpan(period.start, period.end) + 1
}

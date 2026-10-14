package com.numberourdays.app.data

import com.numberourdays.app.domain.DateCalculator
import kotlinx.datetime.Clock
import kotlinx.datetime.DatePeriod
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.plus
import kotlinx.datetime.todayIn

/** First-run demo cards (weekend + new year). Shared so iOS seeds the same samples. */
object DemoEvents {
    fun create(now: Long = Clock.System.now().toEpochMilliseconds()): List<DayEvent> {
        val today = Clock.System.todayIn(TimeZone.currentSystemDefault())
        val sat = sameWeekSaturday(today).let {
            if (it < today) it.plus(DatePeriod(days = 7)) else it
        }
        val newYear = LocalDate(
            if (today.monthNumber >= 12) today.year + 1 else today.year,
            1,
            1,
        ).let { if (it < today) it.plus(DatePeriod(years = 1)) else it }

        return listOf(
            DayEvent(
                id = "demo_weekend",
                title = "周末",
                date = DateCalculator.format(sat),
                note = "每周重复倒数演示",
                starred = true,
                starRank = 1,
                repeatUnit = RepeatUnit.WEEK,
                repeatInterval = 1,
                createdAt = now,
                updatedAt = now,
            ),
            DayEvent(
                id = "demo_newyear",
                title = "新年",
                date = DateCalculator.format(newYear),
                note = "未来倒数演示",
                starred = true,
                starRank = 2,
                createdAt = now,
                updatedAt = now,
            ),
        )
    }

    /** Saturday of the ISO week containing [from] (Mon..Sun), matching java.time `with(SATURDAY)`. */
    private fun sameWeekSaturday(from: LocalDate): LocalDate {
        // kotlinx DayOfWeek is MONDAY-first; ISO day number is ordinal+1.
        val offset = DayOfWeek.SATURDAY.ordinal - from.dayOfWeek.ordinal
        return from.plus(DatePeriod(days = offset))
    }
}

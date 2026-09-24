package com.numberourdays.app.data

import java.time.LocalDate

/** One State Council vacation block (放假调休区间), inclusive. */
data class HolidayPeriod(
    val name: String,
    val start: LocalDate,
    val end: LocalDate,
)

enum class HolidayStatus { UPCOMING, ONGOING }

data class HolidayCountdown(
    val period: HolidayPeriod,
    val status: HolidayStatus,
    /** Calendar days until [HolidayPeriod.start]; 0 when ongoing or today is start. */
    val daysUntilStart: Int,
    val label: String,
)

/**
 * Bundled PRC legal-holiday vacation ranges.
 * Source: 国务院办公厅关于2026年部分节假日安排的通知（国办发明电〔2025〕7号）.
 * Makeup workdays are not vacation and are not listed here.
 */
object LegalHolidays {
    fun periods(): List<HolidayPeriod> = Y2026

    private val Y2026: List<HolidayPeriod> = listOf(
        HolidayPeriod("元旦", LocalDate.of(2026, 1, 1), LocalDate.of(2026, 1, 3)),
        HolidayPeriod("春节", LocalDate.of(2026, 2, 15), LocalDate.of(2026, 2, 23)),
        HolidayPeriod("清明节", LocalDate.of(2026, 4, 4), LocalDate.of(2026, 4, 6)),
        HolidayPeriod("劳动节", LocalDate.of(2026, 5, 1), LocalDate.of(2026, 5, 5)),
        HolidayPeriod("端午节", LocalDate.of(2026, 6, 19), LocalDate.of(2026, 6, 21)),
        HolidayPeriod("中秋节", LocalDate.of(2026, 9, 25), LocalDate.of(2026, 9, 27)),
        HolidayPeriod("国庆节", LocalDate.of(2026, 10, 1), LocalDate.of(2026, 10, 7)),
    )
}

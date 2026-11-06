package com.numberourdays.app.data

import kotlinx.datetime.LocalDate

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
 * Sources:
 * - 国务院办公厅关于2026年部分节假日安排的通知（国办发明电〔2025〕7号）
 * - 国务院办公厅关于2027年部分节假日安排的通知（国办发明电〔2026〕10号，2026-11-05）
 *
 * Makeup workdays (调休上班日) are not vacation and are not listed here.
 */
object LegalHolidays {
    fun periods(): List<HolidayPeriod> = Y2026 + Y2027

    private val Y2026: List<HolidayPeriod> = listOf(
        HolidayPeriod("元旦", LocalDate(2026, 1, 1), LocalDate(2026, 1, 3)),
        HolidayPeriod("春节", LocalDate(2026, 2, 15), LocalDate(2026, 2, 23)),
        HolidayPeriod("清明节", LocalDate(2026, 4, 4), LocalDate(2026, 4, 6)),
        HolidayPeriod("劳动节", LocalDate(2026, 5, 1), LocalDate(2026, 5, 5)),
        HolidayPeriod("端午节", LocalDate(2026, 6, 19), LocalDate(2026, 6, 21)),
        HolidayPeriod("中秋节", LocalDate(2026, 9, 25), LocalDate(2026, 9, 27)),
        HolidayPeriod("国庆节", LocalDate(2026, 10, 1), LocalDate(2026, 10, 7)),
    )

    /**
     * 2027 特点：春节 9 天（2/5 除夕 — 2/13 初八）；端午、中秋各放 1 天不调休。
     * 调休上班日 1/31、2/14、5/8、9/26、10/9 不是假期，不列入。
     */
    private val Y2027: List<HolidayPeriod> = listOf(
        HolidayPeriod("元旦", LocalDate(2027, 1, 1), LocalDate(2027, 1, 3)),
        HolidayPeriod("春节", LocalDate(2027, 2, 5), LocalDate(2027, 2, 13)),
        HolidayPeriod("清明节", LocalDate(2027, 4, 3), LocalDate(2027, 4, 5)),
        HolidayPeriod("劳动节", LocalDate(2027, 5, 1), LocalDate(2027, 5, 5)),
        HolidayPeriod("端午节", LocalDate(2027, 6, 9), LocalDate(2027, 6, 9)),
        HolidayPeriod("中秋节", LocalDate(2027, 9, 15), LocalDate(2027, 9, 15)),
        HolidayPeriod("国庆节", LocalDate(2027, 10, 1), LocalDate(2027, 10, 7)),
    )
}

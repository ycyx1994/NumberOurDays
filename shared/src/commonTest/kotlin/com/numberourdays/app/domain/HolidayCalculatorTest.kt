package com.numberourdays.app.domain

import com.numberourdays.app.data.HolidayStatus
import com.numberourdays.app.data.LegalHolidays
import kotlinx.datetime.LocalDate
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

class HolidayCalculatorTest {

    @Test
    fun nextHoliday_beforeMidAutumn_countsCalendarDaysToStart() {
        val r = HolidayCalculator.nextOrOngoing(LegalHolidays.periods(), LocalDate(2026, 9, 24))
        assertNotNull(r)
        assertEquals("中秋节", r!!.period.name)
        assertEquals(HolidayStatus.UPCOMING, r.status)
        assertEquals(1, r.daysUntilStart)
        assertEquals("还有 1 天", r.label)
    }

    @Test
    fun startDayOnWeekend_stillTargeted() {
        val r = HolidayCalculator.nextOrOngoing(LegalHolidays.periods(), LocalDate(2026, 4, 3))
        assertNotNull(r)
        assertEquals("清明节", r!!.period.name)
        assertEquals(1, r.daysUntilStart)
    }

    @Test
    fun ordinaryWeekend_isNotAHoliday() {
        val r = HolidayCalculator.nextOrOngoing(LegalHolidays.periods(), LocalDate(2026, 3, 14))
        assertNotNull(r)
        assertEquals("清明节", r!!.period.name)
        assertEquals(HolidayStatus.UPCOMING, r.status)
        assertEquals(21, r.daysUntilStart)
    }

    @Test
    fun insideVacation_reportsOngoing() {
        val r = HolidayCalculator.nextOrOngoing(LegalHolidays.periods(), LocalDate(2026, 10, 3))
        assertNotNull(r)
        assertEquals("国庆节", r!!.period.name)
        assertEquals(HolidayStatus.ONGOING, r.status)
        assertEquals(0, r.daysUntilStart)
        assertEquals("假期中", r.label)
    }

    @Test
    fun after2026Bundled_rollsOverTo2027NewYear() {
        val r = HolidayCalculator.nextOrOngoing(LegalHolidays.periods(), LocalDate(2026, 10, 8))
        assertNotNull(r)
        assertEquals("元旦", r!!.period.name)
        assertEquals(LocalDate(2027, 1, 1), r.period.start)
    }

    @Test
    fun afterAllBundledPeriods_returnsNull() {
        val r = HolidayCalculator.nextOrOngoing(LegalHolidays.periods(), LocalDate(2027, 10, 8))
        assertNull(r)
    }

    @Test
    fun lastDayOfVacation_stillOngoing() {
        val r = HolidayCalculator.nextOrOngoing(LegalHolidays.periods(), LocalDate(2026, 9, 27))
        assertNotNull(r)
        assertEquals(HolidayStatus.ONGOING, r!!.status)
    }

    @Test
    fun springFestival_nineDayBlock() {
        val spring = LegalHolidays.periods().first { it.name == "春节" && it.start.year == 2026 }
        assertEquals(LocalDate(2026, 2, 15), spring.start)
        assertEquals(LocalDate(2026, 2, 23), spring.end)
    }

    @Test
    fun springFestival_2027_feb5ToFeb13() {
        val spring = LegalHolidays.periods().first { it.name == "春节" && it.start.year == 2027 }
        assertEquals(LocalDate(2027, 2, 5), spring.start)
        assertEquals(LocalDate(2027, 2, 13), spring.end)
    }

    @Test
    fun periods_covers2027() {
        val y2027 = LegalHolidays.periods().filter { it.start.year == 2027 }
        assertEquals(7, y2027.size)
        assertEquals(LocalDate(2027, 1, 1), y2027.first().start)
        assertEquals(LocalDate(2027, 10, 7), y2027.last().end)
    }

    @Test
    fun singleDayHolidays_2027_duanwuAndMidAutumn() {
        for (name in listOf("端午节", "中秋节")) {
            val p = LegalHolidays.periods().first { it.name == name && it.start.year == 2027 }
            assertEquals(p.start, p.end, "$name 2027 should be a single-day holiday")
        }
    }

    @Test
    fun listRows_upcomingNearestFirst_pastGrayedLast() {
        val today = LocalDate(2026, 9, 24)
        val rows = HolidayCalculator.listRows(LegalHolidays.periods(), today)
        assertEquals(14, rows.size)
        assertEquals("中秋节", rows[0].period.name)
        assertEquals(LocalDate(2026, 9, 25), rows[0].period.start)
        assertEquals("国庆节", rows[1].period.name)
        assertEquals(LocalDate(2026, 10, 1), rows[1].period.start)
        // 2026 未完的两段之后接 2027 全部七段按升序。
        assertEquals("元旦", rows[2].period.name)
        assertEquals(LocalDate(2027, 1, 1), rows[2].period.start)
        assertEquals(LocalDate(2027, 10, 1), rows[8].period.start)
        // 已过段沉底，最近结束的在最前。
        assertEquals("端午节", rows[9].period.name)
        assertEquals(LocalDate(2026, 6, 19), rows[9].period.start)
        assertEquals("元旦", rows.last().period.name)
        assertEquals(LocalDate(2026, 1, 1), rows.last().period.start)
        assertTrue(rows.take(9).none { it.past })
        assertTrue(rows.drop(9).all { it.past })
    }

    @Test
    fun listRows_ongoingIsFirst() {
        val today = LocalDate(2026, 10, 2)
        val rows = HolidayCalculator.listRows(LegalHolidays.periods(), today)
        assertEquals("国庆节", rows.first().period.name)
        assertTrue(rows.first().ongoing)
    }

    @Test
    fun listRows_vacationDays_countsInclusive() {
        val spring = LegalHolidays.periods().first { it.name == "春节" && it.start.year == 2026 }
        val row = HolidayCalculator.listRows(listOf(spring), LocalDate(2026, 1, 1)).first()
        assertEquals(9, row.vacationDays)
    }

    @Test
    fun listRows_past_showsDaysPastEnd() {
        val today = LocalDate(2026, 9, 24)
        val rows = HolidayCalculator.listRows(LegalHolidays.periods(), today)
        val duanwu = rows.first { it.period.name == "端午节" && it.period.start.year == 2026 }
        assertTrue(duanwu.past)
        assertEquals(95, duanwu.daysPastEnd)
    }
}

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
    fun afterAllBundledPeriods_returnsNull() {
        val r = HolidayCalculator.nextOrOngoing(LegalHolidays.periods(), LocalDate(2026, 10, 8))
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
        val spring = LegalHolidays.periods().first { it.name == "春节" }
        assertEquals(LocalDate(2026, 2, 15), spring.start)
        assertEquals(LocalDate(2026, 2, 23), spring.end)
    }

    @Test
    fun listRows_upcomingNearestFirst_pastGrayedLast() {
        val today = LocalDate(2026, 9, 24)
        val rows = HolidayCalculator.listRows(LegalHolidays.periods(), today)
        assertEquals(7, rows.size)
        assertEquals("中秋节", rows[0].period.name)
        assertEquals("国庆节", rows[1].period.name)
        assertEquals("端午节", rows[2].period.name)
        assertEquals("元旦", rows.last().period.name)
        assertTrue(rows[0].past.not() && rows[1].past.not())
        assertTrue(rows.drop(2).all { it.past })
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
        val spring = LegalHolidays.periods().first { it.name == "春节" }
        val row = HolidayCalculator.listRows(listOf(spring), LocalDate(2026, 1, 1)).first()
        assertEquals(9, row.vacationDays)
    }

    @Test
    fun listRows_past_showsDaysPastEnd() {
        val today = LocalDate(2026, 9, 24)
        val rows = HolidayCalculator.listRows(LegalHolidays.periods(), today)
        val duanwu = rows.first { it.period.name == "端午节" }
        assertTrue(duanwu.past)
        assertEquals(95, duanwu.daysPastEnd)
    }
}

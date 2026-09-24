package com.numberourdays.app.domain

import com.numberourdays.app.data.HolidayPeriod
import com.numberourdays.app.data.HolidayStatus
import com.numberourdays.app.data.LegalHolidays
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Test
import java.time.LocalDate

class HolidayCalculatorTest {

    @Test
    fun nextHoliday_beforeMidAutumn_countsCalendarDaysToStart() {
        // 2026-09-24 → 中秋节 2026-09-25
        val r = HolidayCalculator.nextOrOngoing(LegalHolidays.periods(), LocalDate.of(2026, 9, 24))
        assertNotNull(r)
        assertEquals("中秋节", r!!.period.name)
        assertEquals(HolidayStatus.UPCOMING, r.status)
        assertEquals(1, r.daysUntilStart)
        assertEquals("还有 1 天", r.label)
    }

    @Test
    fun startDayOnWeekend_stillTargeted() {
        // 清明节 starts Sat 2026-04-04 — weekend start is a valid target
        val r = HolidayCalculator.nextOrOngoing(LegalHolidays.periods(), LocalDate.of(2026, 4, 3))
        assertNotNull(r)
        assertEquals("清明节", r!!.period.name)
        assertEquals(1, r.daysUntilStart)
    }

    @Test
    fun ordinaryWeekend_isNotAHoliday() {
        // 2026-03-14 is a Saturday with no legal vacation — skip it, target 清明节
        val r = HolidayCalculator.nextOrOngoing(LegalHolidays.periods(), LocalDate.of(2026, 3, 14))
        assertNotNull(r)
        assertEquals("清明节", r!!.period.name)
        assertEquals(HolidayStatus.UPCOMING, r.status)
        assertEquals(21, r.daysUntilStart)
    }

    @Test
    fun insideVacation_reportsOngoing() {
        val r = HolidayCalculator.nextOrOngoing(LegalHolidays.periods(), LocalDate.of(2026, 10, 3))
        assertNotNull(r)
        assertEquals("国庆节", r!!.period.name)
        assertEquals(HolidayStatus.ONGOING, r.status)
        assertEquals(0, r.daysUntilStart)
        assertEquals("假期中", r.label)
    }

    @Test
    fun afterAllBundledPeriods_returnsNull() {
        val r = HolidayCalculator.nextOrOngoing(LegalHolidays.periods(), LocalDate.of(2026, 10, 8))
        assertNull(r)
    }

    @Test
    fun lastDayOfVacation_stillOngoing() {
        val r = HolidayCalculator.nextOrOngoing(LegalHolidays.periods(), LocalDate.of(2026, 9, 27))
        assertNotNull(r)
        assertEquals(HolidayStatus.ONGOING, r!!.status)
    }

    @Test
    fun springFestival_nineDayBlock() {
        val spring = LegalHolidays.periods().first { it.name == "春节" }
        assertEquals(LocalDate.of(2026, 2, 15), spring.start)
        assertEquals(LocalDate.of(2026, 2, 23), spring.end)
    }
}

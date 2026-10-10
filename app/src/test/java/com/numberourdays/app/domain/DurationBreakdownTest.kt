package com.numberourdays.app.domain

import org.junit.Assert.assertEquals
import org.junit.Test
import java.time.LocalDate

class DurationBreakdownTest {

    @Test
    fun monthEnd_toSameDayNextMonth_countsOneMonth() {
        val result = DateCalculator.convertDuration(
            LocalDate.of(2026, 1, 31),
            LocalDate.of(2026, 2, 28),
        )

        assertEquals(1, result.monthsAfterYears)
        assertEquals(1, result.months)
        assertEquals(0, result.daysAfterMonths)
    }

    @Test
    fun monthEnd_toSameDayInMarch_countsTwoMonths() {
        val result = DateCalculator.convertDuration(
            LocalDate.of(2026, 1, 31),
            LocalDate.of(2026, 3, 31),
        )

        assertEquals(2, result.monthsAfterYears)
        assertEquals(2, result.months)
        assertEquals(0, result.daysAfterMonths)
        assertEquals(59, result.days)
    }

    @Test
    fun leapDay_anniversary_countsOneYear() {
        val result = DateCalculator.convertDuration(
            LocalDate.of(2024, 2, 29),
            LocalDate.of(2025, 2, 28),
        )

        assertEquals(1, result.years)
        assertEquals(0, result.monthsAfterYears)
        assertEquals(12, result.months)
        assertEquals(0, result.daysAfterMonths)
    }

    @Test
    fun monthEnd_afterYearPlusMonths_keepsAnchorMilestones() {
        val result = DateCalculator.convertDuration(
            LocalDate.of(2026, 1, 31),
            LocalDate.of(2027, 3, 31),
        )

        assertEquals(1, result.years)
        assertEquals(2, result.monthsAfterYears)
        assertEquals(14, result.months)
        assertEquals(0, result.daysAfterMonths)
    }
}

package com.numberourdays.app.domain

import com.numberourdays.app.data.RepeatUnit
import org.junit.Assert.assertEquals
import org.junit.Test
import java.time.LocalDate

class ResolveNextDateTest {

    @Test
    fun monthEnd_anchorJan31_stepsKeepMonthEnd() {
        val anchor = "2026-01-31"
        // On/after ref: when ref is Feb 1, next clamps to 2/28
        assertEquals(
            LocalDate.of(2026, 2, 28),
            DateCalculator.resolveNextDate(anchor, RepeatUnit.MONTH, 1, LocalDate.of(2026, 2, 1)),
        )
        // After Feb clamp, March must be 3/31 not 3/28
        assertEquals(
            LocalDate.of(2026, 3, 31),
            DateCalculator.resolveNextDate(anchor, RepeatUnit.MONTH, 1, LocalDate.of(2026, 3, 1)),
        )
        assertEquals(
            LocalDate.of(2026, 4, 30),
            DateCalculator.resolveNextDate(anchor, RepeatUnit.MONTH, 1, LocalDate.of(2026, 4, 1)),
        )
        // Anchor itself is the first occurrence when ref is before/on it
        assertEquals(
            LocalDate.of(2026, 1, 31),
            DateCalculator.resolveNextDate(anchor, RepeatUnit.MONTH, 1, LocalDate.of(2026, 1, 1)),
        )
    }

    @Test
    fun monthEnd_leapFeb_fromJan31() {
        assertEquals(
            LocalDate.of(2028, 2, 29),
            DateCalculator.resolveNextDate("2028-01-31", RepeatUnit.MONTH, 1, LocalDate.of(2028, 2, 1)),
        )
        assertEquals(
            LocalDate.of(2028, 3, 31),
            DateCalculator.resolveNextDate("2028-01-31", RepeatUnit.MONTH, 1, LocalDate.of(2028, 3, 1)),
        )
    }

    @Test
    fun yearEnd_anchorJan31_staysJan31() {
        assertEquals(
            LocalDate.of(2027, 1, 31),
            DateCalculator.resolveNextDate("2026-01-31", RepeatUnit.YEAR, 1, LocalDate.of(2026, 2, 1)),
        )
    }

    @Test
    fun feb29_annual_nonLeapYear_clampsTo228() {
        assertEquals(
            LocalDate.of(2027, 2, 28),
            DateCalculator.resolveNextDate("2024-02-29", RepeatUnit.YEAR, 1, LocalDate.of(2026, 3, 1)),
        )
    }

    @Test
    fun feb29_annual_leapYear_keeps229() {
        assertEquals(
            LocalDate.of(2028, 2, 29),
            DateCalculator.resolveNextDate("2024-02-29", RepeatUnit.YEAR, 1, LocalDate.of(2027, 3, 1)),
        )
    }

    @Test
    fun plusMonthsFromAnchor_matchesSpec() {
        val a = LocalDate.of(2026, 1, 31)
        assertEquals(LocalDate.of(2026, 2, 28), DateCalculator.plusMonthsFromAnchor(a, 1))
        assertEquals(LocalDate.of(2026, 3, 31), DateCalculator.plusMonthsFromAnchor(a, 2))
        assertEquals(LocalDate.of(2026, 4, 30), DateCalculator.plusMonthsFromAnchor(a, 3))
    }

    @Test
    fun week_and_day_stillWork() {
        assertEquals(
            LocalDate.of(2026, 9, 26),
            DateCalculator.resolveNextDate("2026-09-26", RepeatUnit.WEEK, 1, LocalDate.of(2026, 9, 26)),
        )
        assertEquals(
            LocalDate.of(2026, 10, 3),
            DateCalculator.resolveNextDate("2026-09-26", RepeatUnit.WEEK, 1, LocalDate.of(2026, 9, 27)),
        )
        assertEquals(
            LocalDate.of(2026, 9, 26),
            DateCalculator.resolveNextDate("2026-09-26", RepeatUnit.NONE, 1, LocalDate.of(2026, 10, 1)),
        )
    }

    @Test
    fun mar31_every2Months_keepsDayWhenPossible() {
        val a = LocalDate.of(2026, 3, 31)
        assertEquals(LocalDate.of(2026, 5, 31), DateCalculator.plusMonthsFromAnchor(a, 2))
        assertEquals(LocalDate.of(2026, 7, 31), DateCalculator.plusMonthsFromAnchor(a, 4))
        assertEquals(LocalDate.of(2026, 9, 30), DateCalculator.plusMonthsFromAnchor(a, 6))
        assertEquals(LocalDate.of(2026, 11, 30), DateCalculator.plusMonthsFromAnchor(a, 8))
    }

    @Test
    fun mar31_every2Months_resolveNext() {
        val anchor = "2026-03-31"
        assertEquals(
            LocalDate.of(2026, 5, 31),
            DateCalculator.resolveNextDate(anchor, RepeatUnit.MONTH, 2, LocalDate.of(2026, 4, 1)),
        )
        assertEquals(
            LocalDate.of(2026, 7, 31),
            DateCalculator.resolveNextDate(anchor, RepeatUnit.MONTH, 2, LocalDate.of(2026, 6, 1)),
        )
        assertEquals(
            LocalDate.of(2026, 9, 30),
            DateCalculator.resolveNextDate(anchor, RepeatUnit.MONTH, 2, LocalDate.of(2026, 8, 1)),
        )
    }

    @Test
    fun interval3_fromDec31() {
        val a = LocalDate.of(2026, 12, 31)
        assertEquals(LocalDate.of(2027, 3, 31), DateCalculator.plusMonthsFromAnchor(a, 3))
        assertEquals(LocalDate.of(2027, 6, 30), DateCalculator.plusMonthsFromAnchor(a, 6))
    }
}

package com.numberourdays.app.domain

import org.junit.Assert.assertEquals
import org.junit.Test

class DragReorderTest {

    @Test
    fun dragTargetIndex_movesDownByRoundedRows() {
        // 1.4 rows down → +1
        assertEquals(1, DateCalculator.dragTargetIndex(0, 3, deltaY = 140f, rowHeightPx = 100f))
        // 1.6 rows down → +2
        assertEquals(2, DateCalculator.dragTargetIndex(0, 3, deltaY = 160f, rowHeightPx = 100f))
    }

    @Test
    fun dragTargetIndex_movesUpByRoundedRows() {
        assertEquals(0, DateCalculator.dragTargetIndex(2, 3, deltaY = -160f, rowHeightPx = 100f))
        assertEquals(1, DateCalculator.dragTargetIndex(2, 3, deltaY = -140f, rowHeightPx = 100f))
    }

    @Test
    fun dragTargetIndex_smallDeltaStaysPut() {
        assertEquals(1, DateCalculator.dragTargetIndex(1, 3, deltaY = 40f, rowHeightPx = 100f))
        assertEquals(1, DateCalculator.dragTargetIndex(1, 3, deltaY = -40f, rowHeightPx = 100f))
    }

    @Test
    fun dragTargetIndex_clampsToBounds() {
        assertEquals(3, DateCalculator.dragTargetIndex(1, 3, deltaY = 500f, rowHeightPx = 100f))
        assertEquals(0, DateCalculator.dragTargetIndex(2, 3, deltaY = -500f, rowHeightPx = 100f))
    }

    @Test
    fun dragTargetIndex_invalidInputsReturnFrom() {
        assertEquals(-1, DateCalculator.dragTargetIndex(-1, 3, deltaY = 200f, rowHeightPx = 100f))
        assertEquals(0, DateCalculator.dragTargetIndex(0, -1, deltaY = 200f, rowHeightPx = 100f))
        assertEquals(5, DateCalculator.dragTargetIndex(5, 3, deltaY = 200f, rowHeightPx = 100f))
        assertEquals(0, DateCalculator.dragTargetIndex(0, 3, deltaY = 200f, rowHeightPx = 0f))
    }
}

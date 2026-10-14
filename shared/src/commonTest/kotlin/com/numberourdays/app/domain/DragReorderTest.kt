package com.numberourdays.app.domain

import kotlin.test.Test
import kotlin.test.assertEquals

class DragReorderTest {

    @Test
    fun dragTargetIndex_movesDownByRoundedRows() {
        assertEquals(1, DateCalculator.dragTargetIndex(0, 3, deltaY = 140f, rowHeightPx = 100f))
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

package com.numberourdays.app.data.db

import com.numberourdays.app.data.RepeatUnit
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class DayEventMappersTest {

    @Test
    fun entity_domain_roundTrip() {
        val entity = DayEventEntity(
            id = "e1",
            title = "在一起",
            date = "2020-01-15",
            note = "备注",
            starred = true,
            starRank = 2,
            repeatUnit = "year",
            repeatInterval = 1,
            remindEnabled = true,
            createdAt = 1L,
            updatedAt = 2L,
        )
        val d = entity.toDomain()
        assertEquals("e1", d.id)
        assertEquals(RepeatUnit.YEAR, d.repeatUnit)
        assertEquals(2, d.starRank)
        val back = d.toEntity()
        assertEquals(entity, back)
    }

    @Test
    fun null_note_and_starRank_survive() {
        val entity = DayEventEntity(
            id = "e2",
            title = "考试",
            date = "2027-03-01",
            note = null,
            starred = false,
            starRank = null,
            repeatUnit = "none",
            repeatInterval = 1,
            remindEnabled = false,
            createdAt = 1L,
            updatedAt = 1L,
        )
        val d = entity.toDomain()
        assertNull(d.note)
        assertNull(d.starRank)
        assertEquals(entity, d.toEntity())
    }
}

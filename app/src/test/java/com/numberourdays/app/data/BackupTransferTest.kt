package com.numberourdays.app.data

import java.time.LocalDate
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class BackupTransferTest {

    private fun sampleEvent(
        id: String = "e1",
        title: String = "在一起",
        date: String = "2020-01-15",
        note: String? = "备注",
        starred: Boolean = true,
        starRank: Int? = 2,
        unit: RepeatUnit = RepeatUnit.YEAR,
        interval: Int = 1,
        remind: Boolean = true,
        updatedAt: Long = 1000L,
    ) = DayEvent(
        id = id,
        title = title,
        date = date,
        note = note,
        starred = starred,
        starRank = starRank,
        repeatUnit = unit,
        repeatInterval = interval,
        remindEnabled = remind,
        createdAt = 1L,
        updatedAt = updatedAt,
    )

    @Test
    fun defaultBackupFileName_usesBasicIsoDate() {
        val name = BackupTransfer.defaultBackupFileName(LocalDate.of(2026, 9, 21))
        assertEquals("numberourdays-backup-20260921.json", name)
    }

    @Test
    fun serialize_then_parseBackup_roundTrip() {
        val original = listOf(
            sampleEvent(),
            sampleEvent(
                id = "e2",
                title = "考试",
                date = "2027-03-01",
                note = null,
                starred = false,
                starRank = null,
                unit = RepeatUnit.NONE,
                interval = 1,
                remind = false,
                updatedAt = 2000L,
            ),
        )
        val json = DayEventCodec.serialize(original)
        val (parsed, note) = DayEventCodec.parseBackup(json)
        assertNull(note)
        assertEquals(2, parsed.size)
        val a = parsed.first { it.id == "e1" }
        assertEquals("在一起", a.title)
        assertEquals("2020-01-15", a.date)
        assertEquals("备注", a.note)
        assertTrue(a.starred)
        assertEquals(2, a.starRank)
        assertEquals(RepeatUnit.YEAR, a.repeatUnit)
        assertEquals(1, a.repeatInterval)
        assertTrue(a.remindEnabled)
        assertEquals(1000L, a.updatedAt)
        val b = parsed.first { it.id == "e2" }
        assertNull(b.note)
        assertEquals(RepeatUnit.NONE, b.repeatUnit)
        assertEquals(2000L, b.updatedAt)
    }

    @Test
    fun parseBackup_skipsInvalidEntries() {
        val json = """
            {"app":"NumberOurDays","schemaVersion":1,"events":[
              {"id":"ok","title":"有效","date":"2024-06-01"},
              {"id":"bad","title":"","date":"2024-06-01"},
              {"id":"bad2","title":"无日期","date":"not-a-date"}
            ]}
        """.trimIndent()
        val (events, note) = DayEventCodec.parseBackup(json)
        assertEquals(1, events.size)
        assertEquals("ok", events[0].id)
        assertEquals("跳过 2 条无效", note)
    }

    @Test
    fun parseBackup_rejectsInvalidJson() {
        val (events, note) = DayEventCodec.parseBackup("not-json")
        assertTrue(events.isEmpty())
        assertEquals("不是合法 JSON", note)
    }

    @Test
    fun merge_prefersNewerUpdatedAtForSameId() {
        val local = listOf(sampleEvent(id = "a", title = "旧", updatedAt = 100L))
        val incoming = listOf(sampleEvent(id = "a", title = "新", updatedAt = 200L))
        val merged = DayEventCodec.merge(local, incoming)
        assertEquals(1, merged.size)
        assertEquals("新", merged[0].title)

        val olderIncoming = listOf(sampleEvent(id = "a", title="更旧", updatedAt=50L))
        val kept = DayEventCodec.merge(local, olderIncoming)
        assertEquals("旧", kept[0].title)
    }

    @Test
    fun merge_keepsLocalOnlyAndAppendsIncoming() {
        val local = listOf(sampleEvent(id = "local", title = "本地独有", updatedAt = 1L))
        val incoming = listOf(sampleEvent(id = "incoming", title = "导入新增", updatedAt = 1L))
        val merged = DayEventCodec.merge(local, incoming)
        assertEquals(2, merged.size)
        assertNotNull(merged.find { it.id == "local" })
        assertNotNull(merged.find { it.id == "incoming" })
    }

    @Test
    fun merge_sameNaturalKey_differentId_doesNotDuplicate() {
        val local = listOf(
            sampleEvent(id = "demo-weekend", title = "周末", date = "2026-09-26", updatedAt = 10L),
        )
        val incoming = listOf(
            sampleEvent(
                id = "backup-weekend",
                title = "周末",
                date = "2026-09-26",
                updatedAt = 20L,
                note = "来自备份",
            ),
        )
        val merged = DayEventCodec.merge(local, incoming)
        assertEquals(1, merged.size)
        assertEquals("demo-weekend", merged[0].id)
        assertEquals("来自备份", merged[0].note)
        assertEquals(20L, merged[0].updatedAt)
    }

    @Test
    fun merge_importTwice_staysSameSize() {
        val local = listOf(sampleEvent(id = "a", title = "在一起", updatedAt = 5L))
        val backup = listOf(sampleEvent(id = "b", title = "在一起", updatedAt = 5L))
        val once = DayEventCodec.merge(local, backup)
        val twice = DayEventCodec.merge(once, backup)
        assertEquals(1, once.size)
        assertEquals(1, twice.size)
    }

    @Test
    fun remindTime_defaultsWhenMissing() {
        val json = """{"id":"x","title":"t","date":"2026-01-01","remindEnabled":true}"""
        val (events, _) = DayEventCodec.parseBackup("""{"events":[$json]}""")
        assertEquals(1, events.size)
        assertEquals(9, events[0].remindHour)
        assertEquals(0, events[0].remindMinute)
    }

    @Test
    fun remindTime_roundTrip() {
        val e = sampleEvent(id = "r1", title = "提醒", date = "2026-09-21")
            .copy(remindEnabled = true, remindHour = 20, remindMinute = 0)
        val (parsed, _) = DayEventCodec.parseBackup(DayEventCodec.serialize(listOf(e)))
        assertEquals(20, parsed[0].remindHour)
        assertEquals(0, parsed[0].remindMinute)
    }
}

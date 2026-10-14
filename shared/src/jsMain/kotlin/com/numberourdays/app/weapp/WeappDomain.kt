package com.numberourdays.app.weapp

import com.numberourdays.app.data.DayEvent
import com.numberourdays.app.data.DayEventCodec
import com.numberourdays.app.data.RepeatUnit
import com.numberourdays.app.domain.DateCalculator
import kotlin.js.ExperimentalJsExport
import kotlin.js.JsExport
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

/**
 * Stable JS surface for the WeChat mini program host.
 * All payloads are JSON strings so the host does not depend on Kotlin class shapes.
 */
@OptIn(ExperimentalJsExport::class)
@JsExport
object WeappDomain {
    private val json = Json { ignoreUnknownKeys = true; encodeDefaults = true; explicitNulls = true }

    fun today(): String = DateCalculator.format(DateCalculator.today())

    fun isValidDate(text: String?): Boolean = DateCalculator.isValidDate(text)

    fun parseDate(text: String): String {
        val d = DateCalculator.parse(text)
        return if (d == null) "" else DateCalculator.format(d)
    }

    fun createId(): String = createEventId()

    fun validateForm(title: String, date: String, unit: String, interval: Int): String {
        val u = RepeatUnit.from(unit)
        return DateCalculator.validateForm(title, date, u, interval) ?: ""
    }

    /** DayEvent JSON object → DayEventDisplay JSON object. */
    fun computeDisplayJson(eventJson: String, todayIso: String = today()): String {
        val e = DayEventCodec.migrate(eventJson) ?: return ""
        val ref = DateCalculator.parse(todayIso) ?: DateCalculator.today()
        val d = DateCalculator.computeDisplay(e, ref)
        return encodeDisplay(d)
    }

    /** JSON array of DayEvent → sorted JSON array of DayEventDisplay. */
    fun sortDisplaysJson(eventsJson: String, todayIso: String = today()): String {
        val events = DayEventCodec.parseEventsArray(if (eventsJson.trim().startsWith("[")) eventsJson else "[$eventsJson]")
        // also accept backup wrapper
        val list = if (events.isEmpty()) {
            val pair = DayEventCodec.parseBackup(eventsJson)
            pair.first
        } else {
            events
        }
        val ref = DateCalculator.parse(todayIso) ?: DateCalculator.today()
        val displays = DateCalculator.sortEvents(list.map { DateCalculator.computeDisplay(it, ref) })
        return "[" + displays.joinToString(",") { encodeDisplay(it) } + "]"
    }

    fun serialize(eventsJson: String): String {
        val events = DayEventCodec.parseEventsArray(if (eventsJson.trim().startsWith("[")) eventsJson else "[$eventsJson]")
        return DayEventCodec.serialize(events)
    }

    /** Returns { "events": DayEventJson[], "note": string|null } */
    fun parseBackup(text: String): String {
        val (events, note) = DayEventCodec.parseBackup(text)
        val arr = events.joinToString(",", prefix = "[", postfix = "]") { DayEventCodec.toJson(it) }
        val noteJson = if (note == null) "null" else json.encodeToString(note)
        return """{"events":$arr,"note":$noteJson}"""
    }

    fun mergeJson(localJson: String, incomingJson: String): String {
        val local = DayEventCodec.parseEventsArray(unwrap(localJson))
        val incoming = DayEventCodec.parseEventsArray(unwrap(incomingJson))
        val merged = DayEventCodec.merge(local, incoming)
        return merged.joinToString(",", prefix = "[", postfix = "]") { DayEventCodec.toJson(it) }
    }

    private fun unwrap(raw: String): String {
        val t = raw.trim()
        return if (t.startsWith("[")) t else "[$t]"
    }

    private fun encodeDisplay(d: com.numberourdays.app.data.DayEventDisplay): String {
        // Keep a small explicit object so WeChat JS stays stable.
        val next = d.nextDate?.let { "\"${it}\"" } ?: "null"
        val repeat = d.repeatLabel?.let { "\"${it.replace("\"", "\\\"")}\"" } ?: "null"
        val note = d.note?.let { "\"${it.replace("\"", "\\\"").replace("\n", "\\n")}\"" } ?: "null"
        val title = d.title.replace("\"", "\\\"")
        return """
            {"id":"${d.id}","title":"$title","date":"${d.date}","kind":"${d.kind.name}",
             "days":${d.days},"elapsedDays":${d.elapsedDays},"label":"${d.label.replace("\"", "\\\"")}",
             "nextDate":$next,"repeatLabel":$repeat,"starred":${d.starred},
             "remindEnabled":${d.remindEnabled},"remindHour":${d.remindHour},"remindMinute":${d.remindMinute},
             "note":$note}
        """.trimIndent().replace("\n", "")
    }
}

// createEventId is top-level in DayEventCodec.kt package data
private fun createEventId(): String = com.numberourdays.app.data.createEventId()

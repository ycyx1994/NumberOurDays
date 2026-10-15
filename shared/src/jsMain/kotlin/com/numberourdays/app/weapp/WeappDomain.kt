package com.numberourdays.app.weapp

import com.numberourdays.app.data.DayEvent
import com.numberourdays.app.data.DayEventCodec
import com.numberourdays.app.data.DayEventDisplay
import com.numberourdays.app.data.DurationUnit
import com.numberourdays.app.data.RepeatUnit
import com.numberourdays.app.domain.DateCalculator
import kotlin.js.ExperimentalJsExport
import kotlin.js.JsExport
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

/**
 * Stable JS surface for the WeChat mini program host.
 * All payloads are JSON strings; encoding uses kotlinx.serialization (not string templates).
 */
@OptIn(ExperimentalJsExport::class)
@JsExport
object WeappDomain {
    private val json = Json {
        ignoreUnknownKeys = true
        encodeDefaults = true
        explicitNulls = true
    }

    @Serializable
    internal data class DisplayDto(
        val id: String,
        val title: String,
        val date: String,
        val kind: String,
        val days: Int,
        val elapsedDays: Int,
        val label: String,
        val nextDate: String? = null,
        val repeatLabel: String? = null,
        val starred: Boolean = false,
        val remindEnabled: Boolean = false,
        val remindHour: Int = 8,
        val remindMinute: Int = 0,
        val note: String? = null,
    )

    @Serializable
    internal data class DurationDto(
        val value: Int,
        val unitLabel: String,
        val rest: String,
    )

    fun today(): String = DateCalculator.format(DateCalculator.today())

    fun isValidDate(text: String?): Boolean = DateCalculator.isValidDate(text)

    fun parseDate(text: String): String {
        val d = DateCalculator.parse(text)
        return if (d == null) "" else DateCalculator.format(d)
    }

    fun formatDate(iso: String): String = iso

    fun createId(): String = createEventId()

    fun validateForm(title: String, date: String, unit: String, interval: Int): String {
        val u = RepeatUnit.from(unit)
        return DateCalculator.validateForm(title, date, u, interval) ?: ""
    }

    fun computeDisplayJson(eventJson: String, todayIso: String = today()): String {
        val e = DayEventCodec.migrate(eventJson) ?: return ""
        val ref = DateCalculator.parse(todayIso) ?: DateCalculator.today()
        return json.encodeToString(DateCalculator.computeDisplay(e, ref).toDto())
    }

    fun sortDisplaysJson(eventsJson: String, todayIso: String = today()): String {
        val list = parseEventList(eventsJson)
        val ref = DateCalculator.parse(todayIso) ?: DateCalculator.today()
        val displays = DateCalculator.sortEvents(list.map { DateCalculator.computeDisplay(it, ref) })
        return json.encodeToString(displays.map { it.toDto() })
    }

    /** Pick nearest countdown/repeat for the home hero (matches Android pickNearest). */
    fun pickNearestJson(displaysJson: String): String {
        val list = json.decodeFromString<List<DisplayDto>>(displaysJson.ifEmpty { "[]" })
        // Rebuild minimal display-like ranking using days + kind string
        val candidates = list.filter { it.days > 0 && (it.kind == "REPEAT" || it.kind == "COUNTDOWN") }
        val best = candidates.minByOrNull { it.days } ?: list.firstOrNull()
        return if (best == null) "" else json.encodeToString(best)
    }

    /**
     * Next/ongoing legal holiday, or {} when none remain.
     * Shape: { name, start, end, status, daysUntilStart, label }
     */
    fun holidayCountdownJson(todayIso: String = today()): String {
        val ref = DateCalculator.parse(todayIso) ?: DateCalculator.today()
        val c = com.numberourdays.app.domain.HolidayCalculator.nextOrOngoing(
            com.numberourdays.app.data.LegalHolidays.periods(),
            ref,
        ) ?: return "{}"
        val name = c.period.name.replace("\"", "\\\"")
        val label = c.label.replace("\"", "\\\"")
        val start = DateCalculator.format(c.period.start)
        val end = DateCalculator.format(c.period.end)
        return """{"name":"$name","start":"$start","end":"$end","status":"${c.status.name}","daysUntilStart":${c.daysUntilStart},"label":"$label"}"""
    }

    /** Full legal-holiday rows for the list page. */
    fun holidayListJson(todayIso: String = today()): String {
        val ref = DateCalculator.parse(todayIso) ?: DateCalculator.today()
        val rows = com.numberourdays.app.domain.HolidayCalculator.listRows(
            com.numberourdays.app.data.LegalHolidays.periods(),
            ref,
        )
        return rows.joinToString(",", prefix = "[", postfix = "]") { row ->
            val name = row.period.name.replace("\"", "\\\"")
            val start = DateCalculator.format(row.period.start)
            val end = DateCalculator.format(row.period.end)
            """{"name":"$name","start":"$start","end":"$end","ongoing":${row.ongoing},"past":${row.past},"daysUntilStart":${row.daysUntilStart},"daysPastEnd":${row.daysPastEnd},"vacationDays":${row.vacationDays}}"""
        }
    }

    /** Remaining duration toward the detail target for the given unit (DAY/MONTH/YEAR). */
    fun targetDurationJson(eventJson: String, unit: String, todayIso: String = today()): String {
        val e = DayEventCodec.migrate(eventJson) ?: return ""
        val ref = DateCalculator.parse(todayIso) ?: DateCalculator.today()
        val display = DateCalculator.computeDisplay(e, ref)
        val target = DateCalculator.detailTargetDate(display)
        val breakdown = DateCalculator.convertDuration(ref, target)
        return json.encodeToString(breakdown.toDto(unit))
    }

    /** Elapsed since anchor; zeros for future (countdown) dates. */
    fun elapsedDurationJson(eventJson: String, unit: String, todayIso: String = today()): String {
        val e = DayEventCodec.migrate(eventJson) ?: return ""
        val ref = DateCalculator.parse(todayIso) ?: DateCalculator.today()
        val anchor = DateCalculator.parse(e.date) ?: ref
        val breakdown = DateCalculator.elapsedSince(anchor, ref)
        return json.encodeToString(breakdown.toDto(unit))
    }

    fun serialize(eventsJson: String): String {
        val events = parseEventList(eventsJson)
        return DayEventCodec.serialize(events)
    }

    fun parseBackup(text: String): String {
        val (events, note) = DayEventCodec.parseBackup(text)
        val arr = events.joinToString(",", prefix = "[", postfix = "]") { DayEventCodec.toJson(it) }
        val noteJson = if (note == null) "null" else json.encodeToString(note)
        return """{"events":$arr,"note":$noteJson}"""
    }

    fun mergeJson(localJson: String, incomingJson: String): String {
        val merged = DayEventCodec.merge(parseEventList(localJson), parseEventList(incomingJson))
        return merged.joinToString(",", prefix = "[", postfix = "]") { DayEventCodec.toJson(it) }
    }

    private fun parseEventList(raw: String): List<DayEvent> {
        val t = raw.trim()
        if (t.isEmpty()) return emptyList()
        val array = if (t.startsWith("[")) t else "[$t]"
        return DayEventCodec.parseEventsArray(array)
    }

    private fun DayEventDisplay.toDto() = DisplayDto(
        id = id,
        title = title,
        date = date,
        kind = kind.name,
        days = days,
        elapsedDays = elapsedDays,
        label = label,
        nextDate = nextDate,
        repeatLabel = repeatLabel,
        starred = starred,
        remindEnabled = remindEnabled,
        remindHour = remindHour,
        remindMinute = remindMinute,
        note = note,
    )

    private fun com.numberourdays.app.data.DurationBreakdown.toDto(unit: String): DurationDto {
        val u = when (unit.uppercase()) {
            "MONTH" -> DurationUnit.MONTH
            "YEAR" -> DurationUnit.YEAR
            else -> DurationUnit.DAY
        }
        val shown = DateCalculator.formatDuration(this, u)
        return DurationDto(shown.value, shown.unitLabel, shown.rest)
    }
}

private fun createEventId(): String = com.numberourdays.app.data.createEventId()

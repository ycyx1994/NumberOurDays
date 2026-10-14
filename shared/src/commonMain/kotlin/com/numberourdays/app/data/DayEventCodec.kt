package com.numberourdays.app.data

import com.numberourdays.app.domain.DateCalculator
import kotlinx.datetime.Clock
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.buildJsonArray
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.put

/**
 * Context-free JSON codec so backup / migration logic is unit-testable on any target.
 * Schema is frozen (`schemaVersion = 1`).
 */
object DayEventCodec {
    @OptIn(kotlinx.serialization.ExperimentalSerializationApi::class)
    private val json = Json {
        ignoreUnknownKeys = true
        encodeDefaults = true
        explicitNulls = true
        prettyPrint = true
        prettyPrintIndent = "  "
    }

    @Serializable
    internal data class DayEventDto(
        val id: String = "",
        val title: String = "",
        val date: String = "",
        val note: String? = null,
        val starred: Boolean = false,
        val starRank: Int? = null,
        val repeatUnit: String = "none",
        val repeatInterval: Int = 1,
        val remindEnabled: Boolean = false,
        // Migration default matches legacy org.json optInt("remindHour", 9)
        val remindHour: Int = 9,
        val remindMinute: Int = 0,
        // Legacy migrate filled missing timestamps with wall-clock now.
        val createdAt: Long = Clock.System.now().toEpochMilliseconds(),
        val updatedAt: Long = Clock.System.now().toEpochMilliseconds(),
    )

    private fun DayEvent.toDto() = DayEventDto(
        id = id,
        title = title,
        date = date,
        note = note,
        starred = starred,
        starRank = starRank,
        repeatUnit = repeatUnit.name.lowercase(),
        repeatInterval = repeatInterval,
        remindEnabled = remindEnabled,
        remindHour = remindHour,
        remindMinute = remindMinute,
        createdAt = createdAt,
        updatedAt = updatedAt,
    )

    private fun DayEventDto.toDomainOrNull(): DayEvent? {
        if (id.isEmpty() || date.isEmpty()) return null
        return DayEvent(
            id = id,
            title = title,
            date = date,
            note = note?.ifEmpty { null },
            starred = starred,
            starRank = starRank,
            repeatUnit = RepeatUnit.from(repeatUnit),
            repeatInterval = repeatInterval.coerceAtLeast(1),
            remindEnabled = remindEnabled,
            remindHour = remindHour.coerceIn(0, 23),
            remindMinute = remindMinute.coerceIn(0, 59),
            createdAt = createdAt,
            updatedAt = updatedAt,
        )
    }

    fun toJson(e: DayEvent): String = json.encodeToString(e.toDto())

    fun migrate(text: String?): DayEvent? {
        if (text.isNullOrBlank()) return null
        return runCatching {
            json.decodeFromString<DayEventDto>(text).toDomainOrNull()
        }.getOrNull()
    }

    fun migrate(obj: JsonObject?): DayEvent? {
        if (obj == null) return null
        return runCatching {
            json.decodeFromJsonElement(DayEventDto.serializer(), obj).toDomainOrNull()
        }.getOrNull()
    }

    /** Parse a bare JSON array of events (legacy prefs blob). */
    fun parseEventsArray(raw: String): List<DayEvent> {
        return runCatching {
            val arr = json.parseToJsonElement(raw).jsonArray
            arr.mapNotNull { migrate(it as? JsonObject) }
        }.getOrElse { emptyList() }
    }

    fun serialize(events: List<DayEvent>): String {
        val payload = buildJsonObject {
            put("app", "NumberOurDays")
            put("schemaVersion", 1)
            put("exportedAt", Clock.System.now().toEpochMilliseconds())
            put("events", buildJsonArray {
                events.forEach { add(json.parseToJsonElement(json.encodeToString(it.toDto()))) }
            })
        }
        return json.encodeToString(JsonObject.serializer(), payload)
    }

    fun parseBackup(text: String): Pair<List<DayEvent>, String?> {
        val trimmed = text.trim()
        if (trimmed.isEmpty()) return emptyList<DayEvent>() to "内容为空"
        return try {
            val data = json.parseToJsonElement(trimmed).jsonObject
            val arr = data["events"]?.jsonArray ?: return emptyList<DayEvent>() to "缺少 events 数组"
            val events = mutableListOf<DayEvent>()
            var skipped = 0
            for (element in arr) {
                val e = migrate(element as? JsonObject)
                if (e != null && e.title.isNotBlank() && DateCalculator.isValidDate(e.date)) events.add(e)
                else skipped++
            }
            val note = if (skipped > 0) "跳过 $skipped 条无效" else null
            events to note
        } catch (_: Exception) {
            emptyList<DayEvent>() to "不是合法 JSON"
        }
    }

    /**
     * Merge by id, then by natural key (title+date+repeat) so re-importing a
     * backup or overlapping demo rows does not create duplicate cards.
     * When only the natural key matches, the winning row keeps the local id.
     */
    fun merge(local: List<DayEvent>, incoming: List<DayEvent>): List<DayEvent> {
        fun naturalKey(e: DayEvent): String = listOf(
            e.title.trim(),
            e.date,
            e.repeatUnit.name,
            e.repeatInterval.toString(),
        ).joinToString("|")

        val map = LinkedHashMap<String, DayEvent>()
        val keyToId = HashMap<String, String>()
        local.forEach { l ->
            map[l.id] = l
            keyToId.putIfAbsent(naturalKey(l), l.id)
        }
        incoming.forEach { r ->
            val l = map[r.id] ?: keyToId[naturalKey(r)]?.let { map[it] }
            when {
                l == null -> {
                    map[r.id] = r
                    keyToId[naturalKey(r)] = r.id
                }
                r.updatedAt >= l.updatedAt -> {
                    val updated = r.copy(id = l.id)
                    map[l.id] = updated
                    // Content may rename the natural key; drop the stale index entry.
                    keyToId.remove(naturalKey(l))
                    keyToId[naturalKey(updated)] = l.id
                }
                else -> {
                    keyToId[naturalKey(l)] = l.id
                }
            }
        }
        return map.values.toList()
    }
}

/** Build a stable-ish local id without java.util.UUID. */
fun createEventId(now: Long = Clock.System.now().toEpochMilliseconds()): String {
    val ts = now.toString(36)
    val rand = kotlin.random.Random.nextLong(0, 1L shl 40).toString(36)
    return "e_${ts}_$rand"
}

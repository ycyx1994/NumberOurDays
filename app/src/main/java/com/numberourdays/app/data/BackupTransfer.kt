package com.numberourdays.app.data

import android.content.Context
import android.net.Uri
import com.numberourdays.app.domain.DateCalculator
import java.time.LocalDate
import java.time.format.DateTimeFormatter

/** Pure + ContentResolver helpers for real .json backup export/import. */
object BackupTransfer {

    fun defaultBackupFileName(date: LocalDate = LocalDate.now()): String {
        val ymd = date.format(DateTimeFormatter.BASIC_ISO_DATE)
        return "numberourdays-backup-$ymd.json"
    }

    fun writeText(context: Context, uri: Uri, text: String): Boolean {
        return try {
            val out = context.contentResolver.openOutputStream(uri, "wt") ?: return false
            out.use { stream ->
                stream.write(text.toByteArray(Charsets.UTF_8))
                stream.flush()
            }
            true
        } catch (_: Exception) {
            false
        }
    }

    fun readText(context: Context, uri: Uri): String? {
        return try {
            val input = context.contentResolver.openInputStream(uri) ?: return null
            input.use { stream ->
                stream.readBytes().toString(Charsets.UTF_8)
            }
        } catch (_: Exception) {
            null
        }
    }
}

/** Context-free JSON codec so backup logic is unit-testable on the JVM. */
object DayEventCodec {

    fun toJson(e: DayEvent): org.json.JSONObject = org.json.JSONObject().apply {
        put("id", e.id)
        put("title", e.title)
        put("date", e.date)
        if (e.note != null) put("note", e.note) else put("note", org.json.JSONObject.NULL)
        put("starred", e.starred)
        if (e.starRank != null) put("starRank", e.starRank) else put("starRank", org.json.JSONObject.NULL)
        put("repeatUnit", e.repeatUnit.name.lowercase())
        put("repeatInterval", e.repeatInterval)
        put("remindEnabled", e.remindEnabled)
        put("createdAt", e.createdAt)
        put("updatedAt", e.updatedAt)
    }

    fun migrate(obj: org.json.JSONObject?): DayEvent? {
        if (obj == null) return null
        val id = obj.optString("id")
        val date = obj.optString("date")
        if (id.isEmpty() || date.isEmpty()) return null
        val unit = RepeatUnit.from(obj.optString("repeatUnit", "none"))
        val interval = obj.optInt("repeatInterval", 1).coerceAtLeast(1)
        return DayEvent(
            id = id,
            title = obj.optString("title", ""),
            date = date,
            note = if (obj.isNull("note")) null else obj.optString("note").ifEmpty { null },
            starred = obj.optBoolean("starred", false),
            starRank = if (obj.has("starRank") && !obj.isNull("starRank")) obj.optInt("starRank") else null,
            repeatUnit = unit,
            repeatInterval = interval,
            remindEnabled = obj.optBoolean("remindEnabled", false),
            createdAt = obj.optLong("createdAt", System.currentTimeMillis()),
            updatedAt = obj.optLong("updatedAt", System.currentTimeMillis()),
        )
    }

    fun serialize(events: List<DayEvent>): String {
        val payload = org.json.JSONObject().apply {
            put("app", "NumberOurDays")
            put("schemaVersion", 1)
            put("exportedAt", System.currentTimeMillis())
            put("events", org.json.JSONArray().also { arr -> events.forEach { arr.put(toJson(it)) } })
        }
        return payload.toString(2)
    }

    fun parseBackup(text: String): Pair<List<DayEvent>, String?> {
        val trimmed = text.trim()
        if (trimmed.isEmpty()) return emptyList<DayEvent>() to "内容为空"
        return try {
            val data = org.json.JSONObject(trimmed)
            val arr = data.optJSONArray("events") ?: return emptyList<DayEvent>() to "缺少 events 数组"
            val events = mutableListOf<DayEvent>()
            var skipped = 0
            for (i in 0 until arr.length()) {
                val e = migrate(arr.optJSONObject(i))
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
                    map[l.id] = r.copy(id = l.id)
                    keyToId[naturalKey(l)] = l.id
                }
                else -> {
                    keyToId[naturalKey(l)] = l.id
                }
            }
        }
        return map.values.toList()
    }
}

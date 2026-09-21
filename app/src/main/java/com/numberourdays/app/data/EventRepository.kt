package com.numberourdays.app.data

import android.content.Context
import android.widget.Toast
import com.numberourdays.app.domain.DateCalculator
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import org.json.JSONArray
import org.json.JSONObject
import java.time.LocalDate
import java.util.UUID

class EventRepository(private val context: Context) {
    private val prefs = context.getSharedPreferences(KEY_FILE, Context.MODE_PRIVATE)

    private val _events = MutableStateFlow(loadFromPrefs())
    val events: StateFlow<List<DayEvent>> = _events.asStateFlow()

    private fun loadFromPrefs(): List<DayEvent> {
        val raw = prefs.getString(KEY_EVENTS, null) ?: return emptyList()
        return try {
            val arr = JSONArray(raw)
            (0 until arr.length()).mapNotNull { migrate(arr.optJSONObject(it)) }
        } catch (_: Exception) {
            emptyList()
        }
    }

    fun loadEvents(): List<DayEvent> = _events.value

    private fun persist(events: List<DayEvent>): Boolean {
        return try {
            val arr = JSONArray()
            events.forEach { arr.put(toJson(it)) }
            prefs.edit().putString(KEY_EVENTS, arr.toString()).apply()
            _events.value = events
            true
        } catch (e: Exception) {
            Toast.makeText(context, "保存失败", Toast.LENGTH_SHORT).show()
            false
        }
    }

    private fun migrate(obj: JSONObject?): DayEvent? {
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

    private fun toJson(e: DayEvent): JSONObject = JSONObject().apply {
        put("id", e.id)
        put("title", e.title)
        put("date", e.date)
        if (e.note != null) put("note", e.note) else put("note", JSONObject.NULL)
        put("starred", e.starred)
        if (e.starRank != null) put("starRank", e.starRank) else put("starRank", JSONObject.NULL)
        put("repeatUnit", e.repeatUnit.name.lowercase())
        put("repeatInterval", e.repeatInterval)
        put("remindEnabled", e.remindEnabled)
        put("createdAt", e.createdAt)
        put("updatedAt", e.updatedAt)
    }

    fun getEvent(id: String): DayEvent? = _events.value.find { it.id == id }

    fun upsert(event: DayEvent) {
        val list = _events.value.toMutableList()
        val idx = list.indexOfFirst { it.id == event.id }
        if (idx >= 0) list[idx] = event else list.add(0, event)
        persist(list)
    }

    fun remove(id: String) {
        persist(_events.value.filterNot { it.id == id })
    }

    fun clearAll() {
        persist(emptyList())
    }

    fun toggleStar(id: String) {
        val list = _events.value.toMutableList()
        val idx = list.indexOfFirst { it.id == id }
        if (idx < 0) return
        val item = list[idx]
        list[idx] = if (item.starred) {
            item.copy(starred = false, starRank = null, updatedAt = System.currentTimeMillis())
        } else {
            val maxRank = list.filter { it.starred }.maxOfOrNull { it.starRank ?: 0 } ?: 0
            item.copy(starred = true, starRank = maxRank + 1, updatedAt = System.currentTimeMillis())
        }
        persist(list)
    }

    fun reorderStars(orderedIds: List<String>) {
        val list = _events.value.toMutableList()
        orderedIds.forEachIndexed { index, id ->
            val idx = list.indexOfFirst { it.id == id }
            if (idx >= 0) {
                list[idx] = list[idx].copy(
                    starred = true,
                    starRank = index + 1,
                    updatedAt = System.currentTimeMillis(),
                )
            }
        }
        persist(list)
    }

    fun saveAll(events: List<DayEvent>) {
        persist(events)
    }

    fun serialize(events: List<DayEvent>): String {
        val payload = JSONObject().apply {
            put("app", "NumberOurDays")
            put("schemaVersion", 1)
            put("exportedAt", System.currentTimeMillis())
            put("events", JSONArray().also { arr -> events.forEach { arr.put(toJson(it)) } })
        }
        return payload.toString(2)
    }

    fun parseBackup(text: String): Pair<List<DayEvent>, String?> {
        val trimmed = text.trim()
        if (trimmed.isEmpty()) return emptyList<DayEvent>() to "内容为空"
        return try {
            val data = JSONObject(trimmed)
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

    fun merge(local: List<DayEvent>, incoming: List<DayEvent>): List<DayEvent> {
        val map = LinkedHashMap<String, DayEvent>()
        local.forEach { map[it.id] = it }
        incoming.forEach { r ->
            val l = map[r.id]
            if (l == null || r.updatedAt >= l.updatedAt) map[r.id] = r
        }
        return map.values.toList()
    }

    fun createId(): String =
        "e_${System.currentTimeMillis().toString(36)}_${UUID.randomUUID().toString().take(6)}"

    fun ensureDemoIfEmpty(): Boolean {
        if (_events.value.isNotEmpty()) return false
        if (prefs.getBoolean(KEY_DEMO, false)) return false
        saveAll(createDemoEvents())
        prefs.edit().putBoolean(KEY_DEMO, true).apply()
        return true
    }

    private fun createDemoEvents(): List<DayEvent> {
        val now = System.currentTimeMillis()
        val sat = LocalDate.now().with(java.time.DayOfWeek.SATURDAY)
            .let { if (it.isBefore(LocalDate.now())) it.plusWeeks(1) else it }
        val newYear = LocalDate.of(
            if (LocalDate.now().monthValue >= 12) LocalDate.now().year + 1 else LocalDate.now().year,
            1, 1,
        ).let { if (it.isBefore(LocalDate.now())) it.plusYears(1) else it }

        return listOf(
            DayEvent(
                id = createId(),
                title = "周末",
                date = DateCalculator.format(sat),
                note = "每周重复倒数演示",
                starred = true,
                starRank = 1,
                repeatUnit = RepeatUnit.WEEK,
                repeatInterval = 1,
                createdAt = now,
                updatedAt = now,
            ),
            DayEvent(
                id = createId(),
                title = "新年",
                date = DateCalculator.format(newYear),
                note = "未来倒数演示",
                starred = true,
                starRank = 2,
                createdAt = now,
                updatedAt = now,
            ),
        )
    }

    companion object {
        private const val KEY_FILE = "nod_prefs"
        private const val KEY_EVENTS = "nod:events:v1"
        private const val KEY_DEMO = "nod:demo_seeded:v2"
    }
}

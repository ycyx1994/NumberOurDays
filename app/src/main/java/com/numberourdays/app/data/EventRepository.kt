package com.numberourdays.app.data

import android.content.Context
import android.widget.Toast
import com.numberourdays.app.domain.DateCalculator
import com.numberourdays.app.notify.ReminderScheduler
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

    private fun syncReminders(events: List<DayEvent>) {
        events.forEach { ReminderScheduler.schedule(context, it) }
    }

    private fun migrate(obj: JSONObject?): DayEvent? = DayEventCodec.migrate(obj)

    private fun toJson(e: DayEvent): JSONObject = DayEventCodec.toJson(e)

    fun getEvent(id: String): DayEvent? = _events.value.find { it.id == id }

    fun upsert(event: DayEvent) {
        val list = _events.value.toMutableList()
        val idx = list.indexOfFirst { it.id == event.id }
        if (idx >= 0) list[idx] = event else list.add(0, event)
        if (persist(list)) ReminderScheduler.schedule(context, event)
    }

    fun remove(id: String) {
        val next = _events.value.filterNot { it.id == id }
        if (persist(next)) ReminderScheduler.cancel(context, id)
    }

    fun clearAll() {
        val ids = _events.value.map { it.id }
        if (persist(emptyList())) {
            ids.forEach { ReminderScheduler.cancel(context, it) }
        }
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
        if (persist(list)) ReminderScheduler.schedule(context, list[idx])
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
        val previousIds = _events.value.map { it.id }.toSet()
        if (persist(events)) {
            syncReminders(events)
            val liveIds = events.map { it.id }.toSet()
            previousIds.forEach { oldId ->
                if (oldId !in liveIds) ReminderScheduler.cancel(context, oldId)
            }
        }
    }

    fun serialize(events: List<DayEvent>): String = DayEventCodec.serialize(events)

    fun parseBackup(text: String): Pair<List<DayEvent>, String?> = DayEventCodec.parseBackup(text)

    fun merge(local: List<DayEvent>, incoming: List<DayEvent>): List<DayEvent> =
        DayEventCodec.merge(local, incoming)

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

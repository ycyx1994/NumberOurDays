package com.numberourdays.app.data

import android.content.Context
import android.widget.Toast
import com.numberourdays.app.data.db.NodDatabase
import com.numberourdays.app.data.db.toDomain
import com.numberourdays.app.data.db.toEntity
import com.numberourdays.app.domain.DateCalculator
import com.numberourdays.app.notify.ReminderScheduler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.json.JSONArray
import org.json.JSONObject
import java.time.LocalDate
import java.util.UUID

/**
 * Room-backed store. Public API stays synchronous-looking for UI:
 * mutations enqueue IO work and [events] reflects the latest DB state.
 */
class EventRepository(private val context: Context) {
    private val prefs = context.getSharedPreferences(KEY_FILE, Context.MODE_PRIVATE)
    private val dao = NodDatabase.get(context).dayEventDao()
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    private val _events = MutableStateFlow<List<DayEvent>>(emptyList())
    val events: StateFlow<List<DayEvent>> = _events.asStateFlow()

    init {
        scope.launch {
            migrateFromPrefsIfNeeded()
            ensureDemoIfEmpty()
            dao.observeAll().collect { rows ->
                _events.value = rows.map { it.toDomain() }
            }
        }
    }

    fun loadEvents(): List<DayEvent> = _events.value

    private suspend fun migrateFromPrefsIfNeeded() {
        val raw = prefs.getString(KEY_EVENTS, null) ?: return
        val migrated = try {
            val arr = JSONArray(raw)
            (0 until arr.length()).mapNotNull { migrate(arr.optJSONObject(it)) }
        } catch (_: Exception) {
            emptyList()
        }
        if (migrated.isEmpty()) return
        if (dao.count() > 0) return
        dao.upsertAll(migrated.map { it.toEntity() })
        // Keep a copy in prefs as last-resort backup; Room is now source of truth.
    }

    private fun migrate(obj: JSONObject?): DayEvent? = DayEventCodec.migrate(obj)

    private fun toJson(e: DayEvent): JSONObject = DayEventCodec.toJson(e)

    fun getEvent(id: String): DayEvent? = _events.value.find { it.id == id }

    fun upsert(event: DayEvent) {
        scope.launch {
            dao.upsert(event.toEntity())
            ReminderScheduler.schedule(context, event)
        }
    }

    fun remove(id: String) {
        scope.launch {
            dao.deleteById(id)
            ReminderScheduler.cancel(context, id)
        }
    }

    fun clearAll() {
        val ids = _events.value.map { it.id }
        scope.launch {
            dao.clearAll()
            ids.forEach { ReminderScheduler.cancel(context, it) }
        }
    }

    fun toggleStar(id: String) {
        val current = getEvent(id) ?: return
        val list = _events.value
        val updated = if (current.starred) {
            current.copy(starred = false, starRank = null, updatedAt = System.currentTimeMillis())
        } else {
            val maxRank = list.filter { it.starred }.maxOfOrNull { it.starRank ?: 0 } ?: 0
            current.copy(
                starred = true,
                starRank = maxRank + 1,
                updatedAt = System.currentTimeMillis(),
            )
        }
        upsert(updated)
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
        saveAll(list)
    }

    fun saveAll(events: List<DayEvent>) {
        val previousIds = _events.value.map { it.id }.toSet()
        scope.launch {
            dao.clearAll()
            dao.upsertAll(events.map { it.toEntity() })
            events.forEach { ReminderScheduler.schedule(context, it) }
            val live = events.map { it.id }.toSet()
            previousIds.forEach { oldId ->
                if (oldId !in live) ReminderScheduler.cancel(context, oldId)
            }
        }
    }

    fun serialize(events: List<DayEvent>): String = DayEventCodec.serialize(events)

    fun parseBackup(text: String): Pair<List<DayEvent>, String?> = DayEventCodec.parseBackup(text)

    fun merge(local: List<DayEvent>, incoming: List<DayEvent>): List<DayEvent> =
        DayEventCodec.merge(local, incoming)

    fun createId(): String =
        "e_${System.currentTimeMillis().toString(36)}_${UUID.randomUUID().toString().take(6)}"

    fun ensureDemoIfEmpty() {
        scope.launch {
            if (dao.count() > 0) return@launch
            if (prefs.getBoolean(KEY_DEMO, false)) return@launch
            dao.upsertAll(createDemoEvents().map { it.toEntity() })
            prefs.edit().putBoolean(KEY_DEMO, true).apply()
        }
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

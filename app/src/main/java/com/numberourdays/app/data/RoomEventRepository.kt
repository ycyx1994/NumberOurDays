package com.numberourdays.app.data

import android.content.Context
import com.numberourdays.app.data.db.NodDatabase
import com.numberourdays.app.data.db.toDomain
import com.numberourdays.app.data.db.toEntity
import com.numberourdays.app.notify.ReminderScheduler
import com.numberourdays.app.widget.NodWidgetReceiver
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * Room-backed [EventRepository]. Public API stays synchronous-looking for UI:
 * mutations enqueue IO work and [events] reflects the latest DB state.
 * Side effects (reminders, widgets, prefs migration) stay on this Android impl.
 */
class RoomEventRepository(private val context: Context) : EventRepository {
    private val prefs = context.getSharedPreferences(KEY_FILE, Context.MODE_PRIVATE)
    private val dao = NodDatabase.get(context).dayEventDao()
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    private val _events = MutableStateFlow<List<DayEvent>>(emptyList())
    override val events: StateFlow<List<DayEvent>> = _events.asStateFlow()

    init {
        scope.launch {
            migrateFromPrefsIfNeeded()
            ensureDemoIfEmpty()
            dao.observeAll().collect { rows ->
                _events.value = rows.map { it.toDomain() }
            }
        }
    }

    override fun loadEvents(): List<DayEvent> = _events.value

    private suspend fun migrateFromPrefsIfNeeded() {
        if (prefs.getBoolean(KEY_PREFS_MIGRATED, false)) return
        val raw = prefs.getString(KEY_EVENTS, null) ?: return
        val migrated = DayEventCodec.parseEventsArray(raw)
        if (migrated.isNotEmpty() && dao.count() == 0) {
            dao.upsertAll(migrated.map { it.toEntity() })
        }
        prefs.edit().putBoolean(KEY_PREFS_MIGRATED, true).apply()
    }

    private fun notifyWidgets() {
        runCatching { NodWidgetReceiver.requestRefresh(context) }
    }

    override fun getEvent(id: String): DayEvent? = _events.value.find { it.id == id }

    override fun upsert(event: DayEvent) {
        scope.launch {
            dao.upsert(event.toEntity())
            ReminderScheduler.schedule(context, event)
            notifyWidgets()
        }
    }

    override fun remove(id: String) {
        scope.launch {
            dao.deleteById(id)
            ReminderScheduler.cancel(context, id)
            notifyWidgets()
        }
    }

    override fun clearAll() {
        val ids = _events.value.map { it.id }
        scope.launch {
            // An explicit clear must win over one-time legacy import and demo seeding.
            prefs.edit()
                .putBoolean(KEY_PREFS_MIGRATED, true)
                .putBoolean(KEY_DEMO, true)
                .apply()
            dao.clearAll()
            ids.forEach { ReminderScheduler.cancel(context, it) }
            notifyWidgets()
        }
    }

    override fun toggleStar(id: String) {
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

    override fun reorderStars(orderedIds: List<String>) {
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

    override fun saveAll(events: List<DayEvent>) {
        val previousIds = _events.value.map { it.id }.toSet()
        scope.launch {
            dao.replaceAll(events.map { it.toEntity() })
            events.forEach { ReminderScheduler.schedule(context, it) }
            val live = events.map { it.id }.toSet()
            previousIds.forEach { oldId ->
                if (oldId !in live) ReminderScheduler.cancel(context, oldId)
            }
            notifyWidgets()
        }
    }

    override fun createId(): String = createEventId()

    override fun ensureDemoIfEmpty() {
        scope.launch {
            // Idempotent: stable ids + flag; safe if App/init both call.
            if (prefs.getBoolean(KEY_DEMO, false)) return@launch
            if (dao.count() > 0) {
                prefs.edit().putBoolean(KEY_DEMO, true).apply()
                return@launch
            }
            dao.upsertAll(DemoEvents.create().map { it.toEntity() })
            prefs.edit().putBoolean(KEY_DEMO, true).apply()
            notifyWidgets()
        }
    }

    companion object {
        private const val KEY_FILE = "nod_prefs"
        private const val KEY_EVENTS = "nod:events:v1"
        private const val KEY_DEMO = "nod:demo_seeded:v2"
        private const val KEY_PREFS_MIGRATED = "nod:prefs_migrated:v1"
    }
}

package com.numberourdays.app.notify

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.numberourdays.app.data.db.NodDatabase
import com.numberourdays.app.data.db.toDomain
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/** Re-arm exact alarms after reboot or app upgrade (HyperOS clears them). */
class BootReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val action = intent.action ?: return
        if (action != Intent.ACTION_BOOT_COMPLETED &&
            action != Intent.ACTION_MY_PACKAGE_REPLACED &&
            action != Intent.ACTION_TIME_CHANGED &&
            action != Intent.ACTION_TIMEZONE_CHANGED
        ) {
            return
        }
        val pendingResult = goAsync()
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val dao = NodDatabase.get(context).dayEventDao()
                val events = dao.getAll().map { it.toDomain() }
                events.filter { it.remindEnabled }.forEach { event ->
                    ReminderScheduler.schedule(context, event)
                }
            } finally {
                pendingResult.finish()
            }
        }
    }
}

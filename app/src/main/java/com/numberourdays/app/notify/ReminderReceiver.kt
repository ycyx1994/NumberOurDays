package com.numberourdays.app.notify

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.numberourdays.app.data.RepeatUnit
import com.numberourdays.app.data.db.NodDatabase
import com.numberourdays.app.data.db.toDomain
import com.numberourdays.app.domain.DateCalculator
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/** Fired by AlarmManager; posts the notification and re-arms repeating events. */
class ReminderReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val eventId = intent.getStringExtra(EXTRA_EVENT_ID) ?: return

        if (intent.getBooleanExtra(EXTRA_DEBUG, false)) {
            val title = intent.getStringExtra(EXTRA_TITLE) ?: "一分钟提醒"
            val text = intent.getStringExtra(EXTRA_TEXT) ?: "AlarmManager 精确闹钟链路正常"
            NotificationHelper.showReminder(
                context = context,
                eventId = eventId,
                title = title,
                text = text,
            )
            return
        }

        val pendingResult = goAsync()
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val event = NodDatabase.get(context).dayEventDao().getById(eventId)?.toDomain()
                if (event == null || !event.remindEnabled) return@launch
                val display = DateCalculator.computeDisplay(event)
                val body = context.getString(
                    com.numberourdays.app.R.string.notif_body,
                    display.label,
                )
                NotificationHelper.showReminder(
                    context = context,
                    eventId = event.id,
                    title = display.title,
                    text = body,
                )
                if (event.repeatUnit != RepeatUnit.NONE) {
                    ReminderScheduler.schedule(context, event, afterFire = true)
                }
            } finally {
                pendingResult.finish()
            }
        }
    }

    companion object {
        const val EXTRA_EVENT_ID = "com.numberourdays.app.extra.EVENT_ID"
        const val EXTRA_DEBUG = "com.numberourdays.app.extra.DEBUG"
        const val EXTRA_TITLE = "com.numberourdays.app.extra.TITLE"
        const val EXTRA_TEXT = "com.numberourdays.app.extra.TEXT"
    }
}

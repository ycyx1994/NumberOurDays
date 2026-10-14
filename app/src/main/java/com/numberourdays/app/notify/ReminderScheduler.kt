package com.numberourdays.app.notify

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import com.numberourdays.app.data.DayEvent

/**
 * Exact-alarm scheduler. WorkManager delays are unreliable on HyperOS/MIUI
 * (doze + process kill), so user-facing reminders use AlarmManager like
 * a typical medication/alarm app.
 */
object ReminderScheduler {

    private const val ACTION = "com.numberourdays.app.action.REMINDER"

    fun schedule(context: Context, event: DayEvent, afterFire: Boolean = false) {
        if (!event.remindEnabled) {
            cancel(context, event.id)
            return
        }
        val at = ReminderTiming.nextReminderMillis(
            anchorDate = event.date,
            unit = event.repeatUnit,
            interval = event.repeatInterval,
            hourOfDay = event.remindHour,
            minuteOfHour = event.remindMinute,
            notBefore = System.currentTimeMillis(),
        ) ?: run {
            cancel(context, event.id)
            return
        }

        val am = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val pending = pendingIntent(context, event.id)
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                am.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, at, pending)
            } else {
                am.setExact(AlarmManager.RTC_WAKEUP, at, pending)
            }
        } catch (_: SecurityException) {
            // No SCHEDULE_EXACT_ALARM — fall back to inexact (may drift).
            am.set(AlarmManager.RTC_WAKEUP, at, pending)
        }
    }

    fun cancel(context: Context, eventId: String) {
        val am = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        am.cancel(pendingIntent(context, eventId))
    }

    fun canScheduleExact(context: Context): Boolean {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.S) return true
        val am = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        return am.canScheduleExactAlarms()
    }

    /** Debug helper: fire a standalone reminder after [delayMillis] (default 60s). */
    fun scheduleDebugReminder(context: Context, delayMillis: Long = 60_000L) {
        val at = System.currentTimeMillis() + delayMillis
        val intent = Intent(context, ReminderReceiver::class.java).apply {
            action = ACTION
            putExtra(ReminderReceiver.EXTRA_EVENT_ID, "debug_reminder")
            putExtra(ReminderReceiver.EXTRA_DEBUG, true)
            putExtra(ReminderReceiver.EXTRA_TITLE, "一分钟提醒")
            putExtra(ReminderReceiver.EXTRA_TEXT, "AlarmManager 精确闹钟链路正常")
        }
        val pending = PendingIntent.getBroadcast(
            context,
            DEBUG_REQUEST_CODE,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
        )
        val am = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                am.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, at, pending)
            } else {
                am.setExact(AlarmManager.RTC_WAKEUP, at, pending)
            }
        } catch (_: SecurityException) {
            am.set(AlarmManager.RTC_WAKEUP, at, pending)
        }
    }

    private fun pendingIntent(context: Context, eventId: String): PendingIntent {
        val intent = Intent(context, ReminderReceiver::class.java).apply {
            action = ACTION
            putExtra(ReminderReceiver.EXTRA_EVENT_ID, eventId)
        }
        return PendingIntent.getBroadcast(
            context,
            eventId.hashCode(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
        )
    }

    private const val DEBUG_REQUEST_CODE = 0x0D0B
}

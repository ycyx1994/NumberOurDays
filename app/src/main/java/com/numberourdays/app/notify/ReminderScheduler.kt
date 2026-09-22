package com.numberourdays.app.notify

import android.content.Context
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.workDataOf
import com.numberourdays.app.data.DayEvent
import java.util.concurrent.TimeUnit

object ReminderScheduler {

    fun workName(eventId: String): String = "remind_$eventId"

    fun schedule(context: Context, event: DayEvent, afterFire: Boolean = false) {
        val wm = WorkManager.getInstance(context)
        val name = workName(event.id)
        if (!event.remindEnabled) {
            wm.cancelUniqueWork(name)
            return
        }
        // after fire, "now" is already past the fired 09:00, so nextReminderMillis advances
        val at = ReminderTiming.nextReminderMillis(
            anchorDate = event.date,
            unit = event.repeatUnit,
            interval = event.repeatInterval,
            hourOfDay = event.remindHour,
            minuteOfHour = event.remindMinute,
            notBefore = System.currentTimeMillis(),
        )
        if (at == null) {
            wm.cancelUniqueWork(name)
            return
        }
        val delay = (at - System.currentTimeMillis()).coerceAtLeast(0L)
        val request = OneTimeWorkRequestBuilder<ReminderWorker>()
            .setInitialDelay(delay, TimeUnit.MILLISECONDS)
            .setInputData(workDataOf(ReminderWorker.KEY_ID to event.id))
            .addTag(TAG)
            .build()
        wm.enqueueUniqueWork(name, ExistingWorkPolicy.REPLACE, request)
    }

    fun cancel(context: Context, eventId: String) {
        WorkManager.getInstance(context).cancelUniqueWork(workName(eventId))
    }

    private const val TAG = "nod_reminders"
}

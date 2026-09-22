package com.numberourdays.app.notify

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.numberourdays.app.App
import com.numberourdays.app.R
import com.numberourdays.app.data.RepeatUnit
import com.numberourdays.app.domain.DateCalculator

class ReminderWorker(
    context: Context,
    params: WorkerParameters,
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        val id = inputData.getString(KEY_ID) ?: return Result.success()
        val event = App.repository.getEvent(id) ?: return Result.success()
        if (!event.remindEnabled) return Result.success()

        val display = DateCalculator.computeDisplay(event)
        val body = applicationContext.getString(
            R.string.notif_body,
            display.title,
            display.label,
        )
        NotificationHelper.showReminder(
            context = applicationContext,
            eventId = event.id,
            title = display.title,
            text = body,
        )

        if (event.repeatUnit != RepeatUnit.NONE) {
            ReminderScheduler.schedule(applicationContext, event, afterFire = true)
        }
        return Result.success()
    }

    companion object {
        const val KEY_ID = "id"
    }
}

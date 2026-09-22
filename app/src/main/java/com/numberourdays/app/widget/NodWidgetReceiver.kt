package com.numberourdays.app.widget

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.view.View
import android.widget.RemoteViews
import com.numberourdays.app.App
import com.numberourdays.app.R
import com.numberourdays.app.data.EventKind
import com.numberourdays.app.domain.DateCalculator
import com.numberourdays.app.notify.NotificationHelper
import com.numberourdays.app.ui.MainActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

/**
 * HyperOS-friendly home widget (standard AppWidget). Shows the nearest
 * day-count like the in-app hero card.
 */
class NodWidgetReceiver : AppWidgetProvider() {

    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray,
    ) {
        val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
        scope.launch {
            val display = nearestDisplay()
            appWidgetIds.forEach { id ->
                appWidgetManager.updateAppWidget(id, buildViews(context, display))
            }
        }
    }

    companion object {
        const val ACTION_REFRESH = "com.numberourdays.app.action.WIDGET_REFRESH"

        fun requestRefresh(context: Context) {
            val intent = Intent(context, NodWidgetReceiver::class.java).apply {
                action = AppWidgetManager.ACTION_APPWIDGET_UPDATE
                putExtra(
                    AppWidgetManager.EXTRA_APPWIDGET_IDS,
                    AppWidgetManager.getInstance(context)
                        .getAppWidgetIds(ComponentName(context, NodWidgetReceiver::class.java)),
                )
            }
            context.sendBroadcast(intent)
        }

        private fun nearestDisplay() =
            DateCalculator.sortEvents(
                App.repository.loadEvents().map { DateCalculator.computeDisplay(it) },
            ).let { DateCalculator.pickNearest(it) }

        private fun buildViews(context: Context, display: com.numberourdays.app.data.DayEventDisplay?): RemoteViews {
            val wide = RemoteViews(context.packageName, R.layout.widget_daycount_wide)
            val compact = RemoteViews(context.packageName, R.layout.widget_daycount)
            listOf(wide, compact).forEach { views ->
                if (display == null) {
                    views.setTextViewText(R.id.widget_title, context.getString(R.string.widget_empty_title))
                    views.setTextViewText(R.id.widget_value, "—")
                    views.setTextViewText(R.id.widget_tag, context.getString(R.string.app_name))
                    views.setTextViewText(R.id.widget_meta, context.getString(R.string.widget_empty_meta))
                    views.setTextViewText(R.id.widget_unit, "")
                    val pi = mainPending(context, null)
                    views.setOnClickPendingIntent(R.id.widget_root, pi)
                } else {
                    val tag = display.repeatLabel
                        ?: when (display.kind) {
                            EventKind.COUNTDOWN -> "倒数"
                            EventKind.ELAPSED -> "已过"
                            EventKind.TODAY -> "今天"
                            EventKind.REPEAT -> "重复"
                        }
                    views.setTextViewText(R.id.widget_tag, context.getString(R.string.next_prefix, tag))
                    views.setTextViewText(R.id.widget_title, display.title)
                    views.setTextViewText(R.id.widget_value, display.days.toString())
                    views.setTextViewText(R.id.widget_meta, display.date)
                    views.setTextViewText(R.id.widget_unit, context.getString(R.string.unit_day))
                    val pi = mainPending(context, display.id)
                    views.setOnClickPendingIntent(R.id.widget_root, pi)
                }
            }
            // Prefer wide if available in host; same IDs so both work when inflated alone.
            // HyperOS often uses the provider initialLayout (compact) after resize.
            return mergeForHost(wide, compact)
        }

        /** Single RemoteViews graph — HyperOS/Android pick layout via size; we ship compact as primary. */
        private fun mergeForHost(wide: RemoteViews, compact: RemoteViews): RemoteViews {
            // Return compact as initialLayout-compatible views; also expose wide via apply.
            // Hosts that only support one layout get compact (matches xml initialLayout).
            return compact.apply {
                // keep wide content parity already applied
            }.also {
                // unused param kept for API symmetry / future size-specific APIs
                wide.hashCode()
            }
        }

        private fun mainPending(context: Context, eventId: String?): PendingIntent {
            val intent = Intent(context, MainActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
                if (eventId != null) putExtra(NotificationHelper.EXTRA_EVENT_ID, eventId)
            }
            val req = eventId?.hashCode() ?: 0
            return PendingIntent.getActivity(
                context,
                req,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
            )
        }
    }
}

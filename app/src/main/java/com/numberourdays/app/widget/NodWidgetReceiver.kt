package com.numberourdays.app.widget

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Typeface
import android.view.View
import android.widget.RemoteViews
import androidx.core.content.res.ResourcesCompat
import com.numberourdays.app.R
import com.numberourdays.app.data.EventKind
import com.numberourdays.app.data.db.NodDatabase
import com.numberourdays.app.data.db.toDomain
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

    override fun onReceive(context: Context, intent: Intent) {
        val dateChanged = intent.action == Intent.ACTION_DATE_CHANGED ||
            intent.action == Intent.ACTION_TIME_CHANGED ||
            intent.action == Intent.ACTION_TIMEZONE_CHANGED
        if (dateChanged) {
            val manager = AppWidgetManager.getInstance(context)
            onUpdate(
                context,
                manager,
                manager.getAppWidgetIds(ComponentName(context, NodWidgetReceiver::class.java)),
            )
        }
        super.onReceive(context, intent)
    }

    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray,
    ) {
        val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
        scope.launch {
            val display = nearestDisplay(context)
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

        private suspend fun nearestDisplay(context: Context) =
            NodDatabase.get(context).dayEventDao().getAll()
                .map { it.toDomain() }
                .map { DateCalculator.computeDisplay(it) }
                .let { DateCalculator.sortEvents(it) }
                .let { DateCalculator.pickNearest(it) }

        private fun numberBitmap(
            context: Context,
            text: String,
            textSizePx: Float,
        ): Bitmap {
            val typeface: Typeface = ResourcesCompat.getFont(
                context,
                R.font.barlow_condensed_bold,
            ) ?: Typeface.DEFAULT_BOLD
            val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                color = 0xFFD4553A.toInt()
                this.textSize = textSizePx
                this.typeface = typeface
                textAlign = Paint.Align.LEFT
            }
            val pad = (textSizePx * 0.12f).toInt().coerceAtLeast(2)
            val textW = paint.measureText(text)
            val fm = paint.fontMetricsInt
            val h = fm.descent - fm.ascent + pad * 2
            val w = (textW + pad * 2).toInt().coerceAtLeast(1)
            val bmp = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888)
            val canvas = Canvas(bmp)
            canvas.drawText(text, pad.toFloat(), pad - fm.ascent.toFloat(), paint)
            return bmp
        }

        private fun buildViews(context: Context, display: com.numberourdays.app.data.DayEventDisplay?): RemoteViews {
            val wide = RemoteViews(context.packageName, R.layout.widget_daycount_wide)
            val compact = RemoteViews(context.packageName, R.layout.widget_daycount)
            listOf(wide, compact).forEach { views ->
                if (display == null) {
                    views.setTextViewText(R.id.widget_title, context.getString(R.string.widget_empty_title))
                    views.setTextViewText(R.id.widget_tag, context.getString(R.string.app_name))
                    views.setTextViewText(R.id.widget_meta, context.getString(R.string.widget_empty_meta))
                    views.setTextViewText(R.id.widget_unit, "")
                    views.setImageViewResource(R.id.widget_value, android.R.color.transparent)
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
                    views.setTextViewText(R.id.widget_meta, display.date)
                    views.setTextViewText(R.id.widget_unit, context.getString(R.string.unit_day))
                    val sp = context.resources.displayMetrics.scaledDensity
                    val px = if (views === wide) 56f * sp else 40f * sp
                    views.setImageViewBitmap(
                        R.id.widget_value,
                        numberBitmap(context, display.days.toString(), px),
                    )
                    val pi = mainPending(context, display.id)
                    views.setOnClickPendingIntent(R.id.widget_root, pi)
                }
            }
            return compact
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

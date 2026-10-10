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
import android.os.Bundle
import android.widget.RemoteViews
import androidx.core.content.res.ResourcesCompat
import com.numberourdays.app.R
import com.numberourdays.app.data.HolidayCountdown
import com.numberourdays.app.data.HolidayStatus
import com.numberourdays.app.data.LegalHolidays
import com.numberourdays.app.domain.DateCalculator
import com.numberourdays.app.domain.HolidayCalculator
import com.numberourdays.app.ui.MainActivity

/**
 * Home-screen widget for the nearest legal holiday vacation (放假首日倒数).
 * Resizable 1×1 / 2×1 / 2×2 (+ wide); layout follows the current cell size.
 * Click opens the in-app holiday list.
 */
class HolidayWidgetReceiver : AppWidgetProvider() {

    override fun onReceive(context: Context, intent: Intent) {
        val dateChanged = intent.action == Intent.ACTION_DATE_CHANGED ||
            intent.action == Intent.ACTION_TIME_CHANGED ||
            intent.action == Intent.ACTION_TIMEZONE_CHANGED
        if (dateChanged) {
            val manager = AppWidgetManager.getInstance(context)
            onUpdate(
                context,
                manager,
                manager.getAppWidgetIds(ComponentName(context, HolidayWidgetReceiver::class.java)),
            )
        }
        super.onReceive(context, intent)
    }

    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray,
    ) {
        val countdown = currentCountdown()
        appWidgetIds.forEach { id ->
            val opts = appWidgetManager.getAppWidgetOptions(id)
            appWidgetManager.updateAppWidget(id, buildViews(context, countdown, opts))
        }
    }

    override fun onAppWidgetOptionsChanged(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetId: Int,
        newOptions: Bundle,
    ) {
        appWidgetManager.updateAppWidget(
            appWidgetId,
            buildViews(context, currentCountdown(), newOptions),
        )
    }

    companion object {
        const val EXTRA_OPEN_HOLIDAYS = "com.numberourdays.app.extra.OPEN_HOLIDAYS"

        /** Rough cell breakpoints in dp (launcher-reported min box). */
        private const val DP_1X1 = 85
        private const val DP_STRIP = 95
        private const val DP_WIDE = 280

        fun requestRefresh(context: Context) {
            val intent = Intent(context, HolidayWidgetReceiver::class.java).apply {
                action = AppWidgetManager.ACTION_APPWIDGET_UPDATE
                putExtra(
                    AppWidgetManager.EXTRA_APPWIDGET_IDS,
                    AppWidgetManager.getInstance(context)
                        .getAppWidgetIds(ComponentName(context, HolidayWidgetReceiver::class.java)),
                )
            }
            context.sendBroadcast(intent)
        }

        private fun currentCountdown(): HolidayCountdown? =
            HolidayCalculator.nextOrOngoing(
                LegalHolidays.periods(),
                DateCalculator.today(),
            )

        /**
         * 1×1 both sides tiny; 2×1 a short strip; 2×2 near-square card with
         * full meta; wide horizontal when very wide.
         */
        private fun layoutFor(widthDp: Int, heightDp: Int): Int {
            val square = widthDp >= 90 && heightDp >= 90
            val strip = heightDp <= DP_STRIP || (widthDp > heightDp * 1.8)
            return when {
                widthDp <= DP_1X1 && heightDp <= DP_1X1 -> R.layout.widget_holiday_1x1
                widthDp >= DP_WIDE && heightDp < 140 -> R.layout.widget_holiday_wide
                square && !strip -> R.layout.widget_holiday
                strip -> R.layout.widget_holiday_2x1
                else -> R.layout.widget_holiday
            }
        }

        private fun numberSizeFor(layout: Int): Float = when (layout) {
            R.layout.widget_holiday_1x1 -> 36f
            R.layout.widget_holiday_2x1 -> 32f
            R.layout.widget_holiday_wide -> 56f
            else -> 40f
        }

        private fun buildViews(
            context: Context,
            countdown: HolidayCountdown?,
            options: Bundle,
        ): RemoteViews {
            val density = context.resources.displayMetrics.density
            val minW = options.getInt(AppWidgetManager.OPTION_APPWIDGET_MIN_WIDTH, 110)
            val minH = options.getInt(AppWidgetManager.OPTION_APPWIDGET_MIN_HEIGHT, 110)
            val widthDp = (minW / density).toInt().coerceAtLeast(40)
            val heightDp = (minH / density).toInt().coerceAtLeast(40)
            val layout = layoutFor(widthDp, heightDp)
            val views = RemoteViews(context.packageName, layout)
            val sp = context.resources.displayMetrics.scaledDensity
            val emptyTitle = context.getString(R.string.widget_holiday_empty)
            val legal = context.getString(R.string.holiday_legal)
            val unit = context.getString(R.string.unit_day)
            val title = countdown?.period?.name ?: emptyTitle
            val meta = when {
                countdown == null -> context.getString(R.string.app_name)
                countdown.status == HolidayStatus.ONGOING ->
                    context.getString(R.string.holiday_ongoing) + " · " +
                        DateCalculator.format(countdown.period.start) +
                        " ~ " + DateCalculator.format(countdown.period.end)
                else ->
                    DateCalculator.format(countdown.period.start) +
                        " ~ " + DateCalculator.format(countdown.period.end)
            }
            val valueText = when {
                countdown == null -> ""
                countdown.status == HolidayStatus.ONGOING -> "0"
                else -> countdown.daysUntilStart.toString()
            }
            val bmp = if (valueText.isEmpty()) {
                null
            } else {
                numberBitmap(context, valueText, numberSizeFor(layout) * sp)
            }

            when (layout) {
                R.layout.widget_holiday_1x1 -> {
                    views.setTextViewText(R.id.widget_title, title)
                    if (bmp != null) {
                        views.setImageViewBitmap(R.id.widget_value, bmp)
                    } else {
                        views.setImageViewResource(R.id.widget_value, android.R.color.transparent)
                    }
                }
                R.layout.widget_holiday_2x1 -> {
                    views.setTextViewText(R.id.widget_tag, legal)
                    views.setTextViewText(R.id.widget_title, title)
                    views.setTextViewText(R.id.widget_unit, unit)
                    if (bmp != null) {
                        views.setImageViewBitmap(R.id.widget_value, bmp)
                    } else {
                        views.setImageViewResource(R.id.widget_value, android.R.color.transparent)
                    }
                }
                R.layout.widget_holiday_wide -> {
                    views.setTextViewText(R.id.widget_tag, legal)
                    views.setTextViewText(R.id.widget_title, title)
                    views.setTextViewText(R.id.widget_meta, meta)
                    views.setTextViewText(R.id.widget_unit, unit)
                    if (bmp != null) {
                        views.setImageViewBitmap(R.id.widget_value, bmp)
                    } else {
                        views.setImageViewResource(R.id.widget_value, android.R.color.transparent)
                    }
                }
                else -> {
                    // 2×2 — full card: tag, name, big number + unit, date range
                    views.setTextViewText(R.id.widget_tag, legal)
                    views.setTextViewText(R.id.widget_title, title)
                    views.setTextViewText(R.id.widget_unit, unit)
                    views.setTextViewText(R.id.widget_meta, meta)
                    if (bmp != null) {
                        views.setImageViewBitmap(R.id.widget_value, bmp)
                    } else {
                        views.setImageViewResource(R.id.widget_value, android.R.color.transparent)
                    }
                }
            }
            views.setOnClickPendingIntent(R.id.widget_root, holidaysPending(context))
            return views
        }

        private fun numberBitmap(context: Context, text: String, textSizePx: Float): Bitmap {
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

        private fun holidaysPending(context: Context): PendingIntent {
            val intent = Intent(context, MainActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
                putExtra(EXTRA_OPEN_HOLIDAYS, true)
            }
            return PendingIntent.getActivity(
                context,
                0xB011,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
            )
        }
    }
}

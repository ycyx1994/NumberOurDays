package com.numberourdays.app.domain

import com.numberourdays.app.data.DayEvent
import com.numberourdays.app.data.DayEventDisplay
import com.numberourdays.app.data.DurationBreakdown
import com.numberourdays.app.data.DurationDisplay
import com.numberourdays.app.data.DurationUnit
import com.numberourdays.app.data.EventKind
import com.numberourdays.app.data.RepeatUnit
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min
import kotlin.math.roundToInt

object DateCalculator {
    private val FMT: DateTimeFormatter = DateTimeFormatter.ISO_LOCAL_DATE

    fun today(): LocalDate = LocalDate.now()

    fun format(date: LocalDate): String = date.format(FMT)

    fun parse(text: String?): LocalDate? = runCatching {
        LocalDate.parse(text, FMT)
    }.getOrNull()

    fun isValidDate(text: String?): Boolean {
        if (text.isNullOrBlank() || !Regex("""\d{4}-\d{2}-\d{2}""").matches(text)) return false
        return parse(text) != null
    }

    fun diffDays(from: LocalDate, to: LocalDate): Long =
        ChronoUnit.DAYS.between(from, to)

    fun formatRepeatLabel(unit: RepeatUnit, interval: Int): String? {
        if (unit == RepeatUnit.NONE) return null
        val n = max(1, interval)
        val unitName = when (unit) {
            RepeatUnit.DAY -> "天"
            RepeatUnit.WEEK -> "周"
            RepeatUnit.MONTH -> "月"
            RepeatUnit.YEAR -> "年"
            RepeatUnit.NONE -> return null
        }
        return if (n == 1) "每$unitName" else "每 $n $unitName"
    }

    /**
     * Next occurrence on/after ref; for NONE returns anchor as-is.
     * MONTH/YEAR steps always measure from the anchor day-of-month so
     * 1/31 monthly stays …1/31, 2/28, 3/31… instead of drifting to 3/28.
     */
    fun resolveNextDate(anchorDate: String, unit: RepeatUnit, interval: Int, ref: LocalDate = today()): LocalDate {
        val anchor = parse(anchorDate) ?: return ref
        if (unit == RepeatUnit.NONE) return anchor
        var next = anchor
        val step = max(1, interval)
        var n = 0
        var guard = 0
        while (next.isBefore(ref) && guard < 20000) {
            n += 1
            next = when (unit) {
                RepeatUnit.DAY -> anchor.plusDays(n.toLong() * step)
                RepeatUnit.WEEK -> anchor.plusWeeks(n.toLong() * step)
                RepeatUnit.MONTH -> plusMonthsFromAnchor(anchor, n.toLong() * step)
                RepeatUnit.YEAR -> plusYearsFromAnchor(anchor, n.toLong() * step)
                RepeatUnit.NONE -> next
            }
            guard++
        }
        return next
    }

    /** anchor + months, keeping day-of-month when the target month allows it. */
    fun plusMonthsFromAnchor(anchor: LocalDate, monthsToAdd: Long): LocalDate {
        val ym = YearMonth.from(anchor).plusMonths(monthsToAdd)
        val day = min(anchor.dayOfMonth, ym.lengthOfMonth())
        return ym.atDay(day)
    }

    /** anchor + years; 2/29 falls on 2/28 in non-leap target years. */
    fun plusYearsFromAnchor(anchor: LocalDate, yearsToAdd: Long): LocalDate {
        val year = (anchor.year + yearsToAdd.toInt())
        val day = min(anchor.dayOfMonth, YearMonth.of(year, anchor.month).lengthOfMonth())
        return LocalDate.of(year, anchor.month, day)
    }

    fun computeDisplay(event: DayEvent, ref: LocalDate = today()): DayEventDisplay {
        val anchor = parse(event.date) ?: ref
        val unit = event.repeatUnit
        val interval = max(1, event.repeatInterval)
        val elapsed = max(0, diffDays(anchor, ref))
        val repeatLabel = formatRepeatLabel(unit, interval)

        if (unit != RepeatUnit.NONE) {
            val next = resolveNextDate(event.date, unit, interval, ref)
            val toNext = max(0, diffDays(ref, next))
            val isToday = toNext == 0L
            return DayEventDisplay(
                event = event,
                kind = EventKind.REPEAT,
                days = toNext.toInt(),
                elapsedDays = elapsed.toInt(),
                label = if (isToday) "今天" else "还有 $toNext 天",
                nextDate = format(next),
                repeatLabel = repeatLabel,
                sortDate = format(next),
            )
        }

        val pastOrFuture = diffDays(anchor, ref).toInt()
        return when {
            pastOrFuture == 0 -> DayEventDisplay(
                event, EventKind.TODAY, 0, 0, "今天",
                repeatLabel = repeatLabel, sortDate = event.date,
            )
            pastOrFuture < 0 -> {
                val remain = abs(pastOrFuture)
                DayEventDisplay(
                    event, EventKind.COUNTDOWN, remain, 0, "还有 $remain 天",
                    repeatLabel = repeatLabel, sortDate = event.date,
                )
            }
            else -> DayEventDisplay(
                event, EventKind.ELAPSED, pastOrFuture, pastOrFuture, "已经过 $pastOrFuture 天",
                repeatLabel = repeatLabel, sortDate = event.date,
            )
        }
    }

    private fun rank(e: DayEventDisplay): Int = when (e.kind) {
        EventKind.REPEAT, EventKind.TODAY -> 0
        EventKind.COUNTDOWN -> 1
        EventKind.ELAPSED -> 2
    }

    fun sortEvents(list: List<DayEventDisplay>): List<DayEventDisplay> =
        list.sortedWith(
            compareByDescending<DayEventDisplay> { it.starred }
                .thenBy { if (it.starred) (it.event.starRank ?: 0) else 0 }
                .thenBy { rank(it) }
                .thenBy {
                    if (rank(it) == 2) it.sortDate.reversed() else it.sortDate
                }
                .thenBy { it.id }
        )

    fun pickNearest(list: List<DayEventDisplay>): DayEventDisplay? {
        if (list.isEmpty()) return null
        val upcoming = list.filter {
            (it.kind == EventKind.REPEAT || it.kind == EventKind.COUNTDOWN) && it.days > 0
        }
        return upcoming.minByOrNull { it.days } ?: list.first()
    }

    fun carouselList(list: List<DayEventDisplay>): List<DayEventDisplay> {
        if (list.isEmpty()) return emptyList()
        val nearest = pickNearest(list) ?: return list
        return listOf(nearest) + list.filter { it.id != nearest.id }
    }

    /**
     * Final star-list index after a drag of [deltaY] px on a row of [rowHeightPx].
     * Down is positive; result is clamped to [0, lastIndex].
     */
    fun dragTargetIndex(from: Int, lastIndex: Int, deltaY: Float, rowHeightPx: Float): Int {
        if (from < 0 || lastIndex < 0 || from > lastIndex) return from
        if (rowHeightPx <= 0f) return from
        val steps = (deltaY / rowHeightPx).roundToInt()
        if (steps == 0) return from
        return (from + steps).coerceIn(0, lastIndex)
    }

    fun convertDuration(from: LocalDate, to: LocalDate): DurationBreakdown {
        val start = if (from.isBefore(to)) from else to
        val end = if (from.isBefore(to)) to else from

        var years = end.year - start.year
        var afterYears = start.plusYears(years.toLong())
        if (afterYears.isAfter(end)) {
            years--
            afterYears = start.plusYears(years.toLong())
        }
        var monthsAfterYears = 0
        var cur = afterYears
        while (true) {
            val nxt = cur.plusMonths(1)
            if (!nxt.isAfter(end)) {
                monthsAfterYears++
                cur = nxt
            } else break
            if (monthsAfterYears > 1200) break
        }
        val daysAfterMonthsFromYears = ChronoUnit.DAYS.between(cur, end).toInt()

        var months = 0
        var cur2 = start
        while (true) {
            val nxt = cur2.plusMonths(1)
            if (!nxt.isAfter(end)) {
                months++
                cur2 = nxt
            } else break
            if (months > 1200) break
        }
        val daysAfterMonths = ChronoUnit.DAYS.between(cur2, end).toInt()

        return DurationBreakdown(
            days = abs(diffDays(start, end)).toInt(),
            years = years,
            monthsAfterYears = monthsAfterYears,
            daysAfterMonthsFromYears = daysAfterMonthsFromYears,
            months = months,
            daysAfterMonths = daysAfterMonths,
        )
    }

    private fun joinNonZero(parts: List<Pair<Int, String>>): String =
        parts.filter { it.first > 0 }.joinToString(" ") { "${it.first} ${it.second}" }

    fun formatDuration(b: DurationBreakdown, unit: DurationUnit): DurationDisplay =
        when (unit) {
            DurationUnit.DAY -> DurationDisplay(b.days, "天", "")
            DurationUnit.MONTH ->
                if (b.months > 0) {
                    DurationDisplay(b.months, "月", joinNonZero(listOf(b.daysAfterMonths to "天")))
                } else {
                    DurationDisplay(b.daysAfterMonths, "天", "")
                }
            DurationUnit.YEAR -> when {
                b.years > 0 -> DurationDisplay(
                    b.years, "年",
                    joinNonZero(listOf(b.monthsAfterYears to "月", b.daysAfterMonthsFromYears to "天")),
                )
                b.monthsAfterYears > 0 ->
                    DurationDisplay(b.monthsAfterYears, "月", joinNonZero(listOf(b.daysAfterMonthsFromYears to "天")))
                else -> DurationDisplay(b.daysAfterMonthsFromYears, "天", "")
            }
        }

    fun detailTargetDate(event: DayEventDisplay): LocalDate =
        parse(event.nextDate ?: event.date) ?: today()

    fun validateForm(title: String, date: String, unit: RepeatUnit, interval: Int): String? {
        val t = title.trim()
        if (t.isEmpty()) return "请填写标题"
        if (t.length > 30) return "标题不超过 30 字"
        if (!isValidDate(date)) return "请选择合法日期"
        if (unit != RepeatUnit.NONE) {
            val n = interval
            if (n < 1) return "重复间隔至少为 1"
            if (n > 999) return "重复间隔过大"
        }
        return null
    }
}

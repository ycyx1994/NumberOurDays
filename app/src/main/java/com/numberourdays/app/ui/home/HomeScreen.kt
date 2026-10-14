package com.numberourdays.app.ui.home

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.StarOutline
import androidx.compose.material.icons.rounded.Star
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.numberourdays.app.App
import com.numberourdays.app.R
import com.numberourdays.app.data.DayEventDisplay
import com.numberourdays.app.data.EventKind
import com.numberourdays.app.data.HolidayCountdown
import com.numberourdays.app.data.HolidayStatus
import com.numberourdays.app.data.LegalHolidays
import com.numberourdays.app.domain.DateCalculator
import com.numberourdays.app.domain.HolidayCalculator
import com.numberourdays.app.ui.components.DayCountHero
import com.numberourdays.app.ui.components.NodTopBar
import com.numberourdays.app.ui.components.rememberCurrentDate
import com.numberourdays.app.ui.theme.NodCardShape
import com.numberourdays.app.ui.theme.NodDisplayCount
import com.numberourdays.app.ui.theme.NodDisplayNumber
import com.numberourdays.app.ui.theme.NodFabShape
import com.numberourdays.app.ui.theme.HeroCardMinHeight
import com.numberourdays.app.ui.theme.HeroCardPadH
import com.numberourdays.app.ui.theme.HeroCardPadV
import com.numberourdays.app.ui.theme.HeroPageMarginH
import com.numberourdays.app.ui.theme.HeroPageMarginTop
import kotlin.math.absoluteValue
import com.numberourdays.app.ui.theme.LocalNodBrand

private sealed interface Slide {
    data class Event(val display: DayEventDisplay) : Slide
    data class Holiday(val countdown: HolidayCountdown) : Slide
}

private fun slideKey(slide: Slide): Any = when (slide) {
    is Slide.Event -> "e:${slide.display.id}:${slide.display.date}"
    is Slide.Holiday -> "h:${slide.countdown.period.name}:${slide.countdown.period.start}"
}

@Composable
fun HomeScreen(
    onOpenDetail: (String) -> Unit,
    onOpenSettings: () -> Unit,
    onOpenAdd: () -> Unit,
    onOpenHolidays: () -> Unit,
) {
    val repo = App.repository
    val events by repo.events.collectAsState()
    val today = rememberCurrentDate()
    val displays = remember(events, today) {
        DateCalculator.sortEvents(events.map { DateCalculator.computeDisplay(it, today) })
    }
    val holiday = remember(today) {
        HolidayCalculator.nextOrOngoing(LegalHolidays.periods(), today)
    }
    val slides = remember(displays, holiday) {
        val user = DateCalculator.carouselList(displays).map { Slide.Event(it) }
        val hol = holiday?.let { Slide.Holiday(it) }
        when {
            hol != null && user.isEmpty() -> listOf(hol)
            hol != null -> user + hol
            else -> user
        }
    }


    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            NodTopBar(
                title = stringResource(R.string.app_name),
                actions = {
                    IconButton(onClick = onOpenSettings) {
                        Icon(
                            imageVector = Icons.Default.Settings,
                            contentDescription = stringResource(R.string.settings),
                        )
                    }
                },
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onOpenAdd,
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary,
                shape = NodFabShape,
            ) {
                Icon(Icons.Default.Add, contentDescription = stringResource(R.string.add_event))
            }
        },
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(
                    horizontal = HeroPageMarginH,
                    vertical = HeroPageMarginTop,
                ),
        ) {
            if (slides.isNotEmpty()) {
                val pagerState = rememberPagerState(pageCount = { slides.size })
                CarouselCard(
                    slides = slides,
                    pagerState = pagerState,
                    onClick = onOpenDetail,
                    onOpenHolidays = onOpenHolidays,
                )
                // Read currentPage inside Dots (or via derivedStateOf) so swipe
                // does not recompose the whole HomeScreen column.
                Dots(count = slides.size, pagerState = pagerState)
            }
            if (holiday != null) {
                HolidayMiniCard(
                    countdown = holiday,
                    modifier = Modifier.padding(top = 8.dp),
                    onClick = onOpenHolidays,
                )
            } else {
                // Keep the holiday entry even when the year has no more vacation blocks.
                HolidayEmptyCard(
                    modifier = Modifier.padding(top = 8.dp),
                    onClick = onOpenHolidays,
                )
            }
            if (displays.isEmpty()) {
                EmptyState(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                    onAdd = onOpenAdd,
                )
            } else {
                Text(
                    text = stringResource(R.string.all_events),
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontSize = 13.sp,
                    modifier = Modifier.padding(top = 12.dp, bottom = 4.dp),
                )
                LazyColumn(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                    contentPadding = PaddingValues(bottom = 96.dp),
                ) {
                    items(displays, key = { it.id }) { item ->
                        EventRow(
                            item = item,
                            onOpen = { onOpenDetail(item.id) },
                            onToggleStar = { repo.toggleStar(item.id) },
                        )
                        HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant, thickness = 1.dp)
                    }
                }
            }
        }
    }
}

private val DigitRun = Regex("\\d+")

@Composable
private fun dayCountAnnotated(label: String): AnnotatedString {
    val match = DigitRun.find(label)
    val digitStyle = SpanStyle(
        color = MaterialTheme.colorScheme.primary,
        fontWeight = FontWeight.Bold,
        fontFamily = NodDisplayCount.fontFamily,
        fontSize = NodDisplayCount.fontSize * 18f / 17f,
    )
    val plainStyle = SpanStyle(
        color = LocalNodBrand.current.n700,
        fontWeight = FontWeight.Normal,
        fontSize = NodDisplayCount.fontSize * 16f / 17f,
    )
    // System CJK bold so "今天" actually thickens (Barlow has no Han glyphs).
    val todayStyle = SpanStyle(
        color = MaterialTheme.colorScheme.primary,
        fontWeight = FontWeight.Bold,
        fontFamily = FontFamily.Default,
        fontSize = NodDisplayCount.fontSize * 16f / 17f,
    )
    return buildAnnotatedString {
        if (match == null) {
            withStyle(todayStyle) {
                append(label)
            }
        } else {
            withStyle(plainStyle) {
                append(label.substring(0, match.range.first))
            }
            withStyle(digitStyle) {
                append(match.value)
            }
            withStyle(plainStyle) {
                append(label.substring(match.range.last + 1))
            }
        }
    }
}

@Composable
private fun EmptyState(modifier: Modifier = Modifier, onAdd: () -> Unit) {
    Column(
        modifier = modifier.padding(horizontal = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Text(
            text = stringResource(R.string.empty_title),
            color = MaterialTheme.colorScheme.onSurface,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
        )
        Spacer(Modifier.height(8.dp))
        Text(
            text = stringResource(R.string.empty_desc),
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            fontSize = 13.sp,
        )
        Spacer(Modifier.height(24.dp))
        Text(
            text = stringResource(R.string.empty_action),
            color = MaterialTheme.colorScheme.onPrimary,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier
                .width(200.dp)
                .clip(MaterialTheme.shapes.medium)
                .background(MaterialTheme.colorScheme.primary)
                .clickable(onClick = onAdd)
                .padding(vertical = 14.dp),
            textAlign = androidx.compose.ui.text.style.TextAlign.Center,
        )
    }
}

@Composable
private fun HolidayEmptyCard(
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {},
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(NodCardShape)
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .border(1.dp, MaterialTheme.colorScheme.outlineVariant, NodCardShape)
            .clickable(onClick = onClick)
            .padding(horizontal = 12.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = stringResource(R.string.holiday_legal),
                color = MaterialTheme.colorScheme.outline,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
            )
            Spacer(Modifier.height(2.dp))
            Text(
                text = stringResource(R.string.holiday_none_left),
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }
        Text(
            text = stringResource(R.string.holiday_none_hint),
            color = MaterialTheme.colorScheme.outline,
            fontSize = 12.sp,
        )
    }
}

@Composable
private fun HolidayMiniCard(
    countdown: HolidayCountdown,
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {},
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(NodCardShape)
            .background(LocalNodBrand.current.holidayTint)
            .border(1.dp, LocalNodBrand.current.holidayEdge, NodCardShape)
            .clickable(onClick = onClick)
            .padding(horizontal = 12.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = stringResource(R.string.holiday_legal),
                color = MaterialTheme.colorScheme.primary,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
            )
            Spacer(Modifier.height(2.dp))
            Text(
                text = countdown.period.name,
                color = MaterialTheme.colorScheme.onSurface,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }
        Text(
            text = when (countdown.status) {
                HolidayStatus.ONGOING -> stringResource(R.string.holiday_ongoing)
                HolidayStatus.UPCOMING -> stringResource(
                    R.string.holiday_countdown,
                    countdown.daysUntilStart,
                )
            },
            style = NodDisplayCount,
            color = MaterialTheme.colorScheme.primary,
        )
    }
}

@Composable
private fun CarouselCard(
    slides: List<Slide>,
    pagerState: androidx.compose.foundation.pager.PagerState,
    onClick: (String) -> Unit,
    onOpenHolidays: () -> Unit,
) {
    if (slides.isEmpty()) return
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(NodCardShape),
    ) {
        HorizontalPager(
            state = pagerState,
            key = { index -> slides.getOrNull(index)?.let { slideKey(it) } ?: index },
            pageSpacing = 0.dp,
            modifier = Modifier.fillMaxWidth(),
        ) { page ->
            when (val item = slides[page]) {
                is Slide.Event -> EventSlide(item.display, onClick)
                is Slide.Holiday -> HolidaySlide(item.countdown, onOpenHolidays)
            }
        }
    }
}

private val HeroSuffixStyle = androidx.compose.ui.text.TextStyle(fontSize = 15.sp)

@Composable
private fun EventSlide(item: DayEventDisplay, onClick: (String) -> Unit) {
    val tag = when {
        item.repeatLabel != null && item.kind == EventKind.REPEAT -> item.repeatLabel ?: "重复"
        item.kind == EventKind.COUNTDOWN -> "倒数"
        item.kind == EventKind.ELAPSED -> "已过"
        else -> "今天"
    }
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .defaultMinSize(minHeight = HeroCardMinHeight)
            .clickable { onClick(item.id) }
            .padding(horizontal = HeroCardPadH, vertical = HeroCardPadV),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top,
    ) {
        Text(
            text = stringResource(R.string.next_prefix, tag),
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            fontSize = 11.sp,
            textAlign = TextAlign.Center,
        )
        Text(
            text = item.title,
            color = MaterialTheme.colorScheme.onSurface,
            fontSize = 17.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.fillMaxWidth(),
        )
        DayCountHero(
            value = item.days.toString(),
            suffix = stringResource(R.string.unit_day),
            suffixBottomPadding = 6,
            suffixStyle = HeroSuffixStyle,
        )
        Text(
            text = item.date,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            fontSize = 12.sp,
            textAlign = TextAlign.Center,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
    }
}

@Composable
private fun HolidaySlide(countdown: HolidayCountdown, onOpenHolidays: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(LocalNodBrand.current.holidayTint)
            .defaultMinSize(minHeight = HeroCardMinHeight)
            .clickable(onClick = onOpenHolidays)
            .padding(horizontal = HeroCardPadH, vertical = HeroCardPadV),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top,
    ) {
        Text(
            text = stringResource(R.string.holiday_next),
            color = MaterialTheme.colorScheme.primary,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
        )
        Text(
            text = countdown.period.name,
            color = MaterialTheme.colorScheme.onSurface,
            fontSize = 17.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.fillMaxWidth(),
        )
        if (countdown.status == HolidayStatus.ONGOING) {
            Text(
                text = stringResource(R.string.holiday_ongoing),
                color = MaterialTheme.colorScheme.primary,
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(top = 18.dp, bottom = 12.dp),
            )
        } else {
            DayCountHero(
                value = countdown.daysUntilStart.toString(),
                suffix = stringResource(R.string.unit_day),
                suffixBottomPadding = 6,
                suffixStyle = HeroSuffixStyle,
            )
        }
        Text(
            text = DateCalculator.format(countdown.period.start) +
                " · " +
                DateCalculator.format(countdown.period.end),
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            fontSize = 12.sp,
            textAlign = TextAlign.Center,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
    }
}

@Composable
private fun Dots(count: Int, pagerState: androidx.compose.foundation.pager.PagerState) {
    if (count <= 1) {
        Spacer(Modifier.height(12.dp))
        return
    }
    // Isolate pager reads so only this row recomposes on page change.
    val selected by remember { derivedStateOf { pagerState.currentPage } }
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 14.dp, bottom = 4.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        repeat(count) { i ->
            val active = i == selected
            val width by animateDpAsState(
                targetValue = if (active) 18.dp else 6.dp,
                animationSpec = spring(stiffness = Spring.StiffnessMediumLow),
                label = "dotW",
            )
            Box(
                modifier = Modifier
                    .padding(horizontal = 3.dp)
                    .height(6.dp)
                    .width(width)
                    .clip(RoundedCornerShape(3.dp))
                    .background(if (active) LocalNodBrand.current.n700 else MaterialTheme.colorScheme.outline)
            )
        }
    }
}

@Composable
private fun EventRow(
    item: DayEventDisplay,
    onOpen: () -> Unit,
    onToggleStar: () -> Unit,
) {
    val kindLabel = when (item.kind) {
        EventKind.COUNTDOWN -> "倒数"
        EventKind.ELAPSED -> "已过"
        EventKind.TODAY -> "今天"
        EventKind.REPEAT -> "重复"
    }
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(72.dp)
            .clickable(onClick = onOpen)
            .padding(horizontal = 12.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        IconButton(onClick = onToggleStar, modifier = Modifier.size(48.dp)) {
            Icon(
                imageVector = if (item.starred) Icons.Rounded.Star else Icons.Outlined.StarOutline,
                contentDescription = if (item.starred) {
                    stringResource(R.string.unstar)
                } else {
                    stringResource(R.string.star)
                },
                tint = if (item.starred) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline,
            )
        }
        Column(
            modifier = Modifier
                .weight(1f)
                .padding(end = 8.dp),
        ) {
            Text(
                text = item.title,
                color = MaterialTheme.colorScheme.onSurface,
                fontSize = 15.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            Text(
                text = buildString {
                    append(item.date)
                    if (item.repeatLabel != null) append(" · ").append(item.repeatLabel)
                    else append(" · ").append(kindLabel)
                },
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontSize = 12.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }
        Text(
            text = dayCountAnnotated(item.label),
            style = NodDisplayCount,
        )
    }
}

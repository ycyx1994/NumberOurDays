package com.numberourdays.app.ui.home

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGesturesAfterLongPress
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
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.input.pointer.pointerInput
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
import com.numberourdays.app.ui.theme.Accent
import com.numberourdays.app.ui.theme.N0
import com.numberourdays.app.ui.theme.N100
import com.numberourdays.app.ui.theme.N300
import com.numberourdays.app.ui.theme.N50
import com.numberourdays.app.ui.theme.N500
import com.numberourdays.app.ui.theme.N700
import com.numberourdays.app.ui.theme.N900
import com.numberourdays.app.ui.theme.NodCardShape
import com.numberourdays.app.ui.theme.NodDisplayCount
import com.numberourdays.app.ui.theme.NodDisplayNumber
import com.numberourdays.app.ui.theme.NodFabShape
import com.numberourdays.app.ui.theme.HeroCardMinHeight
import com.numberourdays.app.ui.theme.HeroCardPadH
import com.numberourdays.app.ui.theme.HeroCardPadV
import com.numberourdays.app.ui.theme.HeroPageMarginH
import com.numberourdays.app.ui.theme.HeroPageMarginTop
import com.numberourdays.app.ui.theme.HolidayEdge
import com.numberourdays.app.ui.theme.HolidayTint
import kotlin.math.absoluteValue

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

    // Drag reorder: accumulate delta during the gesture; commit once on drag end
    // so LazyColumn is not re-sorted mid-gesture (that cancels pointer input).
    var draggingId by remember { mutableStateOf<String?>(null) }
    var dragDeltaPx by remember { mutableFloatStateOf(0f) }
    var dragStartStarIndex by remember { mutableIntStateOf(-1) }
    val rowHeightPx = with(LocalDensity.current) { 72.dp.toPx() }

    fun onDragStart(id: String) {
        draggingId = id
        dragDeltaPx = 0f
        dragStartStarIndex = displays.filter { it.starred }.indexOfFirst { it.id == id }
    }

    fun onDragMove(deltaY: Float) {
        if (draggingId == null) return
        dragDeltaPx += deltaY
    }

    fun onDragEnd() {
        val id = draggingId
        val from = dragStartStarIndex
        val deltaY = dragDeltaPx
        draggingId = null
        dragDeltaPx = 0f
        dragStartStarIndex = -1
        if (id == null || from < 0) return
        val starred = displays.filter { it.starred }.map { it.id }.toMutableList()
        if (from !in starred.indices) return
        val to = DateCalculator.dragTargetIndex(from, starred.lastIndex, deltaY, rowHeightPx)
        if (to == from) return
        val moved = starred.removeAt(from)
        starred.add(to, moved)
        repo.reorderStars(starred)
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
                containerColor = Accent,
                contentColor = N0,
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
                    color = N500,
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
                        val isDraggingItem = draggingId == item.id
                        EventRow(
                            item = item,
                            onOpen = { onOpenDetail(item.id) },
                            onToggleStar = { repo.toggleStar(item.id) },
                            dragEnabled = item.starred,
                            dragging = isDraggingItem,
                            dragOffsetPx = if (isDraggingItem) dragDeltaPx else 0f,
                            onDragStart = { onDragStart(item.id) },
                            onDrag = { dy -> onDragMove(dy) },
                            onDragEnd = { onDragEnd() },
                        )
                        if (!isDraggingItem) {
                            HorizontalDivider(color = N100, thickness = 1.dp)
                        }
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
        color = Accent,
        fontWeight = FontWeight.Bold,
        fontFamily = NodDisplayCount.fontFamily,
        fontSize = NodDisplayCount.fontSize * 18f / 17f,
    )
    val plainStyle = SpanStyle(
        color = N700,
        fontWeight = FontWeight.Normal,
        fontSize = NodDisplayCount.fontSize * 16f / 17f,
    )
    // System CJK bold so "今天" actually thickens (Barlow has no Han glyphs).
    val todayStyle = SpanStyle(
        color = Accent,
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
            color = N900,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
        )
        Spacer(Modifier.height(8.dp))
        Text(
            text = stringResource(R.string.empty_desc),
            color = N500,
            fontSize = 13.sp,
        )
        Spacer(Modifier.height(24.dp))
        Text(
            text = stringResource(R.string.empty_action),
            color = N0,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier
                .width(200.dp)
                .clip(MaterialTheme.shapes.medium)
                .background(Accent)
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
            .background(HolidayTint)
            .border(1.dp, HolidayEdge, NodCardShape)
            .clickable(onClick = onClick)
            .padding(horizontal = 12.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = stringResource(R.string.holiday_legal),
                color = Accent,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
            )
            Spacer(Modifier.height(2.dp))
            Text(
                text = stringResource(R.string.holiday_none_left),
                color = N900,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }
        Text(
            text = stringResource(R.string.holiday_none_hint),
            color = Accent,
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
            .background(HolidayTint)
            .border(1.dp, HolidayEdge, NodCardShape)
            .clickable(onClick = onClick)
            .padding(horizontal = 12.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = stringResource(R.string.holiday_legal),
                color = Accent,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
            )
            Spacer(Modifier.height(2.dp))
            Text(
                text = countdown.period.name,
                color = N900,
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
            color = Accent,
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
            .background(N50)
            .defaultMinSize(minHeight = HeroCardMinHeight)
            .clickable { onClick(item.id) }
            .padding(horizontal = HeroCardPadH, vertical = HeroCardPadV),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top,
    ) {
        Text(
            text = stringResource(R.string.next_prefix, tag),
            color = N500,
            fontSize = 11.sp,
            textAlign = TextAlign.Center,
        )
        Text(
            text = item.title,
            color = N900,
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
            color = N500,
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
            .background(HolidayTint)
            .defaultMinSize(minHeight = HeroCardMinHeight)
            .clickable(onClick = onOpenHolidays)
            .padding(horizontal = HeroCardPadH, vertical = HeroCardPadV),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top,
    ) {
        Text(
            text = stringResource(R.string.holiday_next),
            color = Accent,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
        )
        Text(
            text = countdown.period.name,
            color = N900,
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
                color = Accent,
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
            color = N500,
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
                    .background(if (active) N700 else N300)
            )
        }
    }
}

@Composable
private fun EventRow(
    item: DayEventDisplay,
    onOpen: () -> Unit,
    onToggleStar: () -> Unit,
    dragEnabled: Boolean,
    dragging: Boolean = false,
    dragOffsetPx: Float = 0f,
    onDragStart: () -> Unit = {},
    onDrag: (Float) -> Unit,
    onDragEnd: () -> Unit,
) {
    val kindLabel = when (item.kind) {
        EventKind.COUNTDOWN -> "倒数"
        EventKind.ELAPSED -> "已过"
        EventKind.TODAY -> "今天"
        EventKind.REPEAT -> "重复"
    }
    // Paper-card lift: one scalar (0..1) drives scale/surface/border/inset.
    // Five parallel animate*AsState engines recomposed every list row on start/end.
    val dragShape = NodCardShape
    val lift by animateFloatAsState(
        targetValue = if (dragging) 1f else 0f,
        animationSpec = tween(durationMillis = 140),
        label = "dragLift",
    )
    val liftScale = 1f + 0.015f * lift
    val liftSurface = lerp(Color.Transparent, N50, lift)
    val liftBorder = lerp(Color.Transparent, N100, lift)
    val liftInset = 2.dp * lift
    val liftPad = 10.dp * lift
    val showLift = lift > 0f
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(72.dp)
            .padding(horizontal = liftInset)
            .graphicsLayer {
                translationY = dragOffsetPx
                if (showLift) {
                    scaleX = liftScale
                    scaleY = liftScale
                    shape = dragShape
                    clip = dragging
                }
            }
            .then(
                if (showLift) {
                    Modifier
                        .background(color = liftSurface, shape = dragShape)
                        .border(1.dp, liftBorder, dragShape)
                } else {
                    Modifier
                }
            )
            .then(
                if (dragEnabled) {
                    Modifier.pointerInput(item.id) {
                        detectDragGesturesAfterLongPress(
                            onDragStart = { onDragStart() },
                            onDragEnd = { onDragEnd() },
                            onDragCancel = { onDragEnd() },
                            onDrag = { change, amount ->
                                change.consume()
                                onDrag(amount.y)
                            },
                        )
                    }
                } else {
                    Modifier
                }
            )
            .clickable(onClick = onOpen)
            .padding(horizontal = liftPad, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        IconButton(onClick = onToggleStar, modifier = Modifier.size(40.dp)) {
            Icon(
                imageVector = if (item.starred) Icons.Rounded.Star else Icons.Outlined.StarOutline,
                contentDescription = if (item.starred) {
                    stringResource(R.string.unstar)
                } else {
                    stringResource(R.string.star)
                },
                tint = if (item.starred) Accent else N300,
            )
        }
        Column(
            modifier = Modifier
                .weight(1f)
                .padding(end = 8.dp),
        ) {
            Text(
                text = item.title,
                color = N900,
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
                color = N500,
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

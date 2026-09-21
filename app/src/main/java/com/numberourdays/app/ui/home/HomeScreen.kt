package com.numberourdays.app.ui.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.DragHandle
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
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.numberourdays.app.App
import com.numberourdays.app.R
import com.numberourdays.app.data.DayEventDisplay
import com.numberourdays.app.data.EventKind
import com.numberourdays.app.domain.DateCalculator
import com.numberourdays.app.ui.components.NodTopBar
import com.numberourdays.app.ui.theme.Accent
import com.numberourdays.app.ui.theme.N0
import com.numberourdays.app.ui.theme.N300
import com.numberourdays.app.ui.theme.N50
import com.numberourdays.app.ui.theme.N500
import com.numberourdays.app.ui.theme.N700
import com.numberourdays.app.ui.theme.N900
import kotlin.math.roundToInt

@Composable
fun HomeScreen(
    onOpenDetail: (String) -> Unit,
    onOpenSettings: () -> Unit,
    onOpenAdd: () -> Unit,
) {
    val repo = App.repository
    val events by repo.events.collectAsState()
    val displays = remember(events) {
        DateCalculator.sortEvents(events.map { DateCalculator.computeDisplay(it) })
    }
    val slides = remember(displays) { DateCalculator.carouselList(displays) }

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
            ) {
                Icon(Icons.Default.Add, contentDescription = stringResource(R.string.add_event))
            }
        },
    ) { padding ->
        if (displays.isEmpty()) {
            EmptyState(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                onAdd = onOpenAdd,
            )
            return@Scaffold
        }

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 96.dp),
        ) {
            item(key = "carousel") {
                val pagerState = rememberPagerState(pageCount = { slides.size.coerceAtLeast(1) })
                CarouselCard(slides = slides, pagerState = pagerState, onClick = onOpenDetail)
                Dots(count = slides.size, selected = pagerState.currentPage)
                Text(
                    text = stringResource(R.string.all_events),
                    color = N500,
                    fontSize = 13.sp,
                    modifier = Modifier.padding(top = 12.dp, bottom = 4.dp),
                )
            }
            items(displays, key = { it.id }) { item ->
                EventRow(
                    item = item,
                    onOpen = { onOpenDetail(item.id) },
                    onToggleStar = { repo.toggleStar(item.id) },
                    dragEnabled = item.starred,
                    dragging = draggingId == item.id,
                    dragOffsetPx = if (draggingId == item.id) dragDeltaPx else 0f,
                    onDragStart = { onDragStart(item.id) },
                    onDrag = { dy -> onDragMove(dy) },
                    onDragEnd = { onDragEnd() },
                )
                HorizontalDivider(color = N50, thickness = 1.dp)
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
private fun CarouselCard(
    slides: List<DayEventDisplay>,
    pagerState: androidx.compose.foundation.pager.PagerState,
    onClick: (String) -> Unit,
) {
    if (slides.isEmpty()) return
    HorizontalPager(
        state = pagerState,
        modifier = Modifier
            .fillMaxWidth()
            .height(220.dp),
    ) { page ->
        val item = slides[page]
        val tag = when {
            item.repeatLabel != null && item.kind == EventKind.REPEAT -> item.repeatLabel
            item.kind == EventKind.COUNTDOWN -> "倒数"
            item.kind == EventKind.ELAPSED -> "已过"
            else -> "今天"
        }
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(2.dp)
                .clip(MaterialTheme.shapes.large)
                .background(N50)
                .clickable { onClick(item.id) }
                .padding(16.dp),
        ) {
            Text(stringResource(R.string.next_prefix, tag), color = N500, fontSize = 12.sp)
            Spacer(Modifier.height(4.dp))
            Text(item.title, color = N900, fontSize = 16.sp, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(8.dp))
            Row(verticalAlignment = Alignment.Bottom) {
                Text(
                    text = item.days.toString(),
                    color = Accent,
                    fontSize = 64.sp,
                    fontWeight = FontWeight.Bold,
                )
                Spacer(Modifier.width(6.dp))
                Text(
                    text = stringResource(R.string.unit_day),
                    color = N700,
                    fontSize = 16.sp,
                    modifier = Modifier.padding(bottom = 10.dp),
                )
            }
            Spacer(Modifier.height(8.dp))
            Text(
                text = buildString {
                    append(item.date)
                    if (item.repeatLabel != null) append(" · ").append(item.repeatLabel)
                    else append(" · ").append(item.label)
                },
                color = N500,
                fontSize = 12.sp,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
            )
        }
    }
}

@Composable
private fun Dots(count: Int, selected: Int) {
    if (count <= 1) {
        Spacer(Modifier.height(16.dp))
        return
    }
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(16.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        repeat(count) { i ->
            Box(
                modifier = Modifier
                    .padding(horizontal = 3.dp)
                    .size(6.dp)
                    .clip(CircleShape)
                    .background(if (i == selected) Accent else N300)
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
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(72.dp)
            .graphicsLayer {
                translationY = dragOffsetPx
                if (dragging) {
                    alpha = 0.92f
                    shadowElevation = 8f
                }
            }
            .clickable(onClick = onOpen)
            .padding(vertical = 12.dp),
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
        Text(item.label, color = N700, fontSize = 14.sp)
        if (dragEnabled) {
            // Dedicated handle: drag starts immediately (no long-press), outside IconButton
            // so click/ripple handling cannot steal the drag gesture.
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .pointerInput(item.id) {
                        detectDragGestures(
                            onDragStart = { onDragStart() },
                            onDragEnd = { onDragEnd() },
                            onDragCancel = { onDragEnd() },
                            onDrag = { change, amount ->
                                change.consume()
                                onDrag(amount.y)
                            },
                        )
                    },
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    imageVector = Icons.Default.DragHandle,
                    contentDescription = stringResource(R.string.reorder_star),
                    tint = if (dragging) Accent else N500,
                )
            }
        } else {
            Spacer(Modifier.width(36.dp))
        }
    }
}

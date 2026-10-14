package com.numberourdays.app.ui.detail

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.outlined.StarOutline
import androidx.compose.material.icons.rounded.Star
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.IconButton
import androidx.compose.material3.TextButton
import androidx.compose.material3.AlertDialog
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.numberourdays.app.App
import com.numberourdays.app.R
import com.numberourdays.app.data.DurationUnit
import com.numberourdays.app.data.EventKind
import com.numberourdays.app.domain.DateCalculator
import com.numberourdays.app.ui.components.DayCountHero
import com.numberourdays.app.ui.components.NodTopBar
import com.numberourdays.app.ui.components.rememberCurrentDate
import com.numberourdays.app.ui.theme.NodCardShape
import com.numberourdays.app.ui.theme.NodFabShape
import com.numberourdays.app.ui.theme.HeroCardMinHeight
import com.numberourdays.app.ui.theme.HeroCardPadH
import com.numberourdays.app.ui.theme.HeroCardPadV
import com.numberourdays.app.ui.theme.HeroPageMarginH
import com.numberourdays.app.ui.theme.HeroPageMarginTop
import com.numberourdays.app.ui.theme.LocalNodBrand

@Composable
fun DetailScreen(
    eventId: String,
    onBack: () -> Unit,
    onEdit: (String) -> Unit,
) {
    val repo = App.repository
    val events by repo.events.collectAsState()
    val event = events.find { it.id == eventId }
    val today = rememberCurrentDate()

    var unit by rememberSaveable { mutableStateOf(DurationUnit.DAY.name) }
    val unitEnum = DurationUnit.valueOf(unit)
    var showDeleteConfirm by rememberSaveable { mutableStateOf(false) }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            NodTopBar(
                title = event?.title?.takeIf { it.isNotBlank() } ?: stringResource(R.string.detail),
                onBack = onBack,
                actions = {
                    IconButton(onClick = { onEdit(eventId) }) {
                        Icon(
                            Icons.Default.Edit,
                            contentDescription = stringResource(R.string.edit),
                        )
                    }
                },
            )
        },
    ) { padding ->
        if (event == null) {
            Text(
                text = stringResource(R.string.event_missing),
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(24.dp),
            )
            return@Scaffold
        }

        val d = remember(event, today) { DateCalculator.computeDisplay(event, today) }
        val tag = when {
            d.repeatLabel != null && d.kind == EventKind.REPEAT -> d.repeatLabel.orEmpty()
            d.kind == EventKind.COUNTDOWN -> "倒数"
            d.kind == EventKind.ELAPSED -> "已过"
            else -> "今天"
        }

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = HeroPageMarginH, vertical = HeroPageMarginTop),
            ) {
                // Hero card — same chrome/content order as home carousel
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .defaultMinSize(minHeight = HeroCardMinHeight)
                        .clip(NodCardShape)
                        .background(MaterialTheme.colorScheme.surfaceVariant)
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
                        text = d.title,
                        color = MaterialTheme.colorScheme.onSurface,
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.fillMaxWidth(),
                    )

                    val shown = remember(d, today, unitEnum) {
                        val target = DateCalculator.detailTargetDate(d)
                        val breakdown = DateCalculator.convertDuration(today, target)
                        DateCalculator.formatDuration(breakdown, unitEnum)
                    }
                    val suffix = listOf(shown.unitLabel, shown.rest)
                        .filter { it.isNotBlank() }
                        .joinToString(" ")
                    DayCountHero(
                        value = shown.value.toString(),
                        suffix = suffix,
                        suffixBottomPadding = 6,
                        suffixStyle = androidx.compose.ui.text.TextStyle(fontSize = 15.sp),
                    )
                    // Date line — match home hero
                    Text(
                        text = d.date,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontSize = 12.sp,
                        textAlign = TextAlign.Center,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp),
                    horizontalArrangement = Arrangement.Center,
                ) {
                    UnitChip(stringResource(R.string.unit_day), unitEnum == DurationUnit.DAY) {
                        unit = DurationUnit.DAY.name
                    }
                    Spacer(Modifier.width(6.dp))
                    UnitChip(stringResource(R.string.unit_month), unitEnum == DurationUnit.MONTH) {
                        unit = DurationUnit.MONTH.name
                    }
                    Spacer(Modifier.width(6.dp))
                    UnitChip(stringResource(R.string.unit_year), unitEnum == DurationUnit.YEAR) {
                        unit = DurationUnit.YEAR.name
                    }
                }

                Spacer(Modifier.height(10.dp))

                Column(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .verticalScroll(rememberScrollState())
                        .padding(bottom = 96.dp),
                ) {
                    MetaRow(stringResource(R.string.label_repeat), d.repeatLabel ?: stringResource(R.string.not_repeat))
                    d.nextDate?.let { MetaRow(stringResource(R.string.label_next), it) }

                    val elapsedShown = remember(d, today, unitEnum) {
                        val elapsedBd = DateCalculator.convertDuration(
                            DateCalculator.parse(d.date) ?: today,
                            today,
                        )
                        DateCalculator.formatDuration(elapsedBd, unitEnum)
                    }
                    val elapsedText = listOf("${elapsedShown.value} ${elapsedShown.unitLabel}", elapsedShown.rest)
                        .filter { it.isNotBlank() }
                        .joinToString(" ")
                    MetaRow(
                        label = stringResource(R.string.label_elapsed),
                        value = elapsedText,
                    )
                    MetaRow(stringResource(R.string.label_status), d.label)
                    MetaRow(
                        stringResource(R.string.label_remind),
                        if (d.remindEnabled) {
                            stringResource(R.string.remind_on, d.remindHour, d.remindMinute)
                        } else {
                            stringResource(R.string.remind_off)
                        },
                    )
                    if (!d.note.isNullOrBlank()) {
                        Column(modifier = Modifier.padding(vertical = 14.dp)) {
                            Text(stringResource(R.string.label_note), color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 13.sp)
                            Spacer(Modifier.height(6.dp))
                            Text(d.note.orEmpty(), color = LocalNodBrand.current.n700, fontSize = 14.sp)
                        }
                        HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
                    }
                }

                // Delete footer (confirm first) — matches iOS detail
                Spacer(Modifier.height(16.dp))
                OutlinedButton(
                    onClick = { showDeleteConfirm = true },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = MaterialTheme.colorScheme.error,
                    ),
                ) {
                    Text(stringResource(R.string.delete))
                }
                Spacer(Modifier.height(24.dp))
            }
        }
    }

    if (showDeleteConfirm) {
        AlertDialog(
            onDismissRequest = { showDeleteConfirm = false },
            title = { Text(stringResource(R.string.delete)) },
            text = { Text(stringResource(R.string.delete_confirm_message)) },
            confirmButton = {
                TextButton(onClick = {
                    showDeleteConfirm = false
                    repo.remove(eventId)
                    onBack()
                }) {
                    Text(stringResource(R.string.delete), color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteConfirm = false }) {
                    Text(stringResource(R.string.cancel))
                }
            },
        )
    }
}

@Composable
private fun UnitChip(text: String, selected: Boolean, onClick: () -> Unit) {
    FilterChip(
        selected = selected,
        onClick = onClick,
        label = { Text(text, fontSize = 13.sp) },
        shape = RoundedCornerShape(8.dp),
        colors = FilterChipDefaults.filterChipColors(
            containerColor = MaterialTheme.colorScheme.background,
            labelColor = LocalNodBrand.current.n700,
            selectedContainerColor = MaterialTheme.colorScheme.primary,
            selectedLabelColor = MaterialTheme.colorScheme.onPrimary,
        ),
        border = FilterChipDefaults.filterChipBorder(
            borderColor = MaterialTheme.colorScheme.outline,
            selectedBorderColor = MaterialTheme.colorScheme.primary,
            enabled = true,
            selected = selected,
        ),
    )
}

@Composable
private fun MetaRow(
    label: String,
    value: String,
    trailing: @Composable () -> Unit = {},
) {
    Column {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(label, color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 13.sp)
            Spacer(Modifier.weight(1f))
            Text(value, color = MaterialTheme.colorScheme.onSurface, fontSize = 14.sp)
            trailing()
        }
        HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
    }
}

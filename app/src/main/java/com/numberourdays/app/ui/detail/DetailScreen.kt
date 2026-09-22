package com.numberourdays.app.ui.detail

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
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
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.numberourdays.app.App
import com.numberourdays.app.R
import com.numberourdays.app.data.DurationUnit
import com.numberourdays.app.data.EventKind
import com.numberourdays.app.domain.DateCalculator
import com.numberourdays.app.ui.components.NodTopBar
import com.numberourdays.app.ui.theme.Accent
import com.numberourdays.app.ui.theme.N0
import com.numberourdays.app.ui.theme.N100
import com.numberourdays.app.ui.theme.N300
import com.numberourdays.app.ui.theme.N50
import com.numberourdays.app.ui.theme.N500
import com.numberourdays.app.ui.theme.N700
import com.numberourdays.app.ui.theme.N900
import com.numberourdays.app.ui.theme.NodButtonShape
import com.numberourdays.app.ui.theme.NodCardShape
import com.numberourdays.app.ui.theme.NodDisplayNumber

@Composable
fun DetailScreen(
    eventId: String,
    onBack: () -> Unit,
    onEdit: (String) -> Unit,
) {
    val repo = App.repository
    val events by repo.events.collectAsState()
    val event = events.find { it.id == eventId }

    var unit by rememberSaveable { mutableStateOf(DurationUnit.DAY.name) }
    var elapsedUnit by rememberSaveable { mutableStateOf(DurationUnit.DAY.name) }
    val unitEnum = DurationUnit.valueOf(unit)
    val elapsedEnum = DurationUnit.valueOf(elapsedUnit)

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            NodTopBar(
                title = event?.title?.takeIf { it.isNotBlank() } ?: stringResource(R.string.detail),
                onBack = onBack,
            )
        },
    ) { padding ->
        if (event == null) {
            Text(
                text = stringResource(R.string.event_missing),
                color = N500,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(24.dp),
            )
            return@Scaffold
        }

        val d = DateCalculator.computeDisplay(event)
        val tag = d.repeatLabel ?: when (d.kind) {
            EventKind.COUNTDOWN -> "倒数"
            EventKind.ELAPSED -> "已经过"
            EventKind.TODAY -> "今天"
            EventKind.REPEAT -> "重复"
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 12.dp, vertical = 8.dp),
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(N50, NodCardShape)
                    .padding(horizontal = 16.dp, vertical = 10.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Text(tag, color = N500, fontSize = 11.sp, textAlign = TextAlign.Center)
                Text(
                    d.title,
                    color = N900,
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    maxLines = 1,
                    overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis,
                )
                if (d.repeatLabel != null && d.kind == EventKind.REPEAT) {
                    Text(d.repeatLabel, color = N500, fontSize = 12.sp, textAlign = TextAlign.Center)
                }

                val target = DateCalculator.detailTargetDate(d)
                val breakdown = DateCalculator.convertDuration(DateCalculator.today(), target)
                val shown = DateCalculator.formatDuration(breakdown, unitEnum)
                Row(
                    verticalAlignment = Alignment.Bottom,
                    horizontalArrangement = Arrangement.Center,
                ) {
                    Text(
                        text = shown.value.toString(),
                        color = Accent,
                        style = NodDisplayNumber,
                    )
                    Spacer(Modifier.width(4.dp))
                    Text(
                        text = shown.unitLabel,
                        color = N700,
                        fontSize = 14.sp,
                        modifier = Modifier.padding(bottom = 4.dp),
                    )
                    if (shown.rest.isNotEmpty()) {
                        Spacer(Modifier.width(4.dp))
                        Text(
                            text = shown.rest,
                            color = N700,
                            fontSize = 14.sp,
                            modifier = Modifier.padding(bottom = 4.dp),
                        )
                    }
                }
                Text(d.label, color = N500, fontSize = 12.sp, textAlign = TextAlign.Center)
            }

            // Unit switch lives outside the hero card
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

            MetaRow(stringResource(R.string.label_date), d.date)
            MetaRow(stringResource(R.string.label_repeat), d.repeatLabel ?: stringResource(R.string.not_repeat))
            d.nextDate?.let { MetaRow(stringResource(R.string.label_next), it) }

            val elapsedBd = DateCalculator.convertDuration(
                DateCalculator.parse(d.date) ?: DateCalculator.today(),
                DateCalculator.today(),
            )
            val elapsedShown = DateCalculator.formatDuration(elapsedBd, elapsedEnum)
            val elapsedText = listOf("${elapsedShown.value} ${elapsedShown.unitLabel}", elapsedShown.rest)
                .filter { it.isNotBlank() }
                .joinToString(" ")
            val elapsedUnitLabel = when (elapsedEnum) {
                DurationUnit.DAY -> stringResource(R.string.unit_day)
                DurationUnit.MONTH -> stringResource(R.string.unit_month)
                DurationUnit.YEAR -> stringResource(R.string.unit_year)
            }
            MetaRow(
                label = stringResource(R.string.label_elapsed),
                value = elapsedText,
                trailing = {
                    // Compact cycle: tap unit to rotate 天 → 月 → 年
                    Text(
                        text = elapsedUnitLabel,
                        color = Accent,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier
                            .padding(start = 8.dp)
                            .clip(RoundedCornerShape(6.dp))
                            .background(N50)
                            .clickable {
                                val order = listOf(DurationUnit.DAY, DurationUnit.MONTH, DurationUnit.YEAR)
                                elapsedUnit = order[(order.indexOf(elapsedEnum) + 1) % order.size].name
                            }
                            .padding(horizontal = 8.dp, vertical = 3.dp),
                    )
                },
            )
            MetaRow(stringResource(R.string.label_status), d.label)
            MetaRow(
                stringResource(R.string.label_remind),
                if (d.remindEnabled) stringResource(R.string.remind_on) else stringResource(R.string.remind_off),
            )
            if (!d.note.isNullOrBlank()) {
                Column(modifier = Modifier.padding(vertical = 14.dp)) {
                    Text(stringResource(R.string.label_note), color = N500, fontSize = 13.sp)
                    Spacer(Modifier.height(6.dp))
                    Text(d.note.orEmpty(), color = N700, fontSize = 14.sp)
                }
                HorizontalDivider(color = N100)
            }

            Spacer(Modifier.height(24.dp))
            Button(
                onClick = { onEdit(event.id) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(44.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = N50,
                    contentColor = N700,
                ),
                shape = NodButtonShape,
            ) {
                Icon(Icons.Default.Edit, contentDescription = null, modifier = Modifier.width(18.dp))
                Spacer(Modifier.width(8.dp))
                Text(stringResource(R.string.edit), fontSize = 14.sp)
            }

            Spacer(Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .clickable { repo.toggleStar(event.id) }
                        .padding(horizontal = 14.dp, vertical = 12.dp),
                ) {
                    Icon(
                        imageVector = if (d.starred) Icons.Rounded.Star else Icons.Outlined.StarOutline,
                        contentDescription = null,
                        tint = if (d.starred) Accent else N300,
                    )
                    Spacer(Modifier.width(6.dp))
                    Text(
                        text = stringResource(if (d.starred) R.string.starred else R.string.star),
                        color = N500,
                        fontSize = 13.sp,
                    )
                }
            }
            Spacer(Modifier.height(24.dp))
        }
    }
}

@Composable
private fun UnitChip(text: String, selected: Boolean, onClick: () -> Unit) {
    FilterChip(
        selected = selected,
        onClick = onClick,
        label = { Text(text, fontSize = 13.sp) },
        shape = RoundedCornerShape(topStart = 8.dp, bottomStart = 8.dp, topEnd = 8.dp, bottomEnd = 8.dp),
        colors = FilterChipDefaults.filterChipColors(
            containerColor = N0,
            labelColor = N700,
            selectedContainerColor = Accent,
            selectedLabelColor = N0,
        ),
        border = FilterChipDefaults.filterChipBorder(
            borderColor = N300,
            selectedBorderColor = Accent,
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
            Text(label, color = N500, fontSize = 13.sp)
            Spacer(Modifier.weight(1f))
            Text(value, color = N900, fontSize = 14.sp)
            trailing()
        }
        HorizontalDivider(color = N100)
    }
}

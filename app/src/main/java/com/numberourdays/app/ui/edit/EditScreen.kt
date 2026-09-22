package com.numberourdays.app.ui.edit

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.numberourdays.app.App
import com.numberourdays.app.R
import com.numberourdays.app.data.DayEvent
import com.numberourdays.app.data.EventKind
import com.numberourdays.app.data.RepeatUnit
import com.numberourdays.app.domain.DateCalculator
import com.numberourdays.app.notify.NotificationHelper
import com.numberourdays.app.ui.components.NodTopBar
import com.numberourdays.app.ui.theme.Accent
import com.numberourdays.app.ui.theme.ErrorRed
import com.numberourdays.app.ui.theme.N0
import com.numberourdays.app.ui.theme.N300
import com.numberourdays.app.ui.theme.N50
import com.numberourdays.app.ui.theme.N500
import com.numberourdays.app.ui.theme.N700
import com.numberourdays.app.ui.theme.N900
import com.numberourdays.app.ui.theme.NodButtonShape
import com.numberourdays.app.ui.theme.NodFieldShape
import java.util.Calendar
import java.util.Locale
import java.util.TimeZone

@Composable
private fun ProvideChineseDateLocale(content: @Composable () -> Unit) {
    val configuration = LocalConfiguration.current
    val zhConfig = remember(configuration) {
        android.content.res.Configuration(configuration).apply {
            setLocale(Locale.CHINA)
        }
    }
    CompositionLocalProvider(LocalConfiguration provides zhConfig) {
        content()
    }
}

private fun formatIsoDate(cal: Calendar): String =
    String.format(
        Locale.CHINA,
        "%04d-%02d-%02d",
        cal.get(Calendar.YEAR),
        cal.get(Calendar.MONTH) + 1,
        cal.get(Calendar.DAY_OF_MONTH),
    )

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditScreen(
    eventId: String?,
    onBack: () -> Unit,
    onSaved: () -> Unit,
) {
    val repo = App.repository
    val events by repo.events.collectAsState()
    val context = LocalContext.current
    val existing = eventId?.let { id -> events.find { it.id == id } }

    var title by rememberSaveable { mutableStateOf(existing?.title.orEmpty()) }
    var dateText by rememberSaveable { mutableStateOf(existing?.date ?: DateCalculator.format(DateCalculator.today())) }
    var note by rememberSaveable { mutableStateOf(existing?.note.orEmpty()) }
    var repeatOn by rememberSaveable {
        mutableStateOf(existing?.repeatUnit?.let { it != RepeatUnit.NONE } ?: false)
    }
    var repeatUnitName by rememberSaveable {
        mutableStateOf(
            existing?.repeatUnit?.takeIf { it != RepeatUnit.NONE }?.name ?: RepeatUnit.YEAR.name
        )
    }
    var repeatInterval by rememberSaveable { mutableStateOf((existing?.repeatInterval ?: 1).coerceIn(1, 999)) }
    var remindOn by rememberSaveable { mutableStateOf(existing?.remindEnabled ?: false) }
    var remindHour by rememberSaveable { mutableStateOf((existing?.remindHour ?: 9).toString()) }
    var remindMinute by rememberSaveable { mutableStateOf((existing?.remindMinute ?: 0).toString()) }
    var errorText by rememberSaveable { mutableStateOf<String?>(null) }
    var showDelete by rememberSaveable { mutableStateOf(false) }
    var showDatePicker by rememberSaveable { mutableStateOf(false) }

    val repeatUnit = if (repeatOn) RepeatUnit.valueOf(repeatUnitName) else RepeatUnit.NONE
    val preview = if (title.isNotBlank() && DateCalculator.isValidDate(dateText)) {
        val draft = DayEvent(
            id = "preview",
            title = title,
            date = dateText,
            repeatUnit = repeatUnit,
            repeatInterval = repeatInterval,
        )
        DateCalculator.computeDisplay(draft)
    } else null

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            NodTopBar(
                title = stringResource(if (existing != null) R.string.edit else R.string.add),
                onBack = onBack,
            )
        },
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 8.dp),
        ) {
            Text(stringResource(R.string.title_label), color = N500, fontSize = 12.sp)
            Spacer(Modifier.height(6.dp))
            OutlinedTextField(
                value = title,
                onValueChange = { if (it.length <= 30) title = it },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                placeholder = { Text(stringResource(R.string.title_hint), color = N300) },
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                shape = NodFieldShape,
                colors = nodFieldColors(),
            )

            Spacer(Modifier.height(16.dp))
            Text(stringResource(R.string.date_label), color = N500, fontSize = 12.sp)
            Spacer(Modifier.height(6.dp))
            // ISO date field with calendar affordance
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(NodFieldShape)
                    .background(N50)
                    .border(1.dp, N300, NodFieldShape)
                    .clickable { showDatePicker = true }
                    .padding(horizontal = 12.dp, vertical = 14.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = dateText,
                    color = N900,
                    fontSize = 15.sp,
                    modifier = Modifier.weight(1f),
                )
                Icon(
                    imageVector = Icons.Outlined.CalendarMonth,
                    contentDescription = stringResource(R.string.choose_date),
                    tint = N500,
                    modifier = Modifier.size(20.dp),
                )
            }

            if (preview != null) {
                Spacer(Modifier.height(8.dp))
                Text(
                    text = if (preview.kind == EventKind.REPEAT) {
                        "距离「$title」${preview.label}"
                    } else preview.label,
                    color = Accent,
                    fontSize = 13.sp,
                )
            }

            Spacer(Modifier.height(8.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = stringResource(R.string.repeat_label),
                    color = N900,
                    fontSize = 15.sp,
                    modifier = Modifier.weight(1f),
                )
                Switch(
                    checked = repeatOn,
                    onCheckedChange = { repeatOn = it },
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = N0,
                        checkedTrackColor = Accent,
                        uncheckedThumbColor = N0,
                        uncheckedTrackColor = N300,
                    ),
                )
            }

            if (repeatOn) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(8.dp))
                        .padding(12.dp),
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("每", color = N700, fontSize = 14.sp)
                        Spacer(Modifier.width(8.dp))
                        OutlinedButton(
                            onClick = { repeatInterval = (repeatInterval - 1).coerceAtLeast(1) },
                            modifier = Modifier.height(36.dp),
                            shape = NodButtonShape,
                        ) { Text("−") }
                        Text(
                            text = repeatInterval.toString(),
                            color = N900,
                            fontSize = 16.sp,
                            modifier = Modifier
                                .width(40.dp)
                                .padding(horizontal = 4.dp),
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                        )
                        OutlinedButton(
                            onClick = { repeatInterval = (repeatInterval + 1).coerceAtMost(999) },
                            modifier = Modifier.height(36.dp),
                            shape = NodButtonShape,
                        ) { Text("+") }
                    }
                    Spacer(Modifier.height(12.dp))
                    Row(horizontalArrangement = androidx.compose.foundation.layout.Arrangement.spacedBy(6.dp)) {
                        listOf(
                            RepeatUnit.DAY to stringResource(R.string.unit_day),
                            RepeatUnit.WEEK to "周",
                            RepeatUnit.MONTH to stringResource(R.string.unit_month),
                            RepeatUnit.YEAR to stringResource(R.string.unit_year),
                        ).forEach { (unit, label) ->
                            FilterChip(
                                selected = repeatUnitName == unit.name,
                                onClick = { repeatUnitName = unit.name },
                                label = { Text(label, fontSize = 13.sp) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = Accent,
                                    selectedLabelColor = N0,
                                    containerColor = N0,
                                    labelColor = N700,
                                ),
                            )
                        }
                    }
                }
            }

            Spacer(Modifier.height(4.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = stringResource(R.string.remind_label),
                    color = N900,
                    fontSize = 15.sp,
                    modifier = Modifier.weight(1f),
                )
                Switch(
                    checked = remindOn,
                    onCheckedChange = { remindOn = it },
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = N0,
                        checkedTrackColor = Accent,
                        uncheckedThumbColor = N0,
                        uncheckedTrackColor = N300,
                    ),
                )
            }
            Text(stringResource(R.string.remind_hint), color = N500, fontSize = 11.sp)
            if (remindOn) {
                Spacer(Modifier.height(8.dp))
                Text(stringResource(R.string.remind_time_label), color = N500, fontSize = 12.sp)
                Spacer(Modifier.height(6.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    listOf(
                        Triple(9, 0, stringResource(R.string.remind_time_9)),
                        Triple(12, 0, stringResource(R.string.remind_time_12)),
                        Triple(20, 0, stringResource(R.string.remind_time_20)),
                    ).forEach { (h, m, label) ->
                        val selected = remindHour == h.toString() && remindMinute == m.toString()
                        FilterChip(
                            selected = selected,
                            onClick = {
                                remindHour = h.toString()
                                remindMinute = m.toString()
                            },
                            label = { Text(label, fontSize = 13.sp) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = Accent,
                                selectedLabelColor = N0,
                                containerColor = N0,
                                labelColor = N700,
                            ),
                        )
                    }
                }
            }

            Spacer(Modifier.height(16.dp))
            Text(stringResource(R.string.note_label), color = N500, fontSize = 12.sp)
            Spacer(Modifier.height(6.dp))
            OutlinedTextField(
                value = note,
                onValueChange = { if (it.length <= 100) note = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(100.dp),
                placeholder = { Text(stringResource(R.string.note_hint), color = N300) },
                shape = NodFieldShape,
                colors = nodFieldColors(),
            )

            errorText?.let {
                Spacer(Modifier.height(6.dp))
                Text(it, color = ErrorRed, fontSize = 12.sp)
            }

            Spacer(Modifier.height(20.dp))
            Button(
                onClick = {
                    val err = DateCalculator.validateForm(title, dateText, repeatUnit, repeatInterval)
                    if (err != null) {
                        errorText = err
                        return@Button
                    }
                    errorText = null
                    val now = System.currentTimeMillis()
                    val payload = DayEvent(
                        id = existing?.id ?: repo.createId(),
                        title = title.trim(),
                        date = dateText,
                        note = note.trim().ifEmpty { null },
                        starred = existing?.starred ?: false,
                        starRank = existing?.starRank,
                        repeatUnit = repeatUnit,
                        repeatInterval = if (repeatOn) repeatInterval else 1,
                        remindEnabled = remindOn,
                        remindHour = remindHour.toIntOrNull()?.coerceIn(0, 23) ?: 9,
                        remindMinute = remindMinute.toIntOrNull()?.coerceIn(0, 59) ?: 0,
                        createdAt = existing?.createdAt ?: now,
                        updatedAt = now,
                    )
                    repo.upsert(payload)
                    if (remindOn && !NotificationHelper.canPost(context)) {
                        Toast.makeText(
                            context,
                            R.string.notif_permission_needed,
                            Toast.LENGTH_LONG,
                        ).show()
                    }
                    onSaved()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(44.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Accent, contentColor = N0),
                shape = NodButtonShape,
            ) {
                Text(stringResource(R.string.save), fontSize = 14.sp)
            }

            if (existing != null) {
                Spacer(Modifier.height(16.dp))
                OutlinedButton(
                    onClick = { showDelete = true },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(44.dp),
                    shape = NodButtonShape,
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = ErrorRed),
                ) {
                    Text(stringResource(R.string.delete), fontSize = 14.sp)
                }
            }
            Spacer(Modifier.height(40.dp))
        }
    }

    if (showDelete && existing != null) {
        AlertDialog(
            onDismissRequest = { showDelete = false },
            title = { Text("删除纪念日") },
            text = { Text("确定删除「${existing.title}」？") },
            confirmButton = {
                TextButton(onClick = {
                    repo.remove(existing.id)
                    showDelete = false
                    onSaved()
                }) { Text(stringResource(R.string.delete), color = ErrorRed) }
            },
            dismissButton = {
                TextButton(onClick = { showDelete = false }) { Text("取消") }
            },
        )
    }

    if (showDatePicker) {
        val parsed = DateCalculator.parse(dateText) ?: DateCalculator.today()
        val utc = TimeZone.getTimeZone("UTC")
        val initialMillis = remember(parsed) {
            Calendar.getInstance(utc).apply {
                clear()
                set(parsed.year, parsed.monthValue - 1, parsed.dayOfMonth)
            }.timeInMillis
        }
        val datePickerState = rememberDatePickerState(
            initialSelectedDateMillis = initialMillis,
            yearRange = 1900..2100,
        )
        val selectedIso = datePickerState.selectedDateMillis?.let { millis ->
            formatIsoDate(Calendar.getInstance(utc).apply { timeInMillis = millis })
        } ?: dateText

        ProvideChineseDateLocale {
            // No chrome title — DatePicker alone is enough
            DatePickerDialog(
                onDismissRequest = { showDatePicker = false },
                confirmButton = {
                    TextButton(onClick = {
                        dateText = selectedIso
                        showDatePicker = false
                    }) { Text("确定", color = Accent) }
                },
                dismissButton = {
                    TextButton(onClick = { showDatePicker = false }) { Text("取消") }
                },
            ) {
                DatePicker(
                    state = datePickerState,
                    showModeToggle = false,
                )
            }
        }
    }
}

@Composable
private fun nodFieldColors() = OutlinedTextFieldDefaults.colors(
    focusedContainerColor = N0,
    unfocusedContainerColor = N0,
    disabledContainerColor = N0,
    focusedBorderColor = Accent,
    unfocusedBorderColor = N300,
    focusedTextColor = N900,
    unfocusedTextColor = N900,
    cursorColor = Accent,
)

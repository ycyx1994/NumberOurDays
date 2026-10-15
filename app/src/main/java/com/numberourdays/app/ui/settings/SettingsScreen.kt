package com.numberourdays.app.ui.settings

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.numberourdays.app.App
import com.numberourdays.app.R
import com.numberourdays.app.data.BackupTransfer
import com.numberourdays.app.data.DayEvent
import com.numberourdays.app.data.DayEventCodec
import com.numberourdays.app.notify.NotificationHelper
import com.numberourdays.app.ui.components.NodTopBar
import androidx.compose.ui.graphics.Color
import com.numberourdays.app.ui.theme.LocalNodBrand

@Composable
fun SettingsScreen(onBack: () -> Unit) {
    val repo = App.repository
    val events by repo.events.collectAsState()
    val context = LocalContext.current
    var importText by rememberSaveable { mutableStateOf("") }
    var message by rememberSaveable { mutableStateOf<String?>(null) }
    var confirmReplace by rememberSaveable { mutableStateOf(false) }
    var confirmClear by rememberSaveable { mutableStateOf(false) }
    var pasteAdvanced by rememberSaveable { mutableStateOf(false) }
    var showDebug by rememberSaveable { mutableStateOf(false) }
    var titleClicks by rememberSaveable { mutableStateOf(0) }
    var pendingFileImport by remember { mutableStateOf<List<DayEvent>?>(null) }
    var pendingFileImportNote by remember { mutableStateOf<String?>(null) }
    var showFileImportConfirm by remember { mutableStateOf(false) }

    val exportFileLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.CreateDocument("application/json"),
    ) { uri ->
        if (uri == null) return@rememberLauncherForActivityResult
        val snapshot = repo.loadEvents()
        if (snapshot.isEmpty()) {
            Toast.makeText(context, R.string.nothing_export, Toast.LENGTH_SHORT).show()
            return@rememberLauncherForActivityResult
        }
        val text = DayEventCodec.serialize(snapshot)
        if (BackupTransfer.writeText(context, uri, text)) {
            message = context.getString(R.string.exported_to_file, snapshot.size)
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
        } else {
            message = context.getString(R.string.export_failed)
        }
    }

    val importFileLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument(),
    ) { uri ->
        if (uri == null) return@rememberLauncherForActivityResult
        val text = BackupTransfer.readText(context, uri)
        if (text == null) {
            message = context.getString(R.string.read_file_failed)
            return@rememberLauncherForActivityResult
        }
        val (evts, note) = DayEventCodec.parseBackup(text)
        if (evts.isEmpty()) {
            message = note ?: context.getString(R.string.import_file_empty)
            return@rememberLauncherForActivityResult
        }
        pendingFileImport = evts
        pendingFileImportNote = note
        showFileImportConfirm = true
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            NodTopBar(stringResource(R.string.settings), onBack = onBack)
        },
    ) { padding ->
        Column(
            Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 4.dp),
        ) {
            SectionLabel(stringResource(R.string.data_section))
            Text(
                stringResource(R.string.local_events_value, events.size),
                color = MaterialTheme.colorScheme.onSurface,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .padding(vertical = 10.dp)
                    .clickable {
                        titleClicks++
                        if (titleClicks >= 5) {
                            showDebug = true
                            titleClicks = 0
                        }
                    },
            )
            SectionLabel(stringResource(R.string.backup_section))
            Text(stringResource(R.string.backup_desc), color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 12.sp)
            ActionPair(
                stringResource(R.string.export_file),
                {
                    if (events.isEmpty()) {
                        Toast.makeText(context, R.string.nothing_export, Toast.LENGTH_SHORT).show()
                        return@ActionPair
                    }
                    exportFileLauncher.launch(BackupTransfer.defaultBackupFileName())
                },
                stringResource(R.string.export_clip),
                {
                    if (events.isEmpty()) {
                        Toast.makeText(context, R.string.nothing_export, Toast.LENGTH_SHORT).show()
                        return@ActionPair
                    }
                    val cm = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                    cm.setPrimaryClip(ClipData.newPlainText("NumberOurDays", DayEventCodec.serialize(events)))
                    message = context.getString(R.string.copied)
                    Toast.makeText(context, R.string.copied, Toast.LENGTH_SHORT).show()
                },
                greenPair = true,
            )
            SectionLabel(stringResource(R.string.import_section))
            ActionPair(
                stringResource(R.string.import_from_file),
                { importFileLauncher.launch(arrayOf("application/json", "text/plain", "*/*")) },
                stringResource(if (pasteAdvanced) R.string.import_advanced_collapse else R.string.import_advanced_expand),
                { pasteAdvanced = !pasteAdvanced },
            )
            if (pasteAdvanced) {
                OutlinedTextField(
                    importText, { importText = it },
                    Modifier.fillMaxWidth().height(100.dp),
                    placeholder = {
                        Text(stringResource(R.string.paste_backup), color = MaterialTheme.colorScheme.outline, fontSize = 12.sp)
                    },
                    shape = RoundedCornerShape(12.dp),
                    colors = fieldColors(),
                )
                Row(Modifier.padding(top = 8.dp)) {
                    TextButton(onClick = {
                        val (evts, note) = DayEventCodec.parseBackup(importText)
                        if (evts.isEmpty()) {
                            message = note ?: context.getString(R.string.import_file_empty)
                            return@TextButton
                        }
                        repo.saveAll(DayEventCodec.merge(repo.loadEvents(), evts))
                        importText = ""
                        message = context.getString(R.string.import_file_merged, repo.loadEvents().size, note?.let { "，$it" } ?: "")
                    }) { Text(stringResource(R.string.import_merge), color = MaterialTheme.colorScheme.primary) }
                    Spacer(Modifier.weight(1f))
                    TextButton(onClick = { confirmReplace = true }) {
                        Text(stringResource(R.string.import_replace), color = MaterialTheme.colorScheme.error)
                    }
                }
            }
            if (showDebug) {
                SectionLabel("调试")
                FullButton(stringResource(R.string.test_notification), muted = true) {
                    val sample = events.firstOrNull()
                    if (sample == null) {
                        Toast.makeText(context, R.string.test_notification_empty, Toast.LENGTH_SHORT).show()
                        return@FullButton
                    }
                    NotificationHelper.showReminder(
                        context, sample.id, sample.title,
                        context.getString(R.string.test_notification_body),
                    )
                    message = context.getString(R.string.test_notification)
                }
                FullButton(stringResource(R.string.test_alarm_one_min), muted = true) {
                    com.numberourdays.app.notify.ReminderScheduler.scheduleDebugReminder(context)
                    val exact = com.numberourdays.app.notify.ReminderScheduler.canScheduleExact(context)
                    message = if (exact) {
                        context.getString(R.string.test_alarm_one_min_done)
                    } else {
                        context.getString(R.string.test_alarm_one_min_need_exact)
                    }
                    Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
                }
            }
            SectionLabel(stringResource(R.string.home_unit_title))
            Text(stringResource(R.string.home_unit_hint), color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 12.sp)
            Row(
                Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                val unit = HomeUnitPrefs.unit(context)
                listOf(
                    com.numberourdays.app.data.DurationUnit.DAY to stringResource(R.string.unit_day),
                    com.numberourdays.app.data.DurationUnit.MONTH to stringResource(R.string.unit_month),
                    com.numberourdays.app.data.DurationUnit.YEAR to stringResource(R.string.unit_year),
                ).forEach { (u, label) ->
                    val selected = unit == u
                    Text(
                        label,
                        color = if (selected) MaterialTheme.colorScheme.onPrimary else LocalNodBrand.current.n700,
                        fontSize = 13.sp,
                        modifier = Modifier
                            .clip(RoundedCornerShape(20.dp))
                            .background(if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant)
                            .clickable {
                                HomeUnitPrefs.setUnit(context, u)
                                message = label
                            }
                            .padding(horizontal = 14.dp, vertical = 8.dp),
                    )
                }
            }
            SectionLabel(stringResource(R.string.danger_section))
            FullButton(stringResource(R.string.clear_data), danger = true) { confirmClear = true }
            message?.let {
                Spacer(Modifier.height(12.dp))
                Text(it, color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 12.sp)
            }
            Spacer(Modifier.height(36.dp))
            Text(
                stringResource(R.string.footer),
                color = MaterialTheme.colorScheme.outline,
                fontSize = 11.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth(),
            )
            Spacer(Modifier.height(24.dp))
        }
    }

    if (showFileImportConfirm) {
        val incoming = pendingFileImport.orEmpty()
        val noteSuffix = pendingFileImportNote?.let { "，$it" } ?: ""
        AlertDialog(
            { showFileImportConfirm = false; pendingFileImport = null; pendingFileImportNote = null },
            title = { Text(stringResource(R.string.import_file_title)) },
            text = { Text(stringResource(R.string.import_file_message, incoming.size, noteSuffix)) },
            confirmButton = {
                Row {
                    TextButton(onClick = {
                        repo.saveAll(DayEventCodec.merge(repo.loadEvents(), incoming))
                        message = context.getString(R.string.import_file_merged, repo.loadEvents().size, noteSuffix)
                        showFileImportConfirm = false
                        pendingFileImport = null
                        pendingFileImportNote = null
                    }) { Text(stringResource(R.string.import_file_merge), color = MaterialTheme.colorScheme.primary) }
                    TextButton(onClick = {
                        repo.saveAll(incoming)
                        message = context.getString(R.string.import_file_replaced, incoming.size, noteSuffix)
                        showFileImportConfirm = false
                        pendingFileImport = null
                        pendingFileImportNote = null
                    }) { Text(stringResource(R.string.import_file_replace), color = MaterialTheme.colorScheme.error) }
                }
            },
            dismissButton = {
                TextButton(onClick = {
                    showFileImportConfirm = false
                    pendingFileImport = null
                    pendingFileImportNote = null
                }) { Text(stringResource(R.string.import_file_cancel)) }
            },
        )
    }
    if (confirmReplace) {
        AlertDialog(
            { confirmReplace = false },
            title = { Text("覆盖本地数据") },
            text = { Text("将删除本地不在备份中的事件，确定继续？") },
            confirmButton = {
                TextButton(onClick = {
                    val (evts, note) = DayEventCodec.parseBackup(importText)
                    if (evts.isEmpty()) {
                        message = note ?: context.getString(R.string.import_file_empty)
                        confirmReplace = false
                        return@TextButton
                    }
                    repo.saveAll(evts)
                    importText = ""
                    message = context.getString(R.string.import_file_replaced, evts.size, note?.let { "，$it" } ?: "")
                    confirmReplace = false
                }) { Text("覆盖", color = MaterialTheme.colorScheme.error) }
            },
            dismissButton = { TextButton(onClick = { confirmReplace = false }) { Text("取消") } },
        )
    }
    if (confirmClear) {
        AlertDialog(
            { confirmClear = false },
            title = { Text("清空本地数据") },
            text = { Text("将删除全部纪念日，且不可恢复。建议先导出备份。") },
            confirmButton = {
                TextButton(onClick = {
                    repo.clearAll()
                    message = context.getString(R.string.cleared)
                    confirmClear = false
                }) { Text("清空", color = MaterialTheme.colorScheme.error) }
            },
            dismissButton = { TextButton(onClick = { confirmClear = false }) { Text("取消") } },
        )
    }
}

@Composable
private fun SectionLabel(text: String) {
    Text(text, color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 12.sp, modifier = Modifier.padding(top = 12.dp, bottom = 6.dp))
}

private val ToneGreenBg = Color(0xFFE3ECE1)
private val ToneGreenFg = Color(0xFF5B7A5A)
private val ToneAccentBg = Color(0xFFF4E3DE)
private val ToneDangerBg = Color(0xFFF3E3E1)

@Composable
private fun ActionButton(
    label: String,
    modifier: Modifier = Modifier,
    primary: Boolean = false,
    green: Boolean = false,
    danger: Boolean = false,
    muted: Boolean = false,
    onClick: () -> Unit,
) {
    val bg = when {
        green -> ToneGreenBg
        danger -> ToneDangerBg
        primary -> ToneAccentBg
        else -> MaterialTheme.colorScheme.surfaceVariant
    }
    val fg = when {
        green -> ToneGreenFg
        danger -> MaterialTheme.colorScheme.error
        primary -> MaterialTheme.colorScheme.primary
        muted -> MaterialTheme.colorScheme.onSurfaceVariant
        else -> LocalNodBrand.current.n700
    }
    Row(
        modifier
            .height(48.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(bg)
            .clickable(onClick = onClick),
        Arrangement.Center,
        Alignment.CenterVertically,
    ) {
        Text(label, color = fg, fontSize = 14.sp, fontWeight = FontWeight.Medium)
    }
}

@Composable
private fun ActionPair(
    aLabel: String,
    onA: () -> Unit,
    bLabel: String,
    onB: () -> Unit,
    greenPair: Boolean = false,
) {
    Row(
        Modifier.fillMaxWidth().padding(vertical = 4.dp),
        Arrangement.spacedBy(10.dp),
    ) {
        if (greenPair) {
            ActionButton(aLabel, Modifier.weight(1f), green = true, onClick = onA)
            ActionButton(bLabel, Modifier.weight(1f), green = true, muted = true, onClick = onB)
        } else {
            ActionButton(aLabel, Modifier.weight(1f), primary = true, onClick = onA)
            ActionButton(bLabel, Modifier.weight(1f), onClick = onB)
        }
    }
}

@Composable
private fun FullButton(label: String, danger: Boolean = false, muted: Boolean = false, onClick: () -> Unit) {
    Row(
        Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(if (danger) ToneDangerBg else MaterialTheme.colorScheme.surfaceVariant)
            .clickable(onClick = onClick),
        Arrangement.Center,
        Alignment.CenterVertically,
    ) {
        Text(
            label,
            color = if (danger) MaterialTheme.colorScheme.error else if (muted) MaterialTheme.colorScheme.onSurfaceVariant else MaterialTheme.colorScheme.primary,
            fontSize = 15.sp,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.padding(vertical = 14.dp),
        )
    }
}

@Composable
private fun fieldColors() = OutlinedTextFieldDefaults.colors(
    focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
    unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
    disabledContainerColor = MaterialTheme.colorScheme.surfaceVariant,
    focusedBorderColor = MaterialTheme.colorScheme.primary,
    unfocusedBorderColor = MaterialTheme.colorScheme.outline,
    focusedTextColor = MaterialTheme.colorScheme.onSurface,
    unfocusedTextColor = MaterialTheme.colorScheme.onSurface,
    disabledTextColor = LocalNodBrand.current.n700,
    cursorColor = MaterialTheme.colorScheme.primary,
)

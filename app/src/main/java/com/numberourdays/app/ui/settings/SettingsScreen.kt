package com.numberourdays.app.ui.settings

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
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
import androidx.compose.ui.Modifier
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
import com.numberourdays.app.notify.NotificationHelper
import com.numberourdays.app.ui.components.NodTopBar
import com.numberourdays.app.ui.theme.Accent
import com.numberourdays.app.ui.theme.ErrorRed
import com.numberourdays.app.ui.theme.N0
import com.numberourdays.app.ui.theme.N100
import com.numberourdays.app.ui.theme.N300
import com.numberourdays.app.ui.theme.N50
import com.numberourdays.app.ui.theme.N500
import com.numberourdays.app.ui.theme.N700
import com.numberourdays.app.ui.theme.N900
import com.numberourdays.app.ui.theme.NodButtonShape
import com.numberourdays.app.ui.theme.NodFieldShape

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
    var pendingFileImport by remember { mutableStateOf<List<DayEvent>?>(null) }
    var pendingFileImportNote by remember { mutableStateOf<String?>(null) }
    var showFileImportConfirm by remember { mutableStateOf(false) }

    fun showMessage(text: String) {
        message = text
    }

    val exportFileLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.CreateDocument("application/json"),
    ) { uri ->
        if (uri == null) return@rememberLauncherForActivityResult
        val snapshot = repo.loadEvents()
        if (snapshot.isEmpty()) {
            Toast.makeText(context, R.string.nothing_export, Toast.LENGTH_SHORT).show()
            return@rememberLauncherForActivityResult
        }
        val text = repo.serialize(snapshot)
        if (BackupTransfer.writeText(context, uri, text)) {
            val msg = context.getString(R.string.exported_to_file, snapshot.size)
            showMessage(msg)
            Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
        } else {
            showMessage(context.getString(R.string.export_failed))
        }
    }

    val importFileLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument(),
    ) { uri ->
        if (uri == null) return@rememberLauncherForActivityResult
        val text = BackupTransfer.readText(context, uri)
        if (text == null) {
            showMessage(context.getString(R.string.read_file_failed))
            return@rememberLauncherForActivityResult
        }
        val (evts, note) = repo.parseBackup(text)
        if (evts.isEmpty()) {
            showMessage(note ?: context.getString(R.string.import_file_empty))
            return@rememberLauncherForActivityResult
        }
        pendingFileImport = evts
        pendingFileImportNote = note
        showFileImportConfirm = true
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            NodTopBar(title = stringResource(R.string.settings), onBack = onBack)
        },
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 8.dp),
        ) {
            Text(stringResource(R.string.data_section), color = N500, fontSize = 12.sp)
            Spacer(Modifier.height(6.dp))
            Text(
                text = stringResource(R.string.local_events),
                color = N500,
                fontSize = 12.sp,
            )
            Spacer(Modifier.height(4.dp))
            Text(
                text = stringResource(R.string.local_events_value, events.size),
                color = N900,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
            )

            Spacer(Modifier.height(24.dp))
            Text(stringResource(R.string.backup_section), color = N500, fontSize = 12.sp)
            Spacer(Modifier.height(8.dp))
            Text(stringResource(R.string.backup_desc), color = N500, fontSize = 12.sp)
            Spacer(Modifier.height(12.dp))

            // Primary: real file export via SAF
            Button(
                onClick = {
                    if (events.isEmpty()) {
                        Toast.makeText(context, R.string.nothing_export, Toast.LENGTH_SHORT).show()
                        return@Button
                    }
                    exportFileLauncher.launch(BackupTransfer.defaultBackupFileName())
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(44.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Accent, contentColor = N0),
                shape = NodButtonShape,
            ) { Text(stringResource(R.string.export_file), fontSize = 14.sp) }

            Spacer(Modifier.height(8.dp))
            // Secondary: clipboard
            OutlinedButton(
                onClick = {
                    if (events.isEmpty()) {
                        Toast.makeText(context, R.string.nothing_export, Toast.LENGTH_SHORT).show()
                        return@OutlinedButton
                    }
                    val text = repo.serialize(events)
                    val cm = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                    cm.setPrimaryClip(ClipData.newPlainText("NumberOurDays", text))
                    showMessage(context.getString(R.string.copied))
                    Toast.makeText(context, R.string.copied, Toast.LENGTH_SHORT).show()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(44.dp),
                shape = NodButtonShape,
                colors = ButtonDefaults.outlinedButtonColors(contentColor = N700),
            ) { Text(stringResource(R.string.export_clip), color = N700, fontSize = 14.sp) }

            Spacer(Modifier.height(24.dp))
            Text(stringResource(R.string.import_section), color = N500, fontSize = 12.sp)
            Spacer(Modifier.height(8.dp))
            // Primary: file import
            Button(
                onClick = {
                    importFileLauncher.launch(arrayOf("application/json", "text/plain", "*/*"))
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(44.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Accent, contentColor = N0),
                shape = NodButtonShape,
            ) { Text(stringResource(R.string.import_from_file), fontSize = 14.sp) }

            Spacer(Modifier.height(8.dp))
            // Advanced: paste JSON collapsed by default
            OutlinedButton(
                onClick = { pasteAdvanced = !pasteAdvanced },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(44.dp),
                shape = NodButtonShape,
                colors = ButtonDefaults.outlinedButtonColors(contentColor = N700),
            ) {
                Text(
                    text = stringResource(
                        if (pasteAdvanced) R.string.import_advanced_collapse
                        else R.string.import_advanced_expand
                    ),
                    color = N700,
                    fontSize = 14.sp,
                )
            }

            if (pasteAdvanced) {
                Spacer(Modifier.height(12.dp))
                Text(stringResource(R.string.import_advanced), color = N500, fontSize = 12.sp)
                Spacer(Modifier.height(8.dp))
                OutlinedTextField(
                    value = importText,
                    onValueChange = { importText = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(120.dp),
                    placeholder = {
                        Text(stringResource(R.string.paste_backup), color = N300, fontSize = 12.sp)
                    },
                    shape = NodFieldShape,
                    colors = fieldColors(),
                )
                Spacer(Modifier.height(10.dp))
                OutlinedButton(
                    onClick = {
                        val (evts, note) = repo.parseBackup(importText)
                        if (evts.isEmpty()) {
                            showMessage(note ?: context.getString(R.string.import_file_empty))
                            return@OutlinedButton
                        }
                        val next = repo.merge(repo.loadEvents(), evts)
                        repo.saveAll(next)
                        importText = ""
                        val skipped = note?.let { "，$it" } ?: ""
                        showMessage("已合并 ${next.size} 条$skipped")
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(44.dp),
                    shape = NodButtonShape,
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = N700),
                ) {
                    Text(stringResource(R.string.import_merge), color = N700, fontSize = 14.sp)
                }
                Spacer(Modifier.height(8.dp))
                OutlinedButton(
                    onClick = { confirmReplace = true },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(44.dp),
                    shape = NodButtonShape,
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = ErrorRed),
                ) { Text(stringResource(R.string.import_replace), fontSize = 14.sp) }
            }

            Spacer(Modifier.height(24.dp))
            HorizontalDivider(color = N100)
            Spacer(Modifier.height(16.dp))
            Text(stringResource(R.string.danger_section), color = N500, fontSize = 12.sp)
            Spacer(Modifier.height(10.dp))
            OutlinedButton(
                onClick = {
                    val sample = events.firstOrNull()
                    if (sample == null) {
                        Toast.makeText(context, R.string.test_notification_empty, Toast.LENGTH_SHORT).show()
                        return@OutlinedButton
                    }
                    NotificationHelper.showReminder(
                        context = context,
                        eventId = sample.id,
                        title = sample.title,
                        text = context.getString(R.string.test_notification_body),
                    )
                    showMessage(context.getString(R.string.test_notification))
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(44.dp),
                shape = NodButtonShape,
                colors = ButtonDefaults.outlinedButtonColors(contentColor = N700),
            ) { Text(stringResource(R.string.test_notification), color = N700, fontSize = 14.sp) }

            Spacer(Modifier.height(8.dp))
            OutlinedButton(
                onClick = { confirmClear = true },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(44.dp),
                shape = NodButtonShape,
                colors = ButtonDefaults.outlinedButtonColors(contentColor = ErrorRed),
            ) { Text(stringResource(R.string.clear_data), fontSize = 14.sp) }

            message?.let {
                Spacer(Modifier.height(10.dp))
                Text(it, color = Accent, fontSize = 12.sp)
            }

            Spacer(Modifier.height(40.dp))
            Text(
                text = stringResource(R.string.footer),
                color = N300,
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
            onDismissRequest = {
                showFileImportConfirm = false
                pendingFileImport = null
                pendingFileImportNote = null
            },
            title = { Text(stringResource(R.string.import_file_title)) },
            text = {
                Text(stringResource(R.string.import_file_message, incoming.size, noteSuffix))
            },
            confirmButton = {
                Row {
                    TextButton(onClick = {
                        val next = repo.merge(repo.loadEvents(), incoming)
                        repo.saveAll(next)
                        showMessage(context.getString(R.string.import_file_merged, next.size, noteSuffix))
                        showFileImportConfirm = false
                        pendingFileImport = null
                        pendingFileImportNote = null
                    }) { Text(stringResource(R.string.import_file_merge), color = Accent) }
                    TextButton(onClick = {
                        repo.saveAll(incoming)
                        showMessage(context.getString(R.string.import_file_replaced, incoming.size, noteSuffix))
                        showFileImportConfirm = false
                        pendingFileImport = null
                        pendingFileImportNote = null
                    }) { Text(stringResource(R.string.import_file_replace), color = ErrorRed) }
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
            onDismissRequest = { confirmReplace = false },
            title = { Text("覆盖本地数据") },
            text = { Text("将删除本地不在备份中的事件，确定继续？") },
            confirmButton = {
                TextButton(onClick = {
                    val (evts, note) = repo.parseBackup(importText)
                    if (evts.isEmpty()) {
                        showMessage(note ?: "备份中没有有效事件")
                        confirmReplace = false
                        return@TextButton
                    }
                    repo.saveAll(evts)
                    importText = ""
                    val skipped = note?.let { "，$it" } ?: ""
                    showMessage("已覆盖为 ${evts.size} 条$skipped")
                    confirmReplace = false
                }) { Text("覆盖", color = ErrorRed) }
            },
            dismissButton = {
                TextButton(onClick = { confirmReplace = false }) { Text("取消") }
            },
        )
    }

    if (confirmClear) {
        AlertDialog(
            onDismissRequest = { confirmClear = false },
            title = { Text("清空本地数据") },
            text = { Text("将删除全部纪念日，且不可恢复。建议先导出备份。") },
            confirmButton = {
                TextButton(onClick = {
                    repo.clearAll()
                    showMessage(context.getString(R.string.cleared))
                    confirmClear = false
                }) { Text("清空", color = ErrorRed) }
            },
            dismissButton = {
                TextButton(onClick = { confirmClear = false }) { Text("取消") }
            },
        )
    }
}

@Composable
private fun fieldColors() = OutlinedTextFieldDefaults.colors(
    focusedContainerColor = N50,
    unfocusedContainerColor = N50,
    disabledContainerColor = N50,
    focusedBorderColor = Accent,
    unfocusedBorderColor = N300,
    focusedTextColor = N900,
    unfocusedTextColor = N900,
    disabledTextColor = N700,
    cursorColor = Accent,
)

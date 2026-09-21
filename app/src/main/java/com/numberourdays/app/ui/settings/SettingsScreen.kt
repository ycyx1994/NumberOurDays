package com.numberourdays.app.ui.settings

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.layout.Column
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
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.numberourdays.app.App
import com.numberourdays.app.R
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

@Composable
fun SettingsScreen(onBack: () -> Unit) {
    val repo = App.repository
    val events by repo.events.collectAsState()
    val context = LocalContext.current
    var importText by rememberSaveable { mutableStateOf("") }
    var message by rememberSaveable { mutableStateOf<String?>(null) }
    var confirmReplace by rememberSaveable { mutableStateOf(false) }
    var confirmClear by rememberSaveable { mutableStateOf(false) }

    fun showMessage(text: String) {
        message = text
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
            Spacer(Modifier.height(8.dp))
            OutlinedTextField(
                value = "${events.size} 条",
                onValueChange = {},
                readOnly = true,
                label = { Text(stringResource(R.string.local_events), color = N500) },
                modifier = Modifier.fillMaxWidth(),
                shape = NodFieldShape,
                colors = fieldColors(),
            )

            Spacer(Modifier.height(24.dp))
            Text(stringResource(R.string.backup_section), color = N500, fontSize = 12.sp)
            Spacer(Modifier.height(8.dp))
            Text(stringResource(R.string.backup_desc), color = N500, fontSize = 12.sp)
            Spacer(Modifier.height(12.dp))

            Button(
                onClick = {
                    if (events.isEmpty()) {
                        Toast.makeText(context, R.string.nothing_export, Toast.LENGTH_SHORT).show()
                        return@Button
                    }
                    val text = repo.serialize(events)
                    val cm = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                    cm.setPrimaryClip(ClipData.newPlainText("NumberOurDays", text))
                    showMessage("已导出 ${events.size} 条，请粘贴保存")
                    Toast.makeText(context, R.string.copied, Toast.LENGTH_SHORT).show()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(44.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Accent, contentColor = N0),
                shape = NodButtonShape,
            ) { Text(stringResource(R.string.export_clip), fontSize = 14.sp) }

            Spacer(Modifier.height(8.dp))
            OutlinedButton(
                onClick = {
                    if (events.isEmpty()) {
                        Toast.makeText(context, R.string.nothing_export, Toast.LENGTH_SHORT).show()
                        return@OutlinedButton
                    }
                    val text = repo.serialize(events)
                    val cm = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                    cm.setPrimaryClip(ClipData.newPlainText("NumberOurDays-backup", text))
                    showMessage("备份内容已复制，可粘贴到文件管理器保存为 .json")
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(44.dp),
                shape = NodButtonShape,
                colors = ButtonDefaults.outlinedButtonColors(contentColor = N700),
            ) { Text(stringResource(R.string.export_file), color = N700, fontSize = 14.sp) }

            Spacer(Modifier.height(24.dp))
            Text(stringResource(R.string.import_section), color = N500, fontSize = 12.sp)
            Spacer(Modifier.height(8.dp))
            OutlinedTextField(
                value = importText,
                onValueChange = { importText = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp),
                placeholder = { Text(stringResource(R.string.paste_backup), color = N300, fontSize = 12.sp) },
                shape = NodFieldShape,
                colors = fieldColors(),
            )

            Spacer(Modifier.height(10.dp))
            Button(
                onClick = {
                    val (evts, note) = repo.parseBackup(importText)
                    if (evts.isEmpty()) {
                        showMessage(note ?: "备份中没有有效事件")
                        return@Button
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
                colors = ButtonDefaults.buttonColors(containerColor = Accent, contentColor = N0),
                shape = NodButtonShape,
            ) { Text(stringResource(R.string.import_merge), fontSize = 14.sp) }

            Spacer(Modifier.height(8.dp))
            OutlinedButton(
                onClick = { confirmReplace = true },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(44.dp),
                shape = NodButtonShape,
                colors = ButtonDefaults.outlinedButtonColors(contentColor = ErrorRed),
            ) { Text(stringResource(R.string.import_replace), fontSize = 14.sp) }

            Spacer(Modifier.height(24.dp))
            Text(stringResource(R.string.danger_section), color = N500, fontSize = 12.sp)
            Spacer(Modifier.height(10.dp))
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

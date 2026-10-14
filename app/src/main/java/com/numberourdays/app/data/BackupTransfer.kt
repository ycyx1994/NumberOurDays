package com.numberourdays.app.data

import android.content.Context
import android.net.Uri

/** ContentResolver helpers for real .json backup export/import. Codec lives in shared. */
object BackupTransfer {
    fun defaultBackupFileName(date: kotlinx.datetime.LocalDate = com.numberourdays.app.domain.DateCalculator.today()): String =
        BackupNaming.defaultBackupFileName(date)

    fun writeText(context: Context, uri: Uri, text: String): Boolean {
        return try {
            val out = context.contentResolver.openOutputStream(uri, "wt") ?: return false
            out.use { stream ->
                stream.write(text.toByteArray(Charsets.UTF_8))
                stream.flush()
            }
            true
        } catch (_: Exception) {
            false
        }
    }

    fun readText(context: Context, uri: Uri): String? {
        return try {
            val input = context.contentResolver.openInputStream(uri) ?: return null
            input.use { stream ->
                stream.readBytes().toString(Charsets.UTF_8)
            }
        } catch (_: Exception) {
            null
        }
    }
}

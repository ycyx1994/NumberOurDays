package com.numberourdays.app.data

import kotlinx.datetime.LocalDate

/** Pure backup file naming (no platform I/O). */
object BackupNaming {
    fun defaultBackupFileName(date: LocalDate): String {
        val ymd = date.toString().replace("-", "")
        return "numberourdays-backup-$ymd.json"
    }
}

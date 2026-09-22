package com.numberourdays.app.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

@Database(entities = [DayEventEntity::class], version = 2, exportSchema = false)
abstract class NodDatabase : RoomDatabase() {
    abstract fun dayEventDao(): DayEventDao

    companion object {
        private val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL(
                    "ALTER TABLE day_events ADD COLUMN remindHour INTEGER NOT NULL DEFAULT 9",
                )
                db.execSQL(
                    "ALTER TABLE day_events ADD COLUMN remindMinute INTEGER NOT NULL DEFAULT 0",
                )
            }
        }

        @Volatile
        private var instance: NodDatabase? = null

        fun get(context: Context): NodDatabase =
            instance ?: synchronized(this) {
                instance ?: Room.databaseBuilder(
                    context.applicationContext,
                    NodDatabase::class.java,
                    "nod.sqlite",
                )
                    .addMigrations(MIGRATION_1_2)
                    .build().also { instance = it }
            }
    }
}

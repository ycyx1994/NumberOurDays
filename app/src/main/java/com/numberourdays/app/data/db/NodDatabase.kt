package com.numberourdays.app.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [DayEventEntity::class], version = 1, exportSchema = false)
abstract class NodDatabase : RoomDatabase() {
    abstract fun dayEventDao(): DayEventDao

    companion object {
        @Volatile
        private var instance: NodDatabase? = null

        fun get(context: Context): NodDatabase =
            instance ?: synchronized(this) {
                instance ?: Room.databaseBuilder(
                    context.applicationContext,
                    NodDatabase::class.java,
                    "nod.sqlite",
                ).build().also { instance = it }
            }
    }
}

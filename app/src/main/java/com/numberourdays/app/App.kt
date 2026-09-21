package com.numberourdays.app

import android.app.Application
import com.numberourdays.app.data.EventRepository

class App : Application() {
    override fun onCreate() {
        super.onCreate()
        repository = EventRepository(this)
        repository.ensureDemoIfEmpty()
    }

    companion object {
        lateinit var repository: EventRepository
            private set
    }
}

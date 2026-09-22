package com.numberourdays.app

import android.app.Application
import com.numberourdays.app.data.EventRepository
import com.numberourdays.app.notify.NotificationHelper

class App : Application() {
    override fun onCreate() {
        super.onCreate()
        NotificationHelper.createChannel(this)
        repository = EventRepository(this)
        repository.ensureDemoIfEmpty()
    }

    companion object {
        lateinit var repository: EventRepository
            private set
    }
}

package com.numberourdays.app

import android.app.Application
import com.numberourdays.app.data.RoomEventRepository
import com.numberourdays.app.notify.NotificationHelper

class App : Application() {
    override fun onCreate() {
        super.onCreate()
        NotificationHelper.createChannel(this)
        repository = RoomEventRepository(this)
        repository.ensureDemoIfEmpty()
    }

    companion object {
        lateinit var repository: com.numberourdays.app.data.EventRepository
            private set
    }
}

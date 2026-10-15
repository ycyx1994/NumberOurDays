package com.numberourdays.app.ui.settings

import android.content.Context
import com.numberourdays.app.data.DurationUnit

/** Home hero display unit (天/月/年), persisted like NavAnimPrefs. */
object HomeUnitPrefs {
    private const val FILE = "nod_prefs"
    private const val KEY = "home_unit"

    fun unit(context: Context): DurationUnit {
        val raw = context.getSharedPreferences(FILE, Context.MODE_PRIVATE)
            .getString(KEY, DurationUnit.DAY.name) ?: DurationUnit.DAY.name
        return runCatching { DurationUnit.valueOf(raw) }.getOrDefault(DurationUnit.DAY)
    }

    fun setUnit(context: Context, unit: DurationUnit) {
        context.getSharedPreferences(FILE, Context.MODE_PRIVATE)
            .edit()
            .putString(KEY, unit.name)
            .apply()
    }
}

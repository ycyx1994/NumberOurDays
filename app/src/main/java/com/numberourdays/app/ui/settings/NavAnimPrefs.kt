package com.numberourdays.app.ui.settings

import android.content.Context

/** Persisted navigation transition style so Settings can A/B anims. */
object NavAnimPrefs {
    private const val FILE = "nod_prefs"
    private const val KEY = "nav_anim_style"

    const val FADE = "fade"
    const val FADE_SCALE = "fade_scale"

    fun style(context: Context): String =
        context.getSharedPreferences(FILE, Context.MODE_PRIVATE)
            .getString(KEY, FADE) ?: FADE

    fun setStyle(context: Context, style: String) {
        context.getSharedPreferences(FILE, Context.MODE_PRIVATE)
            .edit()
            .putString(KEY, style)
            .apply()
    }
}

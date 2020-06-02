package com.cronberry.fcmpushnotification

import android.content.Context
import android.content.SharedPreferences

class CronberryPref(context: Context) {

    private val prefFileName = "com.cronberry"
    private val userEmail = "useremail"
    private val prefs: SharedPreferences = context.getSharedPreferences(prefFileName, 0)

    var userEmailPref: String
        get() = prefs.getString(userEmail, "")!!
        set(value) = prefs.edit().putString(userEmail, value).apply()
}
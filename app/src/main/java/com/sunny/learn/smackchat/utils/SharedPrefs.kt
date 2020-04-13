package com.sunny.learn.smackchat.utils

import android.content.Context
import android.content.SharedPreferences
import com.android.volley.toolbox.Volley

class SharedPrefs(context: Context) {

    val SHARED_PREF_FILE_NAME = "user_prefs"
    val IS_LOOGED_IN = "is_looged_in"
    val USER_EMAIL = "user_email"
    val USER_TOKEN = "user_token"

    val sharedPrefs: SharedPreferences =
        context.getSharedPreferences(SHARED_PREF_FILE_NAME, Context.MODE_PRIVATE)

    var isUserLoggedIn: Boolean
        get() = sharedPrefs.getBoolean(IS_LOOGED_IN, false)
        set(value) = sharedPrefs.edit().putBoolean(IS_LOOGED_IN, value).apply()

    var userEmail: String?
        get() = sharedPrefs.getString(USER_EMAIL, "")
        set(value) = sharedPrefs.edit().putString(USER_EMAIL, value).apply()

    var userToken: String?
        get() = sharedPrefs.getString(USER_TOKEN, "")
        set(value) = sharedPrefs.edit().putString(USER_TOKEN, value).apply()

    val requestQueue = Volley.newRequestQueue(context)
}
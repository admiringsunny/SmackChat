package com.sunny.learn.smackchat.controllers

import android.app.Application
import com.sunny.learn.smackchat.utils.SharedPrefs

class App: Application() {

    companion object{
        lateinit var prefs: SharedPrefs
    }

    override fun onCreate() {
        prefs = SharedPrefs(applicationContext)
        super.onCreate()
    }
}
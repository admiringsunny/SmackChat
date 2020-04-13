package com.sunny.learn.smackchat.services

import android.graphics.Color
import com.sunny.learn.smackchat.controllers.App

object UserDataService {
    var id = ""
    var email = ""
    var name = ""
    var avatarName = ""
    var avatarColor = ""

    fun logout() {
        id = ""
        email = ""
        name = ""
        avatarName = ""
        avatarColor = ""

        App.prefs.userEmail = ""
        App.prefs.userToken = ""
        App.prefs.isUserLoggedIn = false
    }
}
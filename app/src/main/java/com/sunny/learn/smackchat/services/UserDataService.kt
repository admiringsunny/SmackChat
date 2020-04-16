package com.sunny.learn.smackchat.services

import android.graphics.Color
import com.sunny.learn.smackchat.controllers.App
import java.util.*

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
        MessageService.clearMessages()
        MessageService.clearChannels()
    }

    fun returnAvatarColor(components: String) : Int {
        val strippedColor = components
            .replace("[", "")
            .replace("]", "")
            .replace(",", "")

        var r = 0
        var g = 0
        var b = 0

        val scanner = Scanner(strippedColor)
        if (scanner.hasNext()) {
            r = (scanner.nextDouble() * 255).toInt()
            g = (scanner.nextDouble() * 255).toInt()
            b = (scanner.nextDouble() * 255).toInt()
        }

        return Color.rgb(r,g,b)
    }
}
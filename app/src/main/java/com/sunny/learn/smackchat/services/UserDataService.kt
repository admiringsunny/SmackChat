package com.sunny.learn.smackchat.services

import android.graphics.Color

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

        AuthService.user = ""
        AuthService.token = ""
        AuthService. isLoggedIn = false
    }
}
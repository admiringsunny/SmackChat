package com.sunny.learn.smackchat.utils

//const val URL_BASE = "http://10.0.2.2:3005/v1/" // emulator
const val URL_BASE = "http://192.168.3.9:3005/v1/" // real device, also needed -> android:usesCleartextTraffic="true" in manifest -> <application.. />
const val URL_REGISTER = "${URL_BASE}account/register"
const val URL_LOGIN = "${URL_BASE}account/login"
const val URL_CREATE_USER = "${URL_BASE}user/add"
const val URL_FIND_USER = "${URL_BASE}user/byEmail/"

// BROADCASTS
const val BROADCAST_USER_CREATED = "BROADCAST_USER_CREATED"

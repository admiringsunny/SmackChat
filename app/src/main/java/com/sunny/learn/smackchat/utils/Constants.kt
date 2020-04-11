package com.sunny.learn.smackchat.utils

//const val SOCKET_URI = "http://10.0.2.2:3005/" // emulator
const val SOCKET_URI = "http://192.168.3.9:3005/" // real device, also needed -> android:usesCleartextTraffic="true" in manifest -> <application.. />
const val URL_BASE = "${SOCKET_URI}v1/"
const val URL_REGISTER = "${URL_BASE}account/register"
const val URL_LOGIN = "${URL_BASE}account/login"
const val URL_CREATE_USER = "${URL_BASE}user/add"
const val URL_FIND_USER = "${URL_BASE}user/byEmail/"
const val URL_CHANNELS = "${URL_BASE}channel"

// BROADCASTS
const val BROADCAST_USER_CREATED = "BROADCAST_USER_CREATED"

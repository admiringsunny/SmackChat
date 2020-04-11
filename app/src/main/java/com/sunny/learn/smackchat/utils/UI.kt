package com.sunny.learn.smackchat.utils

import android.app.Activity
import android.content.Context
import android.view.inputmethod.InputMethodManager

fun hideKeyboard(context: Context) {
    val inputMethodManager =
        context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    if (inputMethodManager.isAcceptingText)
        inputMethodManager.hideSoftInputFromWindow((context as Activity).currentFocus?.windowToken, 0 )
}
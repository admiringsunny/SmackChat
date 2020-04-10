package com.sunny.learn.smackchat.controllers

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.sunny.learn.smackchat.R

class LoginActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
    }

    fun loginLoginBtnClicked(view: View) {
    }

    fun loginCreateUserBtnClicked(view: View) {
        startActivity(Intent(this, CreateUserActivity::class.java))
        finish()
    }
}

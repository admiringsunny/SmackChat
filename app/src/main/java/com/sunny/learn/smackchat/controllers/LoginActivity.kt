package com.sunny.learn.smackchat.controllers

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.sunny.learn.smackchat.R
import com.sunny.learn.smackchat.services.AuthService
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
    }

    fun loginLoginBtnClicked(view: View) {
        Toast.makeText(this, "loginLoginBtnClicked", Toast.LENGTH_SHORT).show()
        val email = loginEmailText.text.toString()
        val password = loginPasswordText.text.toString()
        AuthService.loginUser(this, email, password) { loginSuccess ->
            if (loginSuccess) {
                AuthService.findUserByEmail(this) { findSuccess ->
                    if (findSuccess) {
                        finish()
                    }
                }
            }
        }
    }

    fun loginCreateUserBtnClicked(view: View) {
        startActivity(Intent(this, CreateUserActivity::class.java))
        finish()
    }
}

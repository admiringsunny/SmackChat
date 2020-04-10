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
        showProgress(false)
    }

    private fun showProgress(boolean: Boolean) {
        if (!boolean) {
            loginProgressBar.visibility = View.GONE
        } else {
            loginProgressBar.visibility = View.VISIBLE
        }
        loginEmailText.isEnabled = !boolean
        loginPasswordText.isEnabled = !boolean
        loginLoginBtn.isEnabled = !boolean
        textView.isEnabled = !boolean
        loginCreateUserBtn.isEnabled = !boolean
    }

    fun loginLoginBtnClicked(view: View) {

        val email = loginEmailText.text.toString()
        val password = loginPasswordText.text.toString()
        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "All Fields are Mandatory", Toast.LENGTH_SHORT).show()
            return
        }

        showProgress(true)
        AuthService.loginUser(this, email, password) { loginSuccess ->
            if (loginSuccess) {
                AuthService.findUserByEmail(this) { findSuccess ->
                    if (findSuccess) {
                        finish()
                    }
                }
            } else {
                Toast.makeText(this, "All Fields are Mandatory", Toast.LENGTH_SHORT).show()
            }
            showProgress(false)
        }
    }

    fun loginCreateUserBtnClicked(view: View) {
        startActivity(Intent(this, CreateUserActivity::class.java))
        finish()
    }
}

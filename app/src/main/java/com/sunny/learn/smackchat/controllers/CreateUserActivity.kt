package com.sunny.learn.smackchat.controllers

import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.sunny.learn.smackchat.R
import com.sunny.learn.smackchat.services.AuthService
import com.sunny.learn.smackchat.utils.BROADCAST_USER_CREATED
import kotlinx.android.synthetic.main.activity_create_user.*
import java.util.*

class CreateUserActivity : AppCompatActivity() {

    private var avatarName = "profileDefault"
    private var avatarColor = "[0.5, 0.5, 0.5, 1]"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_user)
        showPrograss(false)
    }

    private fun showPrograss(b: Boolean) {
        if (b) {
            progressBar.visibility = View.VISIBLE
        } else {
            progressBar.visibility = View.GONE
        }
        createUserNameText.isEnabled = !b
        createEmailText.isEnabled = !b
        createPasswordText.isEnabled = !b
        textView2.isEnabled = !b
        createAvatarImageView.isEnabled = !b
        backgroundBtn.isEnabled = !b
        createUserBtn.isEnabled = !b
    }

    fun generateUserAvater(view: View) {
        val random = Random()
        val color = random.nextInt(2)
        val image = random.nextInt(28)

        avatarName = if (color == 0) "light$image" else "dark$image"
        createAvatarImageView.setImageResource(
            resources.getIdentifier(
                avatarName,
                "drawable",
                packageName
            )
        )
//        Toast.makeText(this, "$avatar", Toast.LENGTH_SHORT).show()
    }

    fun generateColorClicked(view: View) {
        val random = Random()
        val r = random.nextInt(255)
        val g = random.nextInt(255)
        val b = random.nextInt(255)

        createAvatarImageView.setBackgroundColor(Color.rgb(r, g, b))
//        Toast.makeText(this, "$r, $g, $b", Toast.LENGTH_SHORT).show()

//        avatarColor = "[${r.toDouble() / 255}, ${g.toDouble() / 255}, ${b.toDouble() / 255}, 1]"
        avatarColor = "[$r, $g, $b, 1]"
//        Toast.makeText(this, "$avatarColor", Toast.LENGTH_SHORT).show()
    }

    fun createUserClicked(view: View) {
        Toast.makeText(this, "createUserClicked", Toast.LENGTH_SHORT).show()
        val name = createUserNameText.text.toString()
        val email = createEmailText.text.toString()
        val password = createPasswordText.text.toString()

        if (name.isEmpty() || email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "All Text Fields are mandatory", Toast.LENGTH_LONG).show()
            return
        }

        showPrograss(true)
        AuthService.registerUser(email, password) { registerSuccess ->
            if (registerSuccess) {
                AuthService.loginUser(this, email, password) { loginSuccess ->
                    if (loginSuccess) {
                        AuthService.createUser(
                            name,
                            email,
                            avatarName,
                            avatarColor
                        ) { userCreated ->
                            if (userCreated) {
                                val broadcastIntent = Intent(BROADCAST_USER_CREATED)
                                LocalBroadcastManager.getInstance(this)
                                    .sendBroadcast(broadcastIntent)
                                Toast.makeText(this, "userCreated", Toast.LENGTH_SHORT).show()
                                finish()
                            } else {
                                Toast.makeText(
                                    this,
                                    "Something went wrong",
                                    Toast.LENGTH_LONG
                                ).show()
                            }
                            showPrograss(false)
                        }
                    } else {
                        Toast.makeText(
                            this,
                            "Something went wrong",
                            Toast.LENGTH_LONG
                        ).show()
                        showPrograss(false)
                    }
                }
            } else {
                Toast.makeText(
                    this,
                    "Something went wrong",
                    Toast.LENGTH_LONG
                ).show()
                showPrograss(false)
            }
        }
    }
}
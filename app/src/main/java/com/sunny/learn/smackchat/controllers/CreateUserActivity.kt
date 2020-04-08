package com.sunny.learn.smackchat.controllers

import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.sunny.learn.smackchat.R
import kotlinx.android.synthetic.main.activity_create_user.*
import java.util.*

class CreateUserActivity : AppCompatActivity() {

    private var avatar = "profileDefault"
    private var avatarColor = "[0.5, 0.5, 0.5, 1]"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_user)
    }

    fun generateUserAvater(view: View) {
        val random = Random()
        val color = random.nextInt(2)
        val image = random.nextInt(28)

        avatar = if (color == 0) "light$image" else "dark$image"
        createAvatarImageView.setImageResource(
            resources.getIdentifier(
                avatar,
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

        avatarColor = "[${r.toDouble() / 255}, ${g.toDouble() / 255}, ${b.toDouble() / 255}, 1]"
//        Toast.makeText(this, "$avatarColor", Toast.LENGTH_SHORT).show()

    }

    fun createUserClicked(view: View) {
        Toast.makeText(this, "createUserClicked", Toast.LENGTH_SHORT).show()
    }
}

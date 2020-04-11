package com.sunny.learn.smackchat.controllers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.google.android.material.navigation.NavigationView
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.drawerlayout.widget.DrawerLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.sunny.learn.smackchat.R
import com.sunny.learn.smackchat.model.Channel
import com.sunny.learn.smackchat.services.AuthService
import com.sunny.learn.smackchat.services.MessageService
import com.sunny.learn.smackchat.services.UserDataService
import com.sunny.learn.smackchat.utils.BROADCAST_USER_CREATED
import com.sunny.learn.smackchat.utils.SOCKET_URI
import com.sunny.learn.smackchat.utils.URL_BASE
import io.socket.client.IO
import io.socket.emitter.Emitter
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.nav_header_main.*

class MainActivity : AppCompatActivity() {

    private lateinit var channelAdapter: ArrayAdapter<Channel>
    val socket = IO.socket(SOCKET_URI)

    private lateinit var appBarConfiguration: AppBarConfiguration

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        val drawerLayout: DrawerLayout = findViewById(R.id.drawer_layout)
        val navView: NavigationView = findViewById(R.id.nav_view)
        val navController = findNavController(R.id.nav_host_fragment)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.nav_home,
                R.id.nav_gallery,
                R.id.nav_slideshow
            ), drawerLayout
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        socket.connect()
        socket.on("channelCreated", onNewChannel)

        channelAdapter = ArrayAdapter<Channel>(
            this,
            android.R.layout.simple_list_item_1,
            MessageService.channels
        )
        channel_list.adapter = channelAdapter
    }

    override fun onResume() {
        LocalBroadcastManager.getInstance(this).registerReceiver(
            userDataReceiver, IntentFilter(
                BROADCAST_USER_CREATED
            )
        )
        super.onResume()
    }

    override fun onDestroy() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(userDataReceiver)
        socket.disconnect()
        super.onDestroy()
    }

    private val userDataReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent?) {
            if (AuthService.isLoggedIn) {

                val imageResId =
                    resources.getIdentifier(UserDataService.avatarName, "drawable", packageName)
                val r: Int = (UserDataService.avatarColor.replace("[", "").replace("]", "")
                    .split(",")[0]).trim().toInt()
                val g: Int = (UserDataService.avatarColor.replace("[", "").replace("]", "")
                    .split(",")[1]).trim().toInt()
                val b: Int = (UserDataService.avatarColor.replace("[", "").replace("]", "")
                    .split(",")[2]).trim().toInt()

                userNameNavHeader.text = UserDataService.name
                userEmailNavHeader.text = UserDataService.email
                userImageNavHeader.setImageResource(imageResId)
                userImageNavHeader.setBackgroundColor(Color.rgb(r, g, b))
                loginBtmNavHeader.text = "Logout"

                MessageService.getChannels(context){ complete ->
                    if (complete) {
                        channelAdapter.notifyDataSetChanged()
                    }
                }
            }
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }

    fun loginBtmNavClicked(view: View) {
        if (AuthService.isLoggedIn) {
            UserDataService.logout()
            userImageNavHeader.setImageResource(R.drawable.profiledefault)
            userImageNavHeader.setBackgroundColor(Color.TRANSPARENT)
            userNameNavHeader.text = ""
            userEmailNavHeader.text = ""
            loginBtmNavHeader.text = "Login"
        } else
            startActivity(Intent(this, LoginActivity::class.java))
    }

    fun addChannelClicked(view: View) {

        if (!AuthService.isLoggedIn) {
            Toast.makeText(this, "Please login !", Toast.LENGTH_SHORT).show()
            return
        }
        Toast.makeText(this, "addChannelClicked", Toast.LENGTH_SHORT).show()
        val builder = AlertDialog.Builder(this)
        val dialogView = layoutInflater.inflate(R.layout.dialog_add_channel, null)
        builder.setView(dialogView)
            .setPositiveButton("Add") { dialogInterface, i ->
                val diag_channel_name =
                    (dialogView.findViewById<EditText>(R.id.diag_channel_name)).text.toString()
                val add_channel_desc =
                    (dialogView.findViewById<EditText>(R.id.add_channel_desc)).text.toString()
                socket.emit("newChannel", diag_channel_name, add_channel_desc)
            }
            .setNegativeButton("Cancel") { dialogInterface, i ->

            }
            .show()
    }

    private val onNewChannel = Emitter.Listener { args ->
        runOnUiThread {
            val name = args[0] as String
            val description = args[1] as String
            val id = args[2] as String

            val channel = Channel(name, description, id)
            MessageService.channels.add(channel)
            channelAdapter.notifyDataSetChanged()
        }
    }

    fun sendMessageBtnClicked(view: View) {
        Toast.makeText(this, "sendMessageBtnClicked", Toast.LENGTH_SHORT).show()
    }
}

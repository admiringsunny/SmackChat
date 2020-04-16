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
import androidx.core.view.GravityCompat
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.sunny.learn.smackchat.R
import com.sunny.learn.smackchat.adapters.MessageAdapter
import com.sunny.learn.smackchat.model.Channel
import com.sunny.learn.smackchat.model.Message
import com.sunny.learn.smackchat.services.AuthService
import com.sunny.learn.smackchat.services.MessageService
import com.sunny.learn.smackchat.services.UserDataService
import com.sunny.learn.smackchat.utils.BROADCAST_USER_CREATED
import com.sunny.learn.smackchat.utils.SOCKET_URI
import io.socket.client.IO
import io.socket.emitter.Emitter
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*
import kotlinx.android.synthetic.main.nav_header_main.*

class MainActivity : AppCompatActivity() {

    private lateinit var selectedChannel: Channel
    private lateinit var channelAdapter: ArrayAdapter<Channel>
    private lateinit var messageAdapter: MessageAdapter
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
        socket.on("messageCreated", onNewMessage)

        setAdapters()

        channel_list.setOnItemClickListener { _, _, i, _ ->
            selectedChannel = MessageService.channels[i]
            drawerLayout.closeDrawer(GravityCompat.START)
            updateWithChannel()
        }

        if (App.prefs.isUserLoggedIn) {
            AuthService.findUserByEmail(this) {}
        }
    }

    private fun setAdapters() {
        channelAdapter = ArrayAdapter<Channel>(
            this,
            android.R.layout.simple_list_item_1,
            MessageService.channels
        )
        channel_list.adapter = channelAdapter

        messageAdapter = MessageAdapter(this, MessageService.messages)
        messageListView.adapter = messageAdapter
        val layoutManager = LinearLayoutManager(this)
        messageListView.layoutManager = layoutManager
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
            if (App.prefs.isUserLoggedIn) {

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

                MessageService.getChannels { complete ->
                    if (complete) {
                        if (MessageService.channels.count() > 0) {
                            channelAdapter.notifyDataSetChanged()
                            selectedChannel = MessageService.channels[0]
                            updateWithChannel()
                        }
                    }
                }
            }
        }
    }

    private fun updateWithChannel() {
        MessageService.messages.clear()
        mainChannelName.text = "#${selectedChannel.name}"

        if (selectedChannel != null) {
            MessageService.getMessages(selectedChannel.id) { complete ->
                if (complete) {
                    messageAdapter.notifyDataSetChanged()
                    if (messageAdapter.itemCount > 0) {
                        messageListView.smoothScrollToPosition(messageAdapter.itemCount - 1)
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
        if (App.prefs.isUserLoggedIn) {
            UserDataService.logout()
            channelAdapter.notifyDataSetChanged()
            messageAdapter.notifyDataSetChanged()
            userImageNavHeader.setImageResource(R.drawable.profiledefault)
            userImageNavHeader.setBackgroundColor(Color.TRANSPARENT)
            userNameNavHeader.text = ""
            userEmailNavHeader.text = ""
            loginBtmNavHeader.text = resources.getString(R.string.login)
            mainChannelName.text = resources.getString(R.string.please_login)
        } else
            startActivity(Intent(this, LoginActivity::class.java))
    }

    fun addChannelClicked(view: View) {

        if (!App.prefs.isUserLoggedIn) {
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
        if (App.prefs.isUserLoggedIn) {
            runOnUiThread {
                val name = args[0] as String
                val description = args[1] as String
                val id = args[2] as String

                val newChannel = Channel(name, description, id)
                MessageService.channels.add(newChannel)
                channelAdapter.notifyDataSetChanged()
            }
        }

    }

    private val onNewMessage = Emitter.Listener { args ->
        if (App.prefs.isUserLoggedIn)
            runOnUiThread {
                val channelId = args[2] as String
                if (channelId == selectedChannel.id) {
                    val messageBody = args[0] as String
                    val userId = args[1] as String

                    val userName = args[3] as String
                    val userAvatar = args[4] as String
                    val userAvatarColor = args[5] as String
                    val id = args[6] as String
                    val timeStamp = args[7] as String

                    val newMsg = Message(
                        messageBody,
                        userId,
                        channelId,
                        userName,
                        userAvatar,
                        userAvatarColor,
                        id,
                        timeStamp
                    )
                    MessageService.messages.add(newMsg)
                    messageAdapter.notifyDataSetChanged()
                    messageListView?.smoothScrollToPosition(messageAdapter.itemCount - 1)
                }
            }
    }

    fun sendMessageBtnClicked(view: View) {

        if (App.prefs.isUserLoggedIn && messageTextField.text.isNotEmpty() && selectedChannel != null) {

            socket.emit(
                "newMessage",
                messageTextField.text.toString(),
                UserDataService.id,
                selectedChannel.id,
                UserDataService.name,
                UserDataService.avatarName,
                UserDataService.avatarColor
            )
            messageTextField.text.clear()
        }
    }
}

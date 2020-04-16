package com.sunny.learn.smackchat.services

import android.util.Log
import com.android.volley.Response
import com.android.volley.toolbox.JsonArrayRequest
import com.sunny.learn.smackchat.controllers.App
import com.sunny.learn.smackchat.model.Channel
import com.sunny.learn.smackchat.model.Message
import com.sunny.learn.smackchat.utils.URL_GET_CHANNELS
import com.sunny.learn.smackchat.utils.URL_GET_MESSAGES
import org.json.JSONException

object MessageService {
    val channels = ArrayList<Channel>()
    val messages = ArrayList<Message>()

    fun getChannels(complete: (Boolean) -> Unit) {
        val channelRequest = object : JsonArrayRequest(Method.GET, URL_GET_CHANNELS, null,
            Response.Listener { response ->
                try {
                    for (x in 0 until response.length()) {
                        val channel = response.getJSONObject(x)
                        val channel_id = channel.getString("_id")
                        val channel_name = channel.getString("name")
                        val channel_description = channel.getString("description")
                        val newChannel = Channel(channel_name, channel_description, channel_id)
                        channels.add(newChannel)
                    }

                    complete(true)
                } catch (e: JSONException) {
                    Log.e("JSONException:", "Find User Failed: $e")
                    complete(false)
                }
            }, Response.ErrorListener { error ->
                Log.e("Error:", "Find User Failed: $error")
                complete(false)
            }) {
            override fun getBodyContentType(): String {
                return "application/json charset=utf-8"
            }

            override fun getHeaders(): MutableMap<String, String> {
                val map = HashMap<String, String>()
                map.put("Authorization", "Bearer ${App.prefs.userToken}")
                return map
            }

        }
        App.prefs.requestQueue.add(channelRequest)
    }

    fun getMessages(channelid: String, complete: (Boolean) -> Unit) {
        val url = "$URL_GET_MESSAGES$channelid"

        val messageRequest = object : JsonArrayRequest(Method.GET, url, null,
            Response.Listener { response ->
                try {
                    for (x in 0 until response.length()) {
                        val msgObj = response.getJSONObject(x)
                        val id = msgObj.getString("_id")
                        val messageBody = msgObj.getString("messageBody")
                        val userId = msgObj.getString("userId")
                        val channelId = msgObj.getString("channelId")
                        val userName = msgObj.getString("userName")
                        val userAvatar = msgObj.getString("userAvatar")
                        val userAvatarColor = msgObj.getString("userAvatarColor")
                        msgObj.getString("__v")
                        val timeStamp = msgObj.getString("timeStamp")
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
                        messages.add(newMsg)
                    }
                    complete(true)
                } catch (e: JSONException) {
                    Log.e("JSONException:", "Find User Failed: $e")
                    complete(false)
                }
            },
            Response.ErrorListener { error ->
                Log.e("Error:", "Find User Failed: $error")
                complete(false)
            }
        ) {
            override fun getBodyContentType(): String {
                return "application/json charset=utf-8"
            }

            override fun getHeaders(): MutableMap<String, String> {
                val map = HashMap<String, String>()
                map.put("Authorization", "Bearer ${App.prefs.userToken}")
                return map
            }
        }
        App.prefs.requestQueue.add(messageRequest)
    }

    fun clearMessages() {
        messages.clear()
    }

    fun clearChannels() {
        channels.clear()
    }
}

package com.sunny.learn.smackchat.services

import android.content.Context
import android.util.Log
import com.android.volley.Response
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.Volley
import com.sunny.learn.smackchat.model.Channel
import com.sunny.learn.smackchat.utils.URL_CHANNELS
import org.json.JSONException

object MessageService {
    val channels = ArrayList<Channel>()

    fun getChannels(context: Context, complete: (Boolean) -> Unit) {
        val channelRequest = object : JsonArrayRequest(Method.GET, URL_CHANNELS, null,
            Response.Listener {response ->
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
            }, Response.ErrorListener {error ->
                Log.e("Error:", "Find User Failed: $error")
                complete(false)
            }) {
            override fun getBodyContentType(): String {
                return "application/json charset=utf-8"
            }

            override fun getHeaders(): MutableMap<String, String> {
                val map = HashMap<String, String>()
                map.put("Authorization", "Bearer ${AuthService.token}")
                return map
            }

        }
        Volley.newRequestQueue(context).add(channelRequest)
    }

}
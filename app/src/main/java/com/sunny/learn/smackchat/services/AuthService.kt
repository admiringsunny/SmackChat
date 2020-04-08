package com.sunny.learn.smackchat.services

import android.content.Context
import android.util.Log
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.sunny.learn.smackchat.utils.URL_REGISTER
import org.json.JSONObject

object AuthService {

    fun rsgisterUser(
        context: Context,
        email: String,
        password: String,
        complete: (Boolean) -> Unit
    ) {
        val jsonObject = JSONObject()
        jsonObject.put("email", email)
        jsonObject.put("password", password)
        val requestBody = jsonObject.toString()

        val registerRequest =
            object : StringRequest(Method.POST, URL_REGISTER,
                Response.Listener {
                    complete(true)
                }, Response.ErrorListener { error ->
                    Log.e("ERROR", "$error")
                    complete(false)
                }) {
                override fun getBodyContentType(): String {
                    return "application/json; charset=utf-8"
                }

                override fun getBody(): ByteArray {
                    return requestBody.toByteArray()
                }
            }
        Volley.newRequestQueue(context).add(registerRequest)
    }
}
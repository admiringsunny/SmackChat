package com.sunny.learn.smackchat.services

import android.content.Context
import android.util.Log
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.sunny.learn.smackchat.utils.URL_CREATE_USER
import com.sunny.learn.smackchat.utils.URL_LOGIN
import com.sunny.learn.smackchat.utils.URL_REGISTER
import org.json.JSONException
import org.json.JSONObject

object AuthService {

    var user = ""
    var token = ""
    var isLoggedIn = false

    fun registerUser(
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

    fun loginUser(context: Context, email: String, password: String, complete: (Boolean) -> Unit) {
        val jsonObject = JSONObject()
        jsonObject.put("email", email)
        jsonObject.put("password", password)
        val requestBody = jsonObject.toString()

        val loginRequest = object : JsonObjectRequest(Method.POST, URL_LOGIN, null,
            Response.Listener { response ->
                Log.d("Response", "Login success: $response")
                try {
                    user = response.getString("user")
                    token = response.getString("token")
                    isLoggedIn = true
                    complete(true)
                } catch (e: JSONException) {
                    Log.e("JSONException", "Exc: ${e.localizedMessage}")
                    complete(false)
                }
            }, Response.ErrorListener { error ->
                Log.e("Error:", "Login Failed: $error")
                complete(false)
            }) {
            override fun getBodyContentType(): String {
                return "application/json; charset=utf-8"
            }

            override fun getBody(): ByteArray {
                return requestBody.toByteArray()
            }
        }

        Volley.newRequestQueue(context).add(loginRequest)
    }

    fun createUser(
        context: Context,
        name: String,
        email: String,
        avatarName: String,
        avatarColor: String,
        complete: (Boolean) -> Unit
    ) {
        val jsonObject = JSONObject()
        jsonObject.put("name", name)
        jsonObject.put("email", email)
        jsonObject.put("avatarName", avatarName)
        jsonObject.put("avatarColor", avatarColor)
        val requestBody = jsonObject.toString()

        val requestCreateUser = object : JsonObjectRequest(Method.POST, URL_CREATE_USER, null,
            Response.Listener { response ->
                try {
                    Log.d("Response:", "Create User success")
                    UserDataService.id = response.getString("_id")
                    UserDataService.email = response.getString("email")
                    UserDataService.name = response.getString("name")
                    UserDataService.avatarName = response.getString("avatarName")
                    UserDataService.avatarColor = response.getString("avatarColor")
                    complete(true)
                } catch (e: JSONException) {
                    Log.e("Error:", "JSONException: $e")
                    complete(false)
                }
            }, Response.ErrorListener { error ->
                Log.e("Error:", "Create User Failed: $error")
                complete(false)
            }) {

            override fun getBodyContentType(): String {
                return "application/json; charset=utf-8"
            }

            override fun getHeaders(): MutableMap<String, String> {
                val headers = HashMap<String, String>()
                headers.put("Authorization", "Bearer $token")
                return headers
            }

            override fun getBody(): ByteArray {
                return requestBody.toByteArray()
            }

        }
        Volley.newRequestQueue(context).add(requestCreateUser)
    }
}
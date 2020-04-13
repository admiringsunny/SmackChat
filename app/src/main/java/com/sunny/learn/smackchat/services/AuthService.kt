package com.sunny.learn.smackchat.services

import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.Toast
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.sunny.learn.smackchat.controllers.App
import com.sunny.learn.smackchat.utils.*
import org.json.JSONException
import org.json.JSONObject

object AuthService {

    fun registerUser(
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
        App.prefs.requestQueue.add(registerRequest)
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
                    App.prefs.userEmail = response.getString("user")
                    App.prefs.userToken = response.getString("token")
                    App.prefs.isUserLoggedIn = true
                    complete(true)
                } catch (e: JSONException) {
                    Log.e("JSONException", "Exc: ${e.localizedMessage}")
                    complete(false)
                }
            }, Response.ErrorListener { error ->
                Log.e("Error:", "Login Failed: $error")
                complete(false)
                Toast.makeText(context, "Login Failed", Toast.LENGTH_SHORT).show()

            }) {
            override fun getBodyContentType(): String {
                return "application/json; charset=utf-8"
            }

            override fun getBody(): ByteArray {
                return requestBody.toByteArray()
            }
        }

        App.prefs.requestQueue.add(loginRequest)
    }

    fun createUser(
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
                headers.put("Authorization", "Bearer ${App.prefs.userToken}")
                return headers
            }

            override fun getBody(): ByteArray {
                return requestBody.toByteArray()
            }

        }
        App.prefs.requestQueue.add(requestCreateUser)
    }

    fun findUserByEmail(context: Context, complete: (Boolean) -> Unit) {
        val findUserRequest = object : JsonObjectRequest(Method.GET, "$URL_FIND_USER${App.prefs.userEmail}", null,
            Response.Listener { response ->
                try {
                    UserDataService.name = response.getString("name")
                    UserDataService.email = response.getString("email")
                    UserDataService.avatarName = response.getString("avatarName")
                    UserDataService.avatarColor = response.getString("avatarColor")
                    UserDataService.id = response.getString("_id")

                    val userDataChange = Intent(BROADCAST_USER_CREATED)
                    LocalBroadcastManager.getInstance(context).sendBroadcast(userDataChange)

                    complete(true)

                } catch (e: JSONException) {
                    Log.e("JSONException:", "Find User Failed: $e")
                    complete(false)
                }

            }, Response.ErrorListener { error ->
                Log.e("Error:", "Find User Failed: $error")
                complete(false)
                Toast.makeText(context, "Find User Failed", Toast.LENGTH_SHORT).show()
            }) {

            override fun getBodyContentType(): String {
                return "application/json; charset=utf-8"
            }

            override fun getHeaders(): MutableMap<String, String> {
                val headers = HashMap<String, String>()
                headers.put("Authorization", "Bearer ${App.prefs.userToken}")
                return headers
            }

        }
        App.prefs.requestQueue.add(findUserRequest)
    }
}
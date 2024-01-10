package com.example.myapplication.ui

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.graphics.Color
import android.text.InputType
import android.text.InputType.TYPE_CLASS_TEXT
import android.text.InputType.TYPE_TEXT_VARIATION_PASSWORD
import android.text.method.PasswordTransformationMethod
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.Request
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley;
import com.example.myapplication.R
import org.json.JSONException
import org.json.JSONObject
import android.util.Patterns
import android.widget.*
import com.android.volley.AuthFailureError
import com.android.volley.DefaultRetryPolicy
import com.android.volley.toolbox.JsonArrayRequest
import com.example.myapplication.adapters.OrderDetailsOneOrderAdapter
import com.example.myapplication.models.OrderDetailsOneOrder
import com.example.myapplication.utils.StaticAddress
import com.google.firebase.FirebaseApp
import com.google.firebase.messaging.FirebaseMessaging
import java.util.HashMap


class LoginActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val emailEditText: EditText = findViewById(R.id.login_email)
        val passwordEditText: EditText = findViewById(R.id.password)
        val loginButton: Button = findViewById(R.id.loginButton)

        val sharedPreferences = getSharedPreferences("MyPreferences", Context.MODE_PRIVATE)
        val email = sharedPreferences.getString("email", "empty").toString()
        val password = sharedPreferences.getString("password", "empty").toString()
        val firstTime = intent.getBooleanExtra("firstTime", false)

        FirebaseApp.initializeApp(this)

        if (email != "empty" && password != "empty") {
            emailEditText.setText(email)
            passwordEditText.setText(password)
//            performLogin(email, password, false)
        }

        loginButton.setOnClickListener {
            val email = emailEditText.text.toString()
            val password = passwordEditText.text.toString()

            if (email.isEmpty()) {
                emailEditText.error = "Unesite email."
            }

            if (!isValidEmail(email)) {
                emailEditText.error = "Neispravna email adresa."
            }

            if (password.isEmpty()) {
                passwordEditText.error = "Unesite šifru."
            }
            if (!isValidPassword(password)) {
                passwordEditText.error = "Šifra mora sadržati najmanje 8 karaktera, jedno veliko slovo, jedno malo slovo i jedan broj."
            }

            if (isValidEmail(email) && isValidPassword(password)) {
                performLogin(email, password, firstTime)
            }
        }

        val forgotPassword = findViewById<TextView>(R.id.forgot_password)
        forgotPassword.setOnClickListener {
            val intent = Intent(this, ForgotPasswordVerificationActivity::class.java)
            startActivity(intent)
        }

        val openRegistration = findViewById<TextView>(R.id.login_to_register)
        openRegistration.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)

        }

        val toggleButton: ImageButton = findViewById(R.id.toggleButton)
        toggleButton.setOnClickListener{
            val passwordEditText: EditText = findViewById(R.id.password)
            var inputType = passwordEditText.inputType

            if (inputType == InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD) {
                passwordEditText.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
            }
            else {
                passwordEditText.inputType = InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
            }
            passwordEditText.setSelection(passwordEditText.text.length)
        }
    }

    private fun isValidEmail(email: String): Boolean {
        return Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    private fun isValidPassword(password: String): Boolean {
        return (password.length >= 8) &&
                password.contains(Regex(".*[A-Z].*")) &&
                password.contains(Regex(".*[a-z].*")) &&
                password.contains(Regex(".*\\d.*"))
    }

    private fun showToastMessage(message: String, isError: Boolean) {
        val toast = Toast.makeText(this@LoginActivity, message, Toast.LENGTH_LONG)
        val view = toast.view
        view?.setBackgroundColor(if (isError) Color.RED else Color.GREEN)
        val text = view?.findViewById<TextView>(android.R.id.message)
        text?.setTextColor(Color.WHITE)
        toast.show()
    }

    private fun sendToken(email: String) {
        FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val token = task.result
                val requestQueue: RequestQueue = Volley.newRequestQueue(this)
                val url = StaticAddress.URL + "/notification/save-token"

                val jsonParams = JSONObject()
                jsonParams.put("email", email)
                jsonParams.put("token", token)

                val JsonObjectRequest = object : JsonObjectRequest(
                    Request.Method.POST, url, jsonParams,
                    { response ->

                    },
                    { error ->
//                        Toast.makeText(this, error.message, Toast.LENGTH_LONG).show()
                    }) {
                    @Throws(AuthFailureError::class)
                    override fun getHeaders(): Map<String, String> {
                        val headers = HashMap<String, String>()

                        val sharedPreferences = getSharedPreferences("MyPreferences", Context.MODE_PRIVATE)
                        val jwtToken = sharedPreferences.getString("JWT_TOKEN", "token")

                        headers["Authorization"] = "Bearer $jwtToken"
                        return headers
                    }
                }

                JsonObjectRequest.retryPolicy = DefaultRetryPolicy(0, 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT)
                requestQueue.add(JsonObjectRequest)
            } else
                showToastMessage("Failed to get FCM Token: ${task.exception?.message}", true)
        }
    }

    private fun performLogin(email: String, password: String, firstTime: Boolean) {
        val jsonParams = JSONObject()
        jsonParams.put("email", email)
        jsonParams.put("password", password)

        val requestQueue: RequestQueue = Volley.newRequestQueue(this)
        val url = StaticAddress.URL + "/web/login"

        val jsonObjectRequest = JsonObjectRequest(Request.Method.POST, url, jsonParams,
            { response ->
                val responseBody = response.toString()
                try {
                    val jsonResponse = JSONObject(responseBody)
                    if (responseBody.contains("Login failed")) {
                        showToastMessage("Pogrešan email ili Šifra. Ako nemate nalog, registrujte se.", true)
                    } else {
                        val sharedPreferences = getSharedPreferences("MyPreferences", Context.MODE_PRIVATE)
                        val editor = sharedPreferences.edit()
                        editor.putString("JWT_TOKEN", jsonResponse.getString("token"))
                        editor.putString("firstName", jsonResponse.getString("firstName"))
                        editor.putString("lastName", jsonResponse.getString("lastName"))
                        editor.putString("email", email)
                        editor.putString("password", password)

                        val role = jsonResponse.getString("authorities")
                        if (role.contains("CUSTOMER")) {
                            editor.putString("role", "Kupac")
                        }
                        else if (role.contains("PRODUCER")) {
                            editor.putString("role", "Prodavac")
                        }
                        else if (role.contains("DELIVERER")) {
                            editor.putString("role", "Dostavljač")
                        }
                        editor.apply()

                        sendToken(email)
                        val intent = Intent(this, HomeActivity::class.java)
                        if (firstTime)
                            intent.putExtra("firstTime", true)
                        startActivity(intent)
                    }
                } catch (e: JSONException) {
                    Log.e("Error", "JSON parsing error: ${e.message}")
                }
            },
            { error ->
                if (error is AuthFailureError && error.networkResponse?.statusCode == 401) {
                    showToastMessage("Pogrešan email ili šifra. Ako nemate nalog, registrujte se.", true)
                } else {
                    Log.e("Error", "Error")
                }
            })
        jsonObjectRequest.retryPolicy = DefaultRetryPolicy(0, 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT)

        requestQueue.add(jsonObjectRequest)
    }

}
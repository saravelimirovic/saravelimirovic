package com.example.myapplication.ui

import android.content.Context
import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.InputType
import android.widget.TextView
import android.widget.Toast
import com.android.volley.AuthFailureError
import com.android.volley.DefaultRetryPolicy
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.example.myapplication.R
import com.example.myapplication.databinding.ActivitySecurityAndPasswordBinding
import com.example.myapplication.databinding.ActivityUserAccountBinding
import com.example.myapplication.utils.StaticAddress
import org.json.JSONObject

class MyPasswordActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySecurityAndPasswordBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySecurityAndPasswordBinding.inflate(layoutInflater)
        setContentView(binding.root)

        getMyEmail()

        binding.goBack.setOnClickListener {
            val intent = Intent(this, UserAccountActivity::class.java)
            startActivity(intent)
        }

        binding.forgotPassword.setOnClickListener {
            val intent = Intent(this, ForgotPasswordVerificationActivity::class.java)
            startActivity(intent)
        }

        binding.toggleButtonCurrent.setOnClickListener {
            var inputType = binding.currentPassword.inputType

            if (inputType == InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD) {
                binding.currentPassword.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
            }
            else {
                binding.currentPassword.inputType = InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
            }
            binding.currentPassword.setSelection(binding.currentPassword.text.length)
        }

        binding.toggleButton1.setOnClickListener {
            var inputType = binding.newPassword1.inputType

            if (inputType == InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD) {
                binding.newPassword1.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
            }
            else {
                binding.newPassword1.inputType = InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
            }
            binding.newPassword1.setSelection(binding.newPassword1.text.length)
        }

        binding.toggleButton2.setOnClickListener {
            var inputType = binding.newPassword2.inputType

            if (inputType == InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD) {
                binding.newPassword2.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
            }
            else {
                binding.newPassword2.inputType = InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
            }
            binding.newPassword2.setSelection(binding.newPassword2.text.length)
        }

        binding.saveChangesButton.setOnClickListener {
            if(binding.currentPassword.text.toString().isEmpty()) {
                binding.currentPassword.error = "Unesite šifru."
            }
            if(binding.newPassword1.text.toString().isEmpty()) {
                binding.newPassword1.error = "Unesite šifru."
            }
            if(binding.newPassword2.text.toString().isEmpty()) {
                binding.newPassword2.error = "Unesite šifru."
            }
            if (!isValidPassword(binding.currentPassword.text.toString())) {
                binding.currentPassword.error = "Šifra mora sadržati najmanje 8 karaktera, jedno veliko slovo, jedno malo slovo i jedan broj."
            }
            if (!isValidPassword(binding.newPassword1.text.toString())) {
                binding.newPassword1.error = "Šifra mora sadržati najmanje 8 karaktera, jedno veliko slovo, jedno malo slovo i jedan broj."
            }
            if (!isValidPassword(binding.newPassword2.text.toString())) {
                binding.newPassword2.error = "Šifra mora sadržati najmanje 8 karaktera, jedno veliko slovo, jedno malo slovo i jedan broj."
            }
            if(isValidPassword(binding.newPassword1.text.toString()) && isValidPassword(binding.newPassword2.text.toString())) {
                if(binding.newPassword1.text.toString() != binding.newPassword2.text.toString()) {
                    showToastMessage("Šifre se ne poklapaju.", true)
                }
                if(binding.newPassword1.text.toString() == binding.newPassword2.text.toString()) {
                    changeMyPassword()
                }
            }
        }
    }

    private fun getMyEmail() {
        val sharedPreferences = getSharedPreferences("MyPreferences", Context.MODE_PRIVATE)
        val email = sharedPreferences.getString("email", "email@example.com")
        binding.emailText.text = email
    }

    private fun changeMyPassword() {
        val jsonParams = JSONObject()
        jsonParams.put("oldPassword", binding.currentPassword.text)
        jsonParams.put("newPassword", binding.newPassword1.text)

        val requestQueue: RequestQueue = Volley.newRequestQueue(this)
        val url = StaticAddress.URL + "/web/user/changePassword"


        val jsonObjectRequest = object : JsonObjectRequest(
            Request.Method.POST, url, jsonParams,
            { response ->
                binding.currentPassword.text = null
                binding.newPassword1.text = null
                binding.newPassword2.text = null
                showToastMessage("Promene su sačuvane.", false)

                val sharedPreferences = getSharedPreferences("MyPreferences", Context.MODE_PRIVATE)
                val editor = sharedPreferences.edit()
                editor.putString("password", binding.newPassword1.text.toString())
                editor.apply()
            },
            { error ->
                showToastMessage("Trenutna šifra nije ispravna.", true)
            }) {
            @Throws(AuthFailureError::class)
            override fun getHeaders(): Map<String, String> {
                val headers = HashMap<String, String>()

                val sharedPreferences = getSharedPreferences("MyPreferences", Context.MODE_PRIVATE)
                val jwtToken = sharedPreferences.getString("JWT_TOKEN", "token")

                //headers["Authorization"] = "Bearer eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJtaWxldGljbWlsb3MyMDAxQGdtYWlsLmNvbSIsInJvbGVzIjoiUk9MRV9DVVNUT01FUiIsImp0aSI6Ijk4Zjc5NzFjLTU4ZDItNDAzNC1hYjBkLWY4NTFkMWI5OGMxNSIsImV4cCI6MTcwMTM3NjA5MiwiaXNzIjoiLTE5ODY5NzI5MjIifQ.hyYg1-8kkpNAPz8rvZ3Xy8AeWv00yKH9l1wm7fiZQBty7UEpT1lpJlWNPy21ZOGmWG64AHciNlZHzOw9PBJ7Bg"
                headers["Authorization"] = "Bearer " + jwtToken.toString()
                return headers
            }
        }

        jsonObjectRequest.retryPolicy = DefaultRetryPolicy(0, 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT)
        requestQueue.add(jsonObjectRequest)
    }

    private fun showToastMessage(message: String, isError: Boolean) {
        val toast = Toast.makeText(this@MyPasswordActivity, message, Toast.LENGTH_LONG)
        val view = toast.view
        view?.setBackgroundColor(if (isError) Color.RED else Color.GREEN)
        val text = view?.findViewById<TextView>(android.R.id.message)
        text?.setTextColor(Color.WHITE)
        toast.show()
    }

    private fun isValidPassword(password: String): Boolean {
        return (password.length >= 8) &&
                password.contains(Regex(".*[A-Z].*")) &&
                password.contains(Regex(".*[a-z].*")) &&
                password.contains(Regex(".*\\d.*"))
    }
}
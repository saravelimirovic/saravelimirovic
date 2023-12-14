package com.example.myapplication.ui

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.text.InputType
import android.util.Log
import android.util.Patterns
import android.view.View
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.AuthFailureError
import com.android.volley.DefaultRetryPolicy
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.example.myapplication.R
import com.example.myapplication.databinding.ActivitySetNewPasswordBinding
import com.example.myapplication.utils.StaticAddress
import org.json.JSONException
import org.json.JSONObject

class SetNewPasswordActivity: AppCompatActivity() {

    private lateinit var binding: ActivitySetNewPasswordBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivitySetNewPasswordBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val receivedEmail = intent.getStringExtra("EMAIL")

        binding.changePasswordButton.setOnClickListener {
            if(binding.password.text.toString().isEmpty()) {
                binding.password.error = "Unesite šifru."
            }
            if(binding.passwordConfirm.text.toString().isEmpty()) {
                binding.passwordConfirm.error = "Unesite šifru."
            }
            if (!isValidPassword(binding.password.text.toString())) {
                binding.password.error = "Šifra mora sadržati najmanje 8 karaktera, jedno veliko slovo, jedno malo slovo i jedan broj."
            }
            if (!isValidPassword(binding.passwordConfirm.text.toString())) {
                binding.passwordConfirm.error = "Šifra mora sadržati najmanje 8 karaktera, jedno veliko slovo, jedno malo slovo i jedan broj."
            }
            if(isValidPassword(binding.password.text.toString()) && isValidPassword(binding.passwordConfirm.text.toString())) {
                if(binding.password.text.toString() != binding.passwordConfirm.text.toString()) {
                    showToastMessage("Šifre se ne poklapaju.", true)
                }
                if(binding.password.text.toString() == binding.passwordConfirm.text.toString()) {
                    changePassword(receivedEmail.toString(), binding.passwordConfirm.text.toString())
                }
            }
        }

        binding.toggleButton1.setOnClickListener{
            var inputType = binding.password.inputType

            if (inputType == InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD) {
                binding.password.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
            }
            else {
                binding.password.inputType = InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
            }
            binding.password.setSelection(binding.password.text.length)
        }

        binding.toggleButton2.setOnClickListener{
            var inputType = binding.passwordConfirm.inputType

            if (inputType == InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD) {
                binding.passwordConfirm.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
            }
            else {
                binding.passwordConfirm.inputType = InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
            }
            binding.passwordConfirm.setSelection(binding.passwordConfirm.text.length)
        }
    }

    private fun isValidPassword(password: String): Boolean {
        return (password.length >= 8) &&
                password.contains(Regex(".*[A-Z].*")) &&
                password.contains(Regex(".*[a-z].*")) &&
                password.contains(Regex(".*\\d.*"))
    }

    private fun showToastMessage(message: String, isError: Boolean) {
        val toast = Toast.makeText(this@SetNewPasswordActivity, message, Toast.LENGTH_LONG)
        val view = toast.view
        view?.setBackgroundColor(if (isError) Color.RED else Color.GREEN)
        val text = view?.findViewById<TextView>(android.R.id.message)
        text?.setTextColor(Color.WHITE)
        toast.show()
    }

    private fun changePassword(email: String, password: String) {
        val jsonParams = JSONObject()
        jsonParams.put("email", email)
        jsonParams.put("lozinka", password)

        val requestQueue: RequestQueue = Volley.newRequestQueue(this)
        val url = StaticAddress.URL + "/email/reset_password"

        val jsonObjectRequest = JsonObjectRequest(Request.Method.POST, url, jsonParams,
            { response ->
                val intent = Intent(this, LoginActivity::class.java)
                startActivity(intent)
            },
            { error ->
                showToastMessage("Došlo je do greške, ukoliko nemate nalog registrujte se.", true)
            })
        jsonObjectRequest.retryPolicy = DefaultRetryPolicy(0, 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT)

        requestQueue.add(jsonObjectRequest)
    }
}
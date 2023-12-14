package com.example.myapplication.ui

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Patterns
import android.view.View
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.DefaultRetryPolicy
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.example.myapplication.R
import com.example.myapplication.databinding.ActivityForgotPasswordVerificationCodeBinding
import com.example.myapplication.utils.StaticAddress
import org.json.JSONObject

class ForgotPasswordVerificationActivity: AppCompatActivity() {

    private lateinit var binding: ActivityForgotPasswordVerificationCodeBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityForgotPasswordVerificationCodeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.sendCodeButton.setOnClickListener {
            binding.textForCode.visibility = View.INVISIBLE
            sendCodeToEmail()
        }

        binding.checkCodeButton.setOnClickListener {
            checkCode()
        }

        binding.verificationResend.setOnClickListener {
            binding.textForCode.visibility = View.INVISIBLE
            sendCodeToEmail()
        }
    }

    private fun isValidEmail(email: String): Boolean {
        return Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    private fun showToastMessage(message: String, isError: Boolean) {
        val toast = Toast.makeText(this@ForgotPasswordVerificationActivity, message, Toast.LENGTH_LONG)
        val view = toast.view
        view?.setBackgroundColor(if (isError) Color.RED else Color.GREEN)
        val text = view?.findViewById<TextView>(android.R.id.message)
        text?.setTextColor(Color.WHITE)
        toast.show()
    }

    private fun sendCodeToEmail() {
        if(binding.loginEmail.text.toString().isEmpty()) {
            binding.loginEmail.error = "Unesite email."
        }
        else if(!isValidEmail(binding.loginEmail.text.toString())) {
            binding.loginEmail.error = "Neispravna email adresa."
        }
        if(isValidEmail(binding.loginEmail.text.toString())) {
            val Email = binding.loginEmail.text.toString()
            val requestQueue: RequestQueue = Volley.newRequestQueue(this)
            val url = StaticAddress.URL + "/email/check_user?email=$Email"

            val jsonObjectRequest = JsonObjectRequest(
                Request.Method.GET, url, null,
                { response ->
                    showToastMessage("Pogrešan email ili šifra. Ako nemate nalog, registrujte se.", true)
                },
                { error ->
                    performVerification()
                }
            )
            jsonObjectRequest.retryPolicy = DefaultRetryPolicy(0, 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT)
            requestQueue.add(jsonObjectRequest)
        }
    }

    private fun performVerification() {
        val Email = binding.loginEmail.text.toString()
        val jsonParams = JSONObject()
        jsonParams.put("email", Email)

        val requestQueue: RequestQueue = Volley.newRequestQueue(this)
        val url = StaticAddress.URL + "/email/send_code"

        val jsonObjectRequest = JsonObjectRequest(
            Request.Method.POST, url, jsonParams,
            { response ->
                //Toast.makeText(this@RegisterActivity, "Response " + response.toString(), Toast.LENGTH_LONG).show()
                binding.textForCode.visibility = View.VISIBLE
            },
            { error ->
                showToastMessage("Problem sa bazom, pokušajte kasnije.", false)
            })
        jsonObjectRequest.retryPolicy = DefaultRetryPolicy(0, 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT)

        requestQueue.add(jsonObjectRequest)
    }

    private fun checkCode() {
        val Email = binding.loginEmail.text.toString()
        var Code = ""
        val verfCard1EditText = findViewById<EditText>(R.id.verf_card1)
        val verfCard2EditText = findViewById<EditText>(R.id.verf_card2)
        val verfCard3EditText = findViewById<EditText>(R.id.verf_card3)
        val verfCard4EditText = findViewById<EditText>(R.id.verf_card4)

        val verfCard1 = verfCard1EditText.text.toString().trim()
        val verfCard2 = verfCard2EditText.text.toString().trim()
        val verfCard3 = verfCard3EditText.text.toString().trim()
        val verfCard4 = verfCard4EditText.text.toString().trim()

        Code = verfCard1 + verfCard2 + verfCard3 + verfCard4

        val jsonParams = JSONObject()
        jsonParams.put("email", Email)
        jsonParams.put("code", Code)

        val requestQueue: RequestQueue = Volley.newRequestQueue(this)
        val url = StaticAddress.URL + "/email/check_code"

        val jsonObjectRequest = JsonObjectRequest(
            Request.Method.POST, url, jsonParams,
            { response ->
                val intent = Intent(this, SetNewPasswordActivity::class.java)
                intent.putExtra("EMAIL", Email)
                startActivity(intent)
            },
            { error ->
                showToastMessage("Kod nije ispravan, pokušajte ponovo.", true)
                binding.textForCode.visibility = View.INVISIBLE
            })
        jsonObjectRequest.retryPolicy = DefaultRetryPolicy(0, 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT)

        requestQueue.add(jsonObjectRequest)
    }
}
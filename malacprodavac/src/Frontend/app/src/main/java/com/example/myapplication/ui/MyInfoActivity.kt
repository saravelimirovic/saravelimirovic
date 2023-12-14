package com.example.myapplication.ui

import android.content.Context
import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.view.View
import android.widget.TextView
import android.widget.Toast
import com.android.volley.AuthFailureError
import com.android.volley.DefaultRetryPolicy
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.example.myapplication.R
import com.example.myapplication.databinding.ActivityMyInfoBinding
import com.example.myapplication.databinding.ActivityUserAccountBinding
import com.example.myapplication.utils.StaticAddress
import org.json.JSONObject

class MyInfoActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMyInfoBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMyInfoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        getMyInfo()

        binding.goBack.setOnClickListener {
            val intent = Intent(this, UserAccountActivity::class.java)
            startActivity(intent)
        }

        binding.saveChangesButton.setOnClickListener {
            if(validInputFields())
                setNewInfo()
        }
    }

    private fun validInputFields(): Boolean {
        if (binding.firstName.text.isEmpty()) {
            binding.firstName.error = "Unesite ime."
            return false
        }
        if (binding.lastName.text.isEmpty()) {
            binding.lastName.error = "Unesite prezime."
            return false
        }
        if (binding.phoneNumber.text.isEmpty()) {
            binding.phoneNumber.error = "Unesite broj telefona."
            return false
        }

        if (!onlyNumbers(binding.phoneNumber.text.toString())) {
            binding.phoneNumber.error = "Dozvoljeni su samo brojevi."
            return false
        }

        return true
    }

    private fun onlyNumbers(string: String): Boolean {
        return string.contains(Regex("^\\d+\$"))
    }

    private fun getMyInfo() {
        val requestQueue: RequestQueue = Volley.newRequestQueue(this)
        val url = StaticAddress.URL + "/web/user/getUpdateInfo"

        val jsonObjectRequest = object : JsonObjectRequest(
            Request.Method.GET, url, null,
            { response ->
                val firstName: Editable = Editable.Factory.getInstance().newEditable(response.getString("firstName"))
                binding.firstName.text = firstName

                val lastName: Editable = Editable.Factory.getInstance().newEditable(response.getString("lastName"))
                binding.lastName.text = lastName

                val phoneNumber: Editable = Editable.Factory.getInstance().newEditable(response.getString("phoneNumber"))
                binding.phoneNumber.text = phoneNumber
            },
            { error ->
                showToastMessage("Došlo je do greške, pokušajte ponovo.", true)
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

    private fun setNewInfo() {
        val jsonParams = JSONObject()
        jsonParams.put("firstName", binding.firstName.text)
        jsonParams.put("lastName", binding.lastName.text)
        jsonParams.put("phoneNumber", binding.phoneNumber.text)

        val requestQueue: RequestQueue = Volley.newRequestQueue(this)
        val url = StaticAddress.URL + "/web/user/updateInfo"

        val jsonObjectRequest = object : JsonObjectRequest(
            Request.Method.POST, url, jsonParams,
            { response ->

                if (response.getInt("Message") == 1) {
                    showToastMessage("Promene su sačuvane.", false)
                }

                val sharedPreferences = getSharedPreferences("MyPreferences", Context.MODE_PRIVATE)
                val editor = sharedPreferences.edit()
                editor.putString("firstName", binding.firstName.text.toString())
                editor.putString("lastName", binding.lastName.text.toString())
                editor.apply()
            },
            { error ->
                showToastMessage("Došlo je do greške, pokušajte ponovo.", true)
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

        jsonObjectRequest.retryPolicy = DefaultRetryPolicy(0, 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT)
        requestQueue.add(jsonObjectRequest)
    }

    private fun showToastMessage(message: String, isError: Boolean) {
        val toast = Toast.makeText(this@MyInfoActivity, message, Toast.LENGTH_LONG)
        val view = toast.view
        view?.setBackgroundColor(if (isError) Color.RED else Color.GREEN)
        val text = view?.findViewById<TextView>(android.R.id.message)
        text?.setTextColor(Color.WHITE)
        toast.show()
    }
}
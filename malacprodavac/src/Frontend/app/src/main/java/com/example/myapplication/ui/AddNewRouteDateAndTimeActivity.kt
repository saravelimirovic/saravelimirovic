package com.example.myapplication.ui

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.example.myapplication.R
import java.util.Calendar
import android.app.TimePickerDialog
import android.content.Context
import android.content.Intent
import android.view.MotionEvent
import android.widget.*
import com.android.volley.AuthFailureError
import com.android.volley.DefaultRetryPolicy
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.example.myapplication.utils.StaticAddress
import org.json.JSONObject

class AddNewRouteDateAndTimeActivity : AppCompatActivity() {

    private lateinit var startDateEditText: EditText
    private lateinit var timeEditText: EditText
    private lateinit var nextStepButton: Button

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_new_route_date_and_time)

        startDateEditText = findViewById(R.id.date)
        timeEditText = findViewById(R.id.time)
        nextStepButton = findViewById(R.id.nextStepButton)

        startDateEditText.showSoftInputOnFocus = false
        timeEditText.showSoftInputOnFocus = false

        startDateEditText.setOnTouchListener { _, event ->
            if (event.action == MotionEvent.ACTION_UP) {
                showDatePickerDialog(startDateEditText)
            }
            true
        }

        timeEditText.setOnTouchListener { _, event ->
            if (event.action == MotionEvent.ACTION_UP) {
                showTimePickerDialog(timeEditText)
            }
            true
        }

        nextStepButton.setOnClickListener {
            if(isInputValid()) {
                val cityStart = intent.getStringExtra("cityStart").toString()
                val postalCodeStart = intent.getStringExtra("postalCodeStart").toString()
                val cityEnd = intent.getStringExtra("cityEnd").toString()
                val postalCodeEnd = intent.getStringExtra("postalCodeEnd").toString()
                sendLocationInfo(cityStart, postalCodeStart, cityEnd, postalCodeEnd)
            }
        }
    }

    private fun sendLocationInfo(cityStart: String, postalCodeStart: String, cityEnd: String, postalCodeEnd: String) {
        val jsonParams = JSONObject()
        jsonParams.put("cityStart", cityStart)
        jsonParams.put("postalCodeStart", postalCodeStart)
        jsonParams.put("cityEnd", cityEnd)
        jsonParams.put("postalCodeEnd", postalCodeEnd)

        val requestQueue: RequestQueue = Volley.newRequestQueue(this)
        val url = StaticAddress.URL + "/web/user/route"

        val jsonObjectRequest = object : JsonObjectRequest(
            Request.Method.POST, url, jsonParams,
            { response ->
                val responseBody = response.toString()
                val jsonResponse = JSONObject(responseBody)

                val intent = Intent(this, ShowRouteActivity::class.java)
                intent.putExtra("lonStart", jsonResponse.getDouble("lonStart"))
                intent.putExtra("latStart", jsonResponse.getDouble("latStart"))
                intent.putExtra("lonEnd", jsonResponse.getDouble("lonEnd"))
                intent.putExtra("latEnd", jsonResponse.getDouble("latEnd"))
                startActivity(intent)
            },
            { error ->

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

    fun showDatePickerDialog(v: View) {
        val c = Calendar.getInstance()
        val year = c.get(Calendar.YEAR)
        val month = c.get(Calendar.MONTH)
        val day = c.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(
            this,
            DatePickerDialog.OnDateSetListener { view: DatePicker, year: Int, monthOfYear: Int, dayOfMonth: Int ->
                // Postavljanje odabranog datuma u EditText
                startDateEditText.setText("$dayOfMonth/${monthOfYear + 1}/$year")
            },
            year, month, day
        )

        datePickerDialog.setButton(TimePickerDialog.BUTTON_NEGATIVE, "Odustani") { _, _ -> }

        datePickerDialog.show()
    }

    fun showTimePickerDialog(v: View) {
        val c = Calendar.getInstance()
        val hour = c.get(Calendar.HOUR_OF_DAY)
        val minute = c.get(Calendar.MINUTE)

        val timePickerDialog = TimePickerDialog(
            this,
            TimePickerDialog.OnTimeSetListener { view: TimePicker, hourOfDay: Int, minute: Int ->
                timeEditText.setText(String.format("%02d:%02d", hourOfDay, minute))
            },
            hour, minute, true
        )

        timePickerDialog.setButton(TimePickerDialog.BUTTON_NEGATIVE, "Odustani") { _, _ -> }

        timePickerDialog.show()
    }

    private fun isInputValid(): Boolean {
        val dateEditText = findViewById<EditText>(R.id.date)
        val timeEditText = findViewById<EditText>(R.id.time)

        val date = findViewById<EditText>(R.id.date).text.toString().trim()
        val time = findViewById<EditText>(R.id.time).text.toString().trim()

        if(date.isEmpty()) {
            dateEditText.error = "Unesite datum polaska."
            return false
        }
        else
            dateEditText.error = null

        if(time.isEmpty()) {
            timeEditText.error = "Unesite vreme polaska."
            return false
        }
        else
            timeEditText.error = null

        return true
    }
}

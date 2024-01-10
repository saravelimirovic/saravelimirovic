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
import androidx.drawerlayout.widget.DrawerLayout
import com.android.volley.AuthFailureError
import com.android.volley.DefaultRetryPolicy
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.etebarian.meowbottomnavigation.MeowBottomNavigation
import com.example.myapplication.utils.StaticAddress
import com.google.android.material.navigation.NavigationView
import org.json.JSONObject

class AddNewRouteDateAndTimeActivity : AppCompatActivity() {

    private lateinit var startDateEditText: EditText
    private lateinit var timeEditText: EditText
    private lateinit var nextStepButton: Button

    protected final val home = 1
    protected final val search = 2
    protected final val add = 3
    protected final val notf = 4
    protected final val account = 5
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

        val goBack = findViewById<ImageButton>(R.id.goBack)
        goBack.setOnClickListener {
            val intent = Intent(this, EnterRouteActivity::class.java)
            startActivity(intent)
        }

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        val drawerLayout: DrawerLayout = findViewById(R.id.drawerLayout)
        val navView: NavigationView = findViewById<NavigationView>(R.id.drawer_view)

        val sharedPreferences = getSharedPreferences("MyPreferences", Context.MODE_PRIVATE)
        val role = sharedPreferences.getString("role", "empty")

        if (role == "Dostavljač") {
            navView.menu.removeItem(R.id.kupovina1)
            navView.menu.removeItem(R.id.nalog1)
        }

        if (role == "Prodavac") {
            navView.menu.removeItem(R.id.kupovina2)
            navView.menu.removeItem(R.id.nalog2)
        }
        if (role == "Kupac") {
            navView.menu.removeItem(R.id.kupovina2)
            navView.menu.removeItem(R.id.kupovina1)
            navView.menu.removeItem(R.id.nalog1)
        }

        navView.setNavigationItemSelectedListener {
            when(it.itemId){

                R.id.drawer_home -> {
                    startActivity(Intent(this, HomeActivity::class.java))
                    finish()
                }

                R.id.drawer_explore -> {
                    startActivity(Intent(this, ExploreActivity::class.java))
                    finish()
                }

                R.id.drawer_notf -> {
                    startActivity(Intent(this, NotificationsActivity::class.java))
                    finish()
                }

                R.id.drawer_profile -> {
                    startActivity(Intent(this, UserProfileActivity::class.java))
                    finish()
                }

                R.id.drawer_account1 -> {
                    startActivity(Intent(this, UserAccountActivity::class.java))
                    finish()
                }

                R.id.drawer_account2 -> {
                    startActivity(Intent(this, UserAccountActivity::class.java))
                    finish()
                }

                R.id.drawer_logout -> {
                    val sharedPreferences = getSharedPreferences("MyPreferences", Context.MODE_PRIVATE)
                    val editor = sharedPreferences.edit()

                    editor.remove("JWT_TOKEN")
                    editor.remove("firstName")
                    editor.remove("lastName")
                    editor.remove("email")
                    editor.remove("password")
                    editor.remove("companyAdded")
                    editor.apply()

                    val intent = Intent(this, LoginActivity::class.java)
                    startActivity(intent)
                }

            }
            true
        }

        try {
            @SuppressLint("MissingInflatedId", "LocalSuppress")
            val bottomNav = findViewById<MeowBottomNavigation>(R.id.meowBottomNav)
            bottomNav.add(MeowBottomNavigation.Model(home, R.drawable.home_fill0_wght400_grad0_opsz24))
            bottomNav.add(MeowBottomNavigation.Model(search, R.drawable.search_black_24dp__1_))

            val sharedPreferences = getSharedPreferences("MyPreferences", Context.MODE_PRIVATE)
            val role = sharedPreferences.getString("role", "empty")

            if (role == "Dostavljač" || role == "Prodavac") {
                bottomNav.add(MeowBottomNavigation.Model(add, R.drawable.round_add_24))
            }

            bottomNav.add(MeowBottomNavigation.Model(notf, R.drawable.notifications_fill0_wght400_grad0_opsz24))
            bottomNav.add(MeowBottomNavigation.Model(account, R.drawable.account_circle_fill0_wght400_grad0_opsz24))

            bottomNav.setOnClickMenuListener {
                when(it.id) {
                    search -> {
                        val intent   = Intent(this, ExploreActivity::class.java)
                        startActivity(intent)
                    }
                    notf-> {
                        val intent   = Intent(this, NotificationsActivity::class.java)
                        startActivity(intent)
                    }
                    home -> {
                        val intent   = Intent(this, HomeActivity::class.java)
                        startActivity(intent)
                    }

                    account -> {
                        if (role == "Prodavac") {
                            startActivity(Intent(this, UserProfileActivity::class.java))
                        } else {
                            val intent = Intent(this, UserAccountActivity::class.java)
                            intent.putExtra("MyProfile", true)
                            startActivity(intent)
                        }
                    }
                    add -> {
                        if (role == "Dostavljač") {
                            val intent = Intent(this, EnterRouteActivity::class.java)
                            startActivity(intent)
                        }
                        if (role != "Dostavljač" && role != "Kupac") {
                            val intent = Intent(this, AddProductActivity::class.java)
                            startActivity(intent)
                        }
                    }
                }
            }

            bottomNav.show(add)
        } catch (e: Exception) {
            println("ne radi :(((((((((((((")
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
                intent.putExtra("cityStart", cityStart)
                intent.putExtra("cityEnd", cityEnd)
                intent.putExtra("date", startDateEditText.text.toString())
                intent.putExtra("time", timeEditText.text.toString())

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

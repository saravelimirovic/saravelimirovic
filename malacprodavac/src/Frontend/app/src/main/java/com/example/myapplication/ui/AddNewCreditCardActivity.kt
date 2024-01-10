package com.example.myapplication.ui

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.drawerlayout.widget.DrawerLayout
import com.android.volley.AuthFailureError
import com.android.volley.DefaultRetryPolicy
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.etebarian.meowbottomnavigation.MeowBottomNavigation
import com.example.myapplication.R
import com.example.myapplication.databinding.ActivityAddNewCreditCardBinding
import com.example.myapplication.utils.StaticAddress
import com.google.android.material.navigation.NavigationView
import org.json.JSONObject

class AddNewCreditCardActivity: AppCompatActivity() {

    private lateinit var binding: ActivityAddNewCreditCardBinding
    var companyId = 0L
    var details = false

    protected final val home = 1
    protected final val search = 2
    protected final val add = 3
    protected final val notf = 4
    protected final val account = 5
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddNewCreditCardBinding.inflate(layoutInflater)
        setContentView(binding.root)

        companyId = intent.getLongExtra("companyId", 0)
        details = intent.getBooleanExtra("details", false)

        binding.goBack.setOnClickListener {
            val details = intent.getBooleanExtra("details", false)
            if (details == true) {
                val intent = Intent(this,RegisterFinal::class.java)
                startActivity(intent)
            } else {
                val intent = Intent(this, CreditCardListActivity::class.java)
                startActivity(intent)
            }
        }

        binding.addCardButton.setOnClickListener {
            if(validInputFields())
                addCreditCard()
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
            val jwtToken = retrieveTokenFromStorage(this)
            if(jwtToken != null) {

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
                        account-> {
                            if((role.equals("Kupac") || role.equals("Dostavljač")))
                                startActivity(Intent(this, UserAccountActivity::class.java))
                            else {
                                val intent   = Intent(this, UserProfileActivity::class.java)
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
                        home -> {
                            val intent   = Intent(this, HomeActivity::class.java)
                            startActivity(intent)
                        }
                    }
                }

                bottomNav.show(account)
            } else {
                val bottomNav = findViewById<MeowBottomNavigation>(R.id.meowBottomNav)
                bottomNav.visibility = View.GONE
            }
        } catch (e: Exception) {
            println("ne radi :(((((((((((((")
        }
    }

    private fun retrieveTokenFromStorage(context: Context): String? {

        val sharedPreferencesIsUserLoggedIn =
            getSharedPreferences("MyPreferences", Context.MODE_PRIVATE)

        return sharedPreferencesIsUserLoggedIn.getString("JWT_TOKEN", "token")
    }

    private fun validInputFields(): Boolean {
        if (binding.cardName.text.isEmpty()) {
            binding.cardName.error = "Unesite naziv kartice."
            return false
        }
        if (binding.cardNumber.text.isEmpty()) {
            binding.cardNumber.error = "Unesite broj karticie."
            return false
        }
        val regex = Regex("^(\\d{4}-){3}\\d{4}$")
        if(!regex.matches(binding.cardNumber.text.toString())) {
            binding.cardNumber.error = "Ovo polje zahteva 16 cifara i nakon svake četvrte cifre karakter '-'."
            return false
        }
        if (binding.expirationDate.text.isEmpty()) {
            binding.expirationDate.error = "Unesite datum isteka kartice."
            return false
        }
        val regexExpirationDate = Regex("^(0[1-9]|1[0-2])-(20[2-9][0-9])$")
        if(!regexExpirationDate.matches(binding.expirationDate.text.toString())) {
            binding.expirationDate.error = "Ovo polje zahteva format MM-YYYY."
            return false
        }
        if (binding.securityCode.text.isEmpty()) {
            binding.securityCode.error = "Unesite sigurnosni kod kartice."
            return false
        }
        if (binding.securityCode.text.length != 3 || !onlyNumbers(binding.securityCode.text.toString())) {
            binding.securityCode.error = "Ovo polje zahteva 3 cifre."
            return false
        }

        return true
    }

    private fun onlyNumbers(string: String): Boolean {
        return string.contains(Regex("^\\d+\$"))
    }

    private fun addCreditCard() {
        val jsonParams = JSONObject()
        val nameOnCard =  binding.cardName.text.toString()
        jsonParams.put("nameOnCard", binding.cardName.text)
        jsonParams.put("cardNumber", binding.cardNumber.text)
        jsonParams.put("cardExpiration", "01-" + binding.expirationDate.text)
        jsonParams.put("securityCode", binding.securityCode.text)

        val requestQueue: RequestQueue = Volley.newRequestQueue(this)
        val url = StaticAddress.URL + "/web/card/add"

        val jsonObjectRequest = object : JsonObjectRequest(
            Request.Method.POST, url, jsonParams,
            { response ->

                if (response.getInt("Message") == 1) {
                    showToastMessage("Kartica uspešno dodata.", false)
                    if(companyId != 0L) {
                        val newIntent = Intent(this, UserOrderDetails::class.java)
                        newIntent.putExtra("cardName", nameOnCard)
                        newIntent.putExtra("companyId", companyId)
                        startActivity(newIntent)
                    }
                    else if(details) {
                        val newIntent = Intent(this, RegisterFinal::class.java)
                        startActivity(newIntent)
                    }
                    else {
                        val intent = Intent(this, CreditCardListActivity::class.java)
                        startActivity(intent)
                    }
                }
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
        val toast = Toast.makeText(this@AddNewCreditCardActivity, message, Toast.LENGTH_LONG)
        val view = toast.view
        view?.setBackgroundColor(if (isError) Color.RED else Color.GREEN)
        val text = view?.findViewById<TextView>(android.R.id.message)
        text?.setTextColor(Color.WHITE)
        toast.show()
    }
}

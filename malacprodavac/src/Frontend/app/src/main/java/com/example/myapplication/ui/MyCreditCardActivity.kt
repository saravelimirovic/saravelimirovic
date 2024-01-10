package com.example.myapplication.ui

import android.annotation.SuppressLint
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.widget.ListView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.drawerlayout.widget.DrawerLayout
import com.android.volley.AuthFailureError
import com.android.volley.DefaultRetryPolicy
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.etebarian.meowbottomnavigation.MeowBottomNavigation
import com.example.myapplication.R
import com.example.myapplication.adapters.CreditCardAdapter
import com.example.myapplication.databinding.ActivityOneCreditCardBinding
import com.example.myapplication.models.CreditCardListItem
import com.example.myapplication.utils.StaticAddress
import com.google.android.material.navigation.NavigationView

class MyCreditCardActivity : AppCompatActivity() {

    private lateinit var binding: ActivityOneCreditCardBinding

    protected final val home = 1
    protected final val search = 2
    protected final val add = 3
    protected final val notf = 4
    protected final val account = 5
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOneCreditCardBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val id = intent.getLongExtra("id", 0)
        getCard(id)

        binding.deleteCreditCard.setOnClickListener {
            val dialogClickListener =
                DialogInterface.OnClickListener { dialog, which ->
                    when (which) {
                        DialogInterface.BUTTON_POSITIVE -> {
                            deleteCard(id)
                        }

                        DialogInterface.BUTTON_NEGATIVE -> {
                            dialog.dismiss()
                        }
                    }
                }

            val builder: AlertDialog.Builder = AlertDialog.Builder(this)

            builder.setMessage("Da li ste sigurni da želite da izbrišete karticu?")
                .setPositiveButton("Da", dialogClickListener)
                .setNegativeButton("Ne", dialogClickListener)
                .show()
        }

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
                    account-> {
                        if((role.equals("Kupac") || role.equals("Dostavljač")))
                            startActivity(Intent(this, UserAccountActivity::class.java))
                        else {
                            val intent   = Intent(this, UserProfileActivity::class.java)
                            intent.putExtra("MyProfile", true)
                            startActivity(intent)
                        }
                    }

                    home -> {
                        val intent   = Intent(this, HomeActivity::class.java)
                        startActivity(intent)
                    }

                }

                if(role.equals("Dostavljac") || role.equals("Kupac")) {
                    when(it.id) {
                        add ->{

                            if (role.equals("Dostavljač")  && !role.equals("Kupac")) {
                                val intent = Intent(this, EnterRouteActivity::class.java)
                                startActivity(intent)
                            }
                            if (!role.equals("Dostavljač") && !role.equals("Kupac")) {
                                val intent = Intent(this, AddProductActivity::class.java)
                                startActivity(intent)
                            }
                        }
                    }
                }
            }

            bottomNav.show(account)
        } catch (e: Exception) {
            println("ne radi :(((((((((((((")
        }
    }

    private fun getCard(id: Long) {
        val requestQueue: RequestQueue = Volley.newRequestQueue(this)
        val url = StaticAddress.URL + "/web/card/$id"

        val jsonObjectRequest = object : JsonObjectRequest(
            Request.Method.GET, url, null,
            { response ->
                val nameOnCard: Editable = Editable.Factory.getInstance().newEditable(response.getString("nameOnCard"))
                binding.cardName.text = nameOnCard

                val cardNumber: Editable = Editable.Factory.getInstance().newEditable(response.getString("cardNumber"))
                binding.cardNumber.text = cardNumber

                val cardExpiration: Editable = Editable.Factory.getInstance().newEditable(response.getString("cardExpiration"))
                binding.expirationDate.text = cardExpiration

                val securityCode: Editable = Editable.Factory.getInstance().newEditable(response.getString("securityCode"))
                binding.securityCode.text = securityCode
            },
            { error ->
                //Toast.makeText(this, error.message, Toast.LENGTH_LONG).show()
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

    private fun deleteCard(id: Long) {
        val requestQueue: RequestQueue = Volley.newRequestQueue(this)
        val url = StaticAddress.URL + "/web/card/deleteCard/$id"

        val jsonObjectRequest = object : JsonObjectRequest(
            Request.Method.DELETE, url, null,
            { response ->
                Toast.makeText(this, "Kartica je uspešno obrisana.", Toast.LENGTH_LONG).show()
                val intent = Intent(this, CreditCardListActivity::class.java)
                startActivity(intent)
            },
            { error ->
                //Toast.makeText(this, error.message, Toast.LENGTH_LONG).show()
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
}
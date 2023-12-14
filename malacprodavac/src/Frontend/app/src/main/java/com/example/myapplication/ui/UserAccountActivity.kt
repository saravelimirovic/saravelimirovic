package com.example.myapplication.ui

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.myapplication.databinding.ActivityUserAccountBinding
import android.content.DialogInterface
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.android.volley.AuthFailureError
import com.android.volley.DefaultRetryPolicy
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.etebarian.meowbottomnavigation.MeowBottomNavigation
import com.example.myapplication.R
import com.example.myapplication.utils.StaticAddress

class UserAccountActivity : AppCompatActivity() {

    protected final val home = 1
    protected final val search = 2
    protected final val add = 3
    protected final val notf = 4
    protected final val account = 5
    private lateinit var binding: ActivityUserAccountBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUserAccountBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val sharedPreferences = getSharedPreferences("MyPreferences", Context.MODE_PRIVATE)
        val firstName = sharedPreferences.getString("firstName", "")
        val lastName = sharedPreferences.getString("lastName", "")
        val role = sharedPreferences.getString("role", "")

        binding.fullName.text = "$firstName $lastName"
        binding.role.text = "$role"

        binding.goBack.setOnClickListener {
            val intent = Intent(this, AddPayementMethod::class.java)
            startActivity(intent)
        }

        binding.myInfo.setOnClickListener {
            val intent = Intent(this, MyInfoActivity::class.java)
            startActivity(intent)
        }

        binding.myLocation.setOnClickListener {
            val intent = Intent(this, MyLocationActivity::class.java)
            startActivity(intent)
        }

        binding.myPassword.setOnClickListener {
            val intent = Intent(this, MyPasswordActivity::class.java)
            startActivity(intent)
        }

        binding.paymentMethods.setOnClickListener {
            val intent = Intent(this, CreditCardListActivity::class.java)
            startActivity(intent)
        }

        binding.deleteUser.setOnClickListener {
            val dialogClickListener =
                DialogInterface.OnClickListener { dialog, which ->
                    when (which) {
                        DialogInterface.BUTTON_POSITIVE -> {
                            deleteUser()
                        }

                        DialogInterface.BUTTON_NEGATIVE -> {
                            dialog.dismiss()
                        }
                    }
                }

            val builder: AlertDialog.Builder = AlertDialog.Builder(this)

            builder.setMessage("Da li ste sigurni da želite da obrišete ovaj nalog? Nećete moći da ga povratite.")
                .setPositiveButton("Da", dialogClickListener)
                .setNegativeButton("Ne", dialogClickListener)
                .show()
        }

        binding.layoutUserInfo.setOnClickListener {
            val intent = Intent(this, MyInfoActivity::class.java)
            startActivity(intent)
        }

        binding.layoutUserLocation.setOnClickListener {
            val intent = Intent(this, MyLocationActivity::class.java)
            startActivity(intent)
        }

        binding.layoutUserSecurity.setOnClickListener {
            val intent = Intent(this, MyPasswordActivity::class.java)
            startActivity(intent)
        }

        binding.layoutUserPaymentMethods.setOnClickListener {
            val intent = Intent(this, CreditCardListActivity::class.java)
            startActivity(intent)
        }

        binding.layoutUserDeleteAcc.setOnClickListener {
            val dialogClickListener =
                DialogInterface.OnClickListener { dialog, which ->
                    when (which) {
                        DialogInterface.BUTTON_POSITIVE -> {
                            deleteUser()
                        }

                        DialogInterface.BUTTON_NEGATIVE -> {
                            dialog.dismiss()
                        }
                    }
                }

            val builder: AlertDialog.Builder = AlertDialog.Builder(this)

            builder.setMessage("Da li ste sigurni da želite da obrišete ovaj nalog? Nećete moći da ga povratite.")
                .setPositiveButton("Da", dialogClickListener)
                .setNegativeButton("Ne", dialogClickListener)
                .show()
        }

        try {
            @SuppressLint("MissingInflatedId", "LocalSuppress")
            val bottomNav = findViewById<MeowBottomNavigation>(R.id.meowBottomNav)
            bottomNav.add(MeowBottomNavigation.Model(home, R.drawable.home_fill0_wght400_grad0_opsz24))
            bottomNav.add(MeowBottomNavigation.Model(search, R.drawable.search_black_24dp__1_))
            bottomNav.add(MeowBottomNavigation.Model(add, R.drawable.round_add_24))
            bottomNav.add(MeowBottomNavigation.Model(notf, R.drawable.notifications_fill0_wght400_grad0_opsz24))
            bottomNav.add(MeowBottomNavigation.Model(account, R.drawable.account_circle_fill0_wght400_grad0_opsz24))

            bottomNav.setOnClickMenuListener {
                when(it.id) {

                    home -> {
                        val intent   = Intent(this, HomeActivity::class.java)
                        startActivity(intent)
                    }
                    notf-> {
                        val intent   = Intent(this, NotificationsActivity::class.java)
                        startActivity(intent)
                    }
                    add-> {
                        val intent   = Intent(this, AddPayementMethod::class.java)
                        startActivity(intent)
                    }
                    search -> {
                        val intent   = Intent(this, ExploreActivity::class.java)
                        startActivity(intent)
                    }

                }
            }

            bottomNav.show(account)
        } catch (e: Exception) {
            println("ne radi :(((((((((((((")
        }

    }

    private fun deleteUser() {
        val requestQueue: RequestQueue = Volley.newRequestQueue(this)
        val url = StaticAddress.URL + "/web/user/deleteUser"

        val jsonObjectRequest = object : JsonObjectRequest(
            Request.Method.DELETE, url, null,
            { response ->
                val sharedPreferences = getSharedPreferences("MyPreferences", Context.MODE_PRIVATE)
                val editor = sharedPreferences.edit()

                editor.remove("JWT_TOKEN")
                editor.remove("firstName")
                editor.remove("lastName")
                editor.remove("email")
                editor.remove("password")
                editor.apply()

                val intent = Intent(this, LoginActivity::class.java)
                startActivity(intent)
            },
            { error ->
                Toast.makeText(this, error.message, Toast.LENGTH_LONG).show()
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
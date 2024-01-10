package com.example.myapplication.ui

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Spinner
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import com.example.myapplication.R

class RegisterFinal : AppCompatActivity() {

    var companyCreated = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register_final)

        val locationButton: Button = findViewById(R.id.locationButton)
        val paymentButton: Button = findViewById(R.id.paymentButton)
        val companyButton: Button = findViewById(R.id.companyButton)
        val additionalButton: Button = findViewById(R.id.additionalButton)
        val companyLayout: ConstraintLayout = findViewById(R.id.constraintLayout3)
        companyCreated = intent.getBooleanExtra("companyCreated", false)

        val sharedPreferences = getSharedPreferences("MyPreferences", Context.MODE_PRIVATE)
        val role = sharedPreferences.getString("role", "empty")
        val companyAdded = sharedPreferences.getBoolean("companyAdded", false)

        if((role.equals("Kupac") || role.equals("Dostavljač"))){
            companyLayout.visibility = View.GONE
            companyCreated = true
        }

        locationButton.setOnClickListener {
            val intent = Intent(this, MyLocationActivity::class.java)
            intent.putExtra("details", true)
            startActivity(intent)
        }

        paymentButton.setOnClickListener {
            val intent = Intent(this, AddNewCreditCardActivity::class.java)
            intent.putExtra("details", true)
            startActivity(intent)
        }

        companyButton.setOnClickListener {
            val intent = Intent(this, AddCompanyActivity::class.java)
            intent.putExtra("details", true)
            startActivity(intent)
        }

        additionalButton.setOnClickListener {
            if(companyAdded) {
                val intent = Intent(this, HomeActivity::class.java)
                startActivity(intent)
            }
            else if(role.equals("Kupac") || role.equals("Dostavljač")) {
                val intent = Intent(this, HomeActivity::class.java)
                startActivity(intent)
            }
            else
                Toast.makeText(this, "Kreirajte svoju kompaniju.", Toast.LENGTH_LONG).show()
        }
    }
}
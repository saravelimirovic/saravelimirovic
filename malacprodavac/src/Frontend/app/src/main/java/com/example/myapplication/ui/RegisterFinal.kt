package com.example.myapplication.ui

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Spinner
import com.example.myapplication.R

class RegisterFinal : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register_final)

        val locationButton: Button = findViewById(R.id.locationButton)
        val paymentButton: Button = findViewById(R.id.paymentButton)
        val companyButton: Button = findViewById(R.id.companyButton)
        val additionalButton: Button = findViewById(R.id.additionalButton)

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
            val intent = Intent(this, HomeActivity::class.java)
            startActivity(intent)
        }
    }
}
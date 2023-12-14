package com.example.myapplication.ui

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ListView
import com.example.myapplication.R
import com.example.myapplication.adapters.AvailableDeliveryManAdapter
import com.example.myapplication.adapters.CreditCardAdapter
import com.example.myapplication.models.AvailableDeliveryMan
import com.example.myapplication.models.CreditCardListItem
import java.util.Date

class AvailableDeliverymanActivity : AppCompatActivity() {
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_available_deliveryman)

        /*
            val availableDeliveryManList: MutableList<AvailableDeliveryMan> = ArrayList()

            availableDeliveryManList.add(AvailableDeliveryMan("Pera", R.drawable.avatar.toString(), Date()))

            val adapter = AvailableDeliveryManAdapter(this, R.layout.activity_one_available_deliveryman, availableDeliveryManList)
            val listView: ListView = findViewById(R.id.availableDeliveryManListView)
            listView.adapter = adapter*/
    }
}
package com.example.myapplication.ui

import android.annotation.SuppressLint
import android.content.Intent
import android.media.Image
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.CheckBox
import android.widget.CompoundButton
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ListView
import android.widget.RadioButton
import android.widget.TextView
import android.widget.Toast
import androidx.compose.ui.graphics.Color
import com.example.myapplication.R
import com.example.myapplication.adapters.OrderDetailsOneOrderAdapter
import com.example.myapplication.adapters.UserOrderDetailsOneOrderAdapter
import com.example.myapplication.databinding.ActivityOrderDetailsBinding
import com.example.myapplication.databinding.ActivityUserOrderDetailsBinding
import com.example.myapplication.models.OrderDetailsOneOrder

class UserOrderDetails : AppCompatActivity() {
    private lateinit var binding: ActivityUserOrderDetailsBinding
    @SuppressLint("MissingInflatedId", "UseCompatLoadingForDrawables")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_order_details)
        binding = ActivityUserOrderDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val backButton : Button = findViewById(R.id.go_back)
        backButton.setOnClickListener {
            val intent = Intent(this, OrdersActivity::class.java)
            startActivity(intent)
        }

        val orderList: MutableList<OrderDetailsOneOrder> = ArrayList()
        orderList.add(OrderDetailsOneOrder(1,"Kifla", 5, "kg", 99.00))
        orderList.add(OrderDetailsOneOrder(2,"Kifla2", 5, "kg", 99.00))
        orderList.add(OrderDetailsOneOrder(3,"Kifla3", 5, "kg", 99.00))
        orderList.add(OrderDetailsOneOrder(3,"Kifla3", 5, "kg", 99.00))
        orderList.add(OrderDetailsOneOrder(3,"Kifla3", 5, "kg", 99.00))
        orderList.add(OrderDetailsOneOrder(3,"Kifla3", 5, "kg", 99.00))

        val adapter = UserOrderDetailsOneOrderAdapter(this, R.layout.user_one_ordered_item, orderList)
        val listView: ListView = findViewById(R.id.userOrderItemsListView)
        listView.adapter = adapter

        //---------------------- Show list of credit cards --------------------------------//
        var showListOfCreditCards = findViewById<ImageButton>(R.id.showListOffCreditCards)
        showListOfCreditCards.setOnClickListener {
            var intent = Intent(this, CreditCardListActivity::class.java)
            startActivity(intent)
        }

        //---------------------- DELIVERY & PICK UP BUTTON LISTENERS ----------------------//
        var deliveryButton = findViewById<TextView>(R.id.deliveryButton)
        var licnoPreuzimanjeButton = findViewById<TextView>(R.id.licnoPreuzimanjeButton)
        val deliveryLayout = findViewById<LinearLayout>(R.id.deliveryInfoLayout)
        val lpLayout = findViewById<LinearLayout>(R.id.licnoPreuzimanjeLayout)

        val checkBox = findViewById<CheckBox>(R.id.paymentCheckBox)
        checkBox.setOnClickListener { view ->
            if(view is CheckBox) {
                var paymentCard = findViewById<LinearLayout>(R.id.layoutPaymentCard)
                var pomLayout = findViewById<LinearLayout>(R.id.pomLayout1)

                if (view.isChecked) {
                    paymentCard.visibility = View.VISIBLE
                    pomLayout.visibility = View.GONE
                } else {
                    paymentCard.visibility = View.GONE
                    pomLayout.visibility = View.VISIBLE
                }
            }
        }

        deliveryButton.setOnClickListener {
            deliveryButton.setTextColor(resources.getColor(R.color.white))
            deliveryButton.background = getDrawable(R.drawable.input_field_green)
            deliveryButton.setCompoundDrawablesWithIntrinsicBounds(getDrawable(R.drawable.bike_white), null, null, null)
            licnoPreuzimanjeButton.setTextColor(resources.getColor(R.color.black))
            licnoPreuzimanjeButton.background = getDrawable(R.drawable.input_field_white)
            licnoPreuzimanjeButton.setCompoundDrawablesWithIntrinsicBounds(getDrawable(R.drawable.walk), null, null, null)
            deliveryLayout.visibility = View.VISIBLE
            lpLayout.visibility = View.GONE
        }

        licnoPreuzimanjeButton.setOnClickListener {
            licnoPreuzimanjeButton.setTextColor(resources.getColor(R.color.white))
            licnoPreuzimanjeButton.background = getDrawable(R.drawable.input_field_green)
            licnoPreuzimanjeButton.setCompoundDrawablesWithIntrinsicBounds(getDrawable(R.drawable.walk_white), null, null, null)
            deliveryButton.background = getDrawable(R.drawable.input_field_white)
            deliveryButton.setTextColor(resources.getColor(R.color.black))
            deliveryButton.setCompoundDrawablesWithIntrinsicBounds(getDrawable(R.drawable.bike), null, null, null)
            deliveryLayout.visibility = View.GONE
            lpLayout.visibility = View.VISIBLE
        }
        //------------------------------ FINISH ORDER --------------------------------------------------//
        var orderButton = findViewById<Button>(R.id.finishOrder)
        orderButton.setOnClickListener {
            showToastMessage("Uspešno naručeno!", false)
            finish()
        }

    }


    private fun showToastMessage(message: String, isError: Boolean) {
        val toast = Toast.makeText(this@UserOrderDetails, message, Toast.LENGTH_LONG)
        val view = toast.view
        view?.setBackgroundColor(if (isError) android.graphics.Color.RED else android.graphics.Color.GREEN)
        val text = view?.findViewById<TextView>(android.R.id.message)
        text?.setTextColor(android.graphics.Color.WHITE)
        toast.show()
    }
}
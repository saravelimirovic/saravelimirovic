package com.example.myapplication.ui

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import androidx.appcompat.app.AppCompatActivity
import com.example.myapplication.R

@Suppress("DEPRECATION")
class SplashScreenActivity : AppCompatActivity() {
    @SuppressLint("ResourceType")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)

        val r =
            Runnable { // if you are redirecting from a fragment then use getActivity() as the context.
                startActivity(Intent(this, LoginActivity::class.java))
                // To close the CurrentActitity, r.g. SpalshActivity
                finish()
            }

        val h = Handler()
// The Runnable will be executed after the given delay time
// The Runnable will be executed after the given delay time
        h.postDelayed(r, 1500)
    }

}
package com.example.myapplication.ui

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.etebarian.meowbottomnavigation.MeowBottomNavigation
import com.example.myapplication.R

class POM2Activity : AppCompatActivity() {
    protected final val home = 1
    protected final val dash = 3
    protected final val notf = 5
    protected final val item4 = 2
    protected final val item5 = 4
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pom2)


        try {
            @SuppressLint("MissingInflatedId", "LocalSuppress")
            val bottomNav = findViewById<MeowBottomNavigation>(R.id.meowBottomNav)
            bottomNav.add(MeowBottomNavigation.Model(home,R.drawable.home_fill0_wght400_grad0_opsz24))
            bottomNav.add(MeowBottomNavigation.Model(item4,R.drawable.search_black_24dp__1_))
            bottomNav.add(MeowBottomNavigation.Model(dash,R.drawable.round_add_24))
            bottomNav.add(MeowBottomNavigation.Model(item5,R.drawable.notifications_fill0_wght400_grad0_opsz24))
            bottomNav.add(MeowBottomNavigation.Model(notf,R.drawable.account_circle_fill0_wght400_grad0_opsz24))

            bottomNav.setOnClickMenuListener {
                when(it.id) {

                    home -> {
                        val intent   = Intent(this, AddPayementMethod::class.java)
                        bottomNav.show(home)
                        startActivity(intent)
                    }

                    dash-> {
                        val intent   = Intent(this, ExploreActivity::class.java)
                        bottomNav.show(dash)
                        startActivity(intent)
                    }

                    item4-> {
                        val intent   = Intent(this, POM1Activity::class.java)
                        startActivity(intent)
                    }

                    notf-> {
                        val intent   = Intent(this, NotificationsActivity::class.java)
                        startActivity(intent)
                    }

                }
            }

            bottomNav.setOnShowListener {
                /*
                when(it.id) {

                    home -> {
                        startActivity(intent)
                    }

                    notf-> {
                        startActivity(intent)
                    }
                }

                 */
            }
            bottomNav.show(item5)
        } catch (e: Exception) {
            println("ne radi :(((((((((((((")
        }
    }
}
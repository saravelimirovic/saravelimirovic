package com.example.myapplication.ui

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Button
import android.widget.GridView
import android.widget.ImageButton
import android.widget.Toast
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.etebarian.meowbottomnavigation.MeowBottomNavigation
import com.example.myapplication.R
//import com.example.myapplication.adapters.ProductAdapter
import com.example.myapplication.models.ProductCard
import com.google.android.material.navigation.NavigationView

class ExploreActivity : AppCompatActivity() {
    protected final val home = 1
    protected final val search = 2
    protected final val add = 3
    protected final val notf = 4
    protected final val account = 5
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_explore)

        setVisibility()

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        val drawerLayout: DrawerLayout = findViewById(R.id.drawerLayout)
        val navView: NavigationView = findViewById<NavigationView>(R.id.drawer_view)

        val menu_button = findViewById<ImageButton>(R.id.menu)

        menu_button.setOnClickListener{
            if(!drawerLayout.isDrawerOpen(GravityCompat.START))
                drawerLayout.openDrawer(GravityCompat.START)
            else
                drawerLayout.closeDrawer(GravityCompat.END)
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

                R.id.drawer_account -> {
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
            bottomNav.add(MeowBottomNavigation.Model(home,R.drawable.home_fill0_wght400_grad0_opsz24))
            bottomNav.add(MeowBottomNavigation.Model(search,R.drawable.search_black_24dp__1_))
            bottomNav.add(MeowBottomNavigation.Model(add,R.drawable.round_add_24))
            bottomNav.add(MeowBottomNavigation.Model(notf,R.drawable.notifications_fill0_wght400_grad0_opsz24))
            bottomNav.add(MeowBottomNavigation.Model(account,R.drawable.account_circle_fill0_wght400_grad0_opsz24))

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
                    account-> {
                        val sharedPreferences = getSharedPreferences("MyPreferences", Context.MODE_PRIVATE)
                        val role = sharedPreferences.getString("role", "empty")
                        if((role.equals("Kupac") || role.equals("Dostavljač")))
                            startActivity(Intent(this, UserAccountActivity::class.java))
                        else {
                            val intent   = Intent(this, UserProfileActivity::class.java)
                            intent.putExtra("MyProfile", true)
                            startActivity(intent)
                        }
                    }
                    add-> {
                        val intent   = Intent(this, AddPayementMethod::class.java)
                        startActivity(intent)
                    }

                }
            }

            bottomNav.show(search)
        } catch (e: Exception) {
            println("ne radi :(((((((((((((")
        }


//        val products = generateDummyProducts()
//        val adapter = ProductAdapter(this, R.layout.grid_one_product, products)
//        val gridView: GridView = findViewById(R.id.explore_grid)
//        gridView.adapter = adapter
    }

//    private fun generateDummyProducts(): List<ProductCard> {
//        val products: MutableList<ProductCard> = ArrayList()
//        products.add(ProductCard("Svece pepco vanila", 36, 4.5F, true, "slika"))
//        products.add(ProductCard("Lidl kifla sa kremom", 149, 3.5F, false, "slika"))
//        products.add(ProductCard("Domaci dzem od kupina Katarina", 239, 4.5F, false, "slika"))
//        products.add(ProductCard("Domaci puter od kikirija", 199, 4F, false, "slika"))
//        products.add(ProductCard("Domaci dzem od kupina Katarina", 239, 4.5F, false, "slika"))
//        products.add(ProductCard("Domaci puter od kikirija", 199, 4F, false, "slika"))
//        products.add(ProductCard("Domaci dzem od kupina Katarina", 239, 4.5F, false, "slika"))
//        products.add(ProductCard("Domaci puter od kikirija", 199, 4F, false, "slika"))
//        products.add(ProductCard("Svece ", 56, 5F, true, "slika"))
//        return products
//    }

    private fun setVisibility() {
        val navView: NavigationView = findViewById(R.id.drawer_view)
        val navMenu: Menu = navView.menu

        val sharedPreferences = getSharedPreferences("MyPreferences", Context.MODE_PRIVATE)
        val role = sharedPreferences.getString("role", "empty")

        val drawerProfileItem: MenuItem? = navMenu.findItem(R.id.drawer_profile)

        if (drawerProfileItem != null) {
            drawerProfileItem.setVisible(!(role.equals("Kupac") || role.equals("Dostavljač")))
        } else {
            Toast.makeText(this, "Stavka ne postoji", Toast.LENGTH_LONG).show()
        }
    }
}
package com.example.myapplication.ui

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.example.myapplication.R
import com.example.myapplication.databinding.ActivityRegisterBinding
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.widget.AppCompatSpinner
import com.android.volley.AuthFailureError
import com.android.volley.DefaultRetryPolicy
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.VolleyError
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import org.json.JSONException
import org.json.JSONObject
import com.example.myapplication.ui.RegisterThird.RegisterActivityCallback
import com.example.myapplication.utils.StaticAddress

class RegisterActivity : AppCompatActivity(), RegisterThird.RegisterActivityCallback {

    private lateinit var fragmentManager: FragmentManager
    private lateinit var binding: ActivityRegisterBinding

    var FirstName: String = ""
    var LastName: String = ""
    var Email: String = ""
    var Password: String = ""
    var PhoneNumber: String = ""
    var City: String = ""
    var Street: String = ""
    var StreetNumber: String = ""
    var Role: Number = 1
    var PostalCode: String = ""
    var ROLE: String = ""
    var Code: String = ""

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        goToFragment(RegisterFirst(), null)

        val first_button = findViewById<Button>(R.id.registration_button_first_page)
        val second_button = findViewById<Button>(R.id.registration_button_second_page)

        val back_to_login_button = findViewById<Button>(R.id.registration_button_back_to_login)
        val back_to_first_page_button = findViewById<Button>(R.id.registration_button_back_to_first_page)
        val openLogin = findViewById<TextView>(R.id.register_to_login)
        val confirm_code = findViewById<Button>(R.id.confirm_code)

        // OTVARANJE DRUGOG KORAKA
        binding.registrationButtonFirstPage.setOnClickListener {
            if(isFirstStepValid()) {
                val requestQueue: RequestQueue = Volley.newRequestQueue(this)
                val url = StaticAddress.URL + "/email/check_user?email=$Email"

                val jsonObjectRequest = JsonObjectRequest(
                    Request.Method.GET, url, null,
                    { response ->
                            first_button.visibility = View.INVISIBLE
                            second_button.visibility = View.VISIBLE
                            back_to_first_page_button.visibility = View.VISIBLE
                            back_to_login_button.visibility = View.INVISIBLE
                            goToFragment(RegisterSecond(), "reg_first_tag")
                    },
                    { error ->
                        showToastMessage("Korisnik sa ovom email adresom već postoji.", false)
                    }
                )
                jsonObjectRequest.retryPolicy = DefaultRetryPolicy(0, 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT)
                requestQueue.add(jsonObjectRequest)
            }
        }

        //  OTVARANJE TRECEG KORAKA registration_button_second_page
        binding.registrationButtonSecondPage.setOnClickListener {
            if(isSecondStepValid()) {
                back_to_first_page_button.visibility = View.INVISIBLE
                second_button.visibility = View.INVISIBLE
                openLogin.visibility = View.INVISIBLE
                confirm_code.visibility = View.VISIBLE
                goToFragment(RegisterThird(), "reg_second_tag")

                performVerification()
            }
        }

        //  POTVRDA KODA
        binding.confirmCode.setOnClickListener {
            checkCode()
        }

        //  POVRATAK NA DRUGI KORAK
        binding.registrationButtonBackToFirstPage.setOnClickListener {
            first_button.visibility = View.VISIBLE
            second_button.visibility = View.INVISIBLE
            back_to_first_page_button.visibility = View.INVISIBLE
            back_to_login_button.visibility = View.VISIBLE
            fragmentManager.popBackStack()
        }

        // POVRATAK NA LOGIN
        binding.registrationButtonBackToLogin.setOnClickListener {
            first_button.visibility = View.VISIBLE
            second_button.visibility = View.INVISIBLE
            back_to_first_page_button.visibility = View.INVISIBLE
            back_to_login_button.visibility = View.VISIBLE
            finish()
        }

        val openLoginPage = findViewById<TextView>(R.id.register_to_login)
        openLoginPage.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }
    }

    private fun goToFragment(fragment: Fragment, backStack: String?) {
        fragmentManager = supportFragmentManager
        fragmentManager.beginTransaction().replace(R.id.registrationFragmentField, fragment).addToBackStack(backStack).commit()

    }

    private fun isFirstStepValid(): Boolean {
        val nameEditText = findViewById<EditText>(R.id.registration_name)
        val lastNameEditText = findViewById<EditText>(R.id.registration_lastname)
        val emailEditText = findViewById<EditText>(R.id.registration_email)
        val passwordEditText = findViewById<EditText>(R.id.registration_password)

        FirstName = nameEditText.text.toString().trim()
        LastName = lastNameEditText.text.toString().trim()
        Email = emailEditText.text.toString().trim()
        Password = passwordEditText.text.toString().trim()

        if (FirstName.isEmpty()) {
            nameEditText.error = "Unesite ime."
            return false
        }

        if (LastName.isEmpty()) {
            lastNameEditText.error = "Unesite prezime."
            return false
        }

        if (Email.isEmpty()) {
            emailEditText.error = "Unesite email."
            return false
        }

        if (!isValidEmail(Email)) {
            emailEditText.error = "Unesite validan email."
            return false
        }

        if(Password.isEmpty()) {
            passwordEditText.error = "Unesite šifru."
            return false
        }

        if(!isPasswordValid(Password)) {
            passwordEditText.error = "Šifra mora sadržati najmanje 8 karaktera, jedno veliko slovo, jedno malo slovo i jedan broj."
            return false
        }

        return true
    }

    private fun isSecondStepValid(): Boolean {
        val cityNameEditText = findViewById<EditText>(R.id.registration_city)
        val streetNameEditText = findViewById<EditText>(R.id.registration_street_name)
        val streetNumberEditText = findViewById<EditText>(R.id.registration_street_number)
        val roleFrameLayout = findViewById<AppCompatSpinner>(R.id.spinner_uloga)
        val phoneNumberEditText = findViewById<EditText>(R.id.registration_phone_number)
        val postalCodeEditText = findViewById<EditText>(R.id.registration_postal_code)

        val cityName = cityNameEditText.text.toString().trim()
        val streetName = streetNameEditText.text.toString().trim()
        val streetNumber = streetNumberEditText.text.toString().trim()
        val role = roleFrameLayout.selectedItem.toString()
        val phoneNumber = phoneNumberEditText.text.toString().trim()
        val postalCode = postalCodeEditText.text.toString().trim()

        // Provera da li je unesen bar jedan od podataka (ulica ili broj ili postanski kod)
        val isStreetOrNumberEntered = streetName.isNotEmpty() || streetNumber.isNotEmpty()
        val isStreetEntered = streetName.isNotEmpty()
        val isStreetNumberEntered = streetNumber.isNotEmpty()
        val isPostalCodeEntered = postalCode.isNotEmpty()

        // Dodatna provera za broj ulice - samo brojevi
        if (streetNumber.isNotEmpty() && !onlyNumbers(streetNumber)) {
            streetNumberEditText.error = "Dozvoljeni su samo brojevi za broj ulice."
            return false
        }

        // Dodatna provera za poštanski broj - samo brojevi
        if (postalCode.isNotEmpty() && !onlyNumbers(postalCode)) {
            postalCodeEditText.error = "Dozvoljeni su samo brojevi za poštanski broj."
            return false
        }

        // Provera da li je unesen grad
        if (cityName.isEmpty() && isPostalCodeEntered) {
            cityNameEditText.error = "Unesite grad."
            return false
        }

        if (cityName.isNotEmpty() && !isPostalCodeEntered) {
            postalCodeEditText.error = "Unesite poštanski broj."
            return false
        }

        // Provera da li je unesen grad
        if (cityName.isEmpty() && isStreetOrNumberEntered) {
            cityNameEditText.error = "Unesite grad."
            return false
        }

        if(streetName.isEmpty() && isStreetNumberEntered) {
            streetNameEditText.error = "Unesite ulicu."
            return false
        }

        if(streetNumber.isEmpty() && isStreetEntered) {
            streetNumberEditText.error = "Unesite broj u ulici."
            return false
        }

        if (phoneNumber.isNotEmpty() && !onlyNumbers(phoneNumber)) {
            phoneNumberEditText.error = "Dozvoljeni su samo brojevi."
            return false
        }

        City = cityName
        Street = streetName
        StreetNumber = streetNumber
        PhoneNumber = phoneNumber
        PostalCode = postalCode
        if(role == "Kupac") Role = 1
        if(role == "Dostavljac") Role = 3
        if(role == "Prodavac") Role = 2
        return true
    }

    private fun isValidEmail(email: String): Boolean {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    private fun isPasswordValid(password: String): Boolean {
        return (password.length >= 8) &&
                password.contains(Regex(".*[A-Z].*")) &&
                password.contains(Regex(".*[a-z].*")) &&
                password.contains(Regex(".*\\d.*"))
    }

    private fun isStreetNumberValid(streetNumber: String): Boolean {
        return streetNumber.contains(Regex("\\d+"))
    }

    private fun onlyNumbers(string: String): Boolean {
        return string.contains(Regex("^\\d+\$"))
    }

    private fun performVerification() {
        val jsonParams = JSONObject()
        jsonParams.put("email", Email)

        val requestQueue: RequestQueue = Volley.newRequestQueue(this)
        val url = StaticAddress.URL + "/email/send_code"

        val jsonObjectRequest = JsonObjectRequest(
            Request.Method.POST, url, jsonParams,
            { response ->
                //Toast.makeText(this@RegisterActivity, "Response " + response.toString(), Toast.LENGTH_LONG).show()
            },
            { error ->
                showToastMessage("Problem sa bazom, pokušajte kasnije.", false)
            })
        jsonObjectRequest.retryPolicy = DefaultRetryPolicy(0, 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT)

        requestQueue.add(jsonObjectRequest)
    }

    private fun checkCode() {
        val verfCard1EditText = findViewById<EditText>(R.id.verf_card1)
        val verfCard2EditText = findViewById<EditText>(R.id.verf_card2)
        val verfCard3EditText = findViewById<EditText>(R.id.verf_card3)
        val verfCard4EditText = findViewById<EditText>(R.id.verf_card4)

        val verfCard1 = verfCard1EditText.text.toString().trim()
        val verfCard2 = verfCard2EditText.text.toString().trim()
        val verfCard3 = verfCard3EditText.text.toString().trim()
        val verfCard4 = verfCard4EditText.text.toString().trim()

        Code = verfCard1 + verfCard2 + verfCard3 + verfCard4

        val jsonParams = JSONObject()
        jsonParams.put("email", Email)
        jsonParams.put("code", Code)

        val requestQueue: RequestQueue = Volley.newRequestQueue(this)
        val url = StaticAddress.URL + "/email/check_code"

        val jsonObjectRequest = JsonObjectRequest(
            Request.Method.POST, url, jsonParams,
            { response ->
                performRegister()
            },
            { error ->
                showToastMessage("Kod nije ispravan, pokušajte ponovo.", false)
            })
        jsonObjectRequest.retryPolicy = DefaultRetryPolicy(0, 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT)

        requestQueue.add(jsonObjectRequest)
    }

    private fun showToastMessage(message: String, success: Boolean) {
        val toast = Toast.makeText(this@RegisterActivity, message, Toast.LENGTH_LONG)
        val view = toast.view
        view?.setBackgroundColor(if (success) Color.GREEN else Color.RED)
        val text = view?.findViewById<TextView>(android.R.id.message)
        text?.setTextColor(Color.WHITE)
        toast.show()
    }

    private fun handleRegistrationResponse(response: JSONObject) {
        try {
            if (response.getInt("Message") == 1) {
                showToastMessage("Ovaj korisnik je uspešno registrovan.", success = true)
                goToLogin()
            } else {
                showToastMessage("Korisnik nije registrovan.", success = false)
            }
        } catch (e: JSONException) {
            showToastMessage("Korisnik nije registrovan.", success = false)
        }
    }

    private fun handleErrorResponse(error: VolleyError) {
        if (error is AuthFailureError && error.networkResponse?.statusCode == 401) {
            showToastMessage("Postoji korisnik sa ovom email adresom.", success = false)
        } else {
            Log.e("Error", "Error")
        }
    }

    private fun performRegister() {
        val jsonParams = JSONObject()
        jsonParams.put("firstName", FirstName)
        jsonParams.put("lastName", LastName)
        jsonParams.put("email", Email)
        jsonParams.put("password", Password)
        jsonParams.put("phoneNumber", PhoneNumber)
        jsonParams.put("role", Role)
        jsonParams.put("city", City)
        jsonParams.put("street", Street)
        jsonParams.put("streetNumber", StreetNumber)
        jsonParams.put("postalCode", PostalCode)

        val requestQueue: RequestQueue = Volley.newRequestQueue(this)
        val url = StaticAddress.URL + "/web/auth/register"

        val jsonObjectRequest = JsonObjectRequest(
            Request.Method.POST, url, jsonParams,
            { response ->
                handleRegistrationResponse(response)
            },
            { error ->
                handleErrorResponse(error)
            }
        )
        jsonObjectRequest.retryPolicy = DefaultRetryPolicy(0, 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT)

        requestQueue.add(jsonObjectRequest)
    }

    private fun goToLogin() {
        val intent = Intent(this, LoginActivity::class.java)
        intent.putExtra("firstTime", true)
        startActivity(intent)
    }

    override fun performVerificationFromFragment() {
        performVerification()
    }

}

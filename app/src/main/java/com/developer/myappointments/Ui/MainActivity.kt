package com.developer.myappointments.Ui

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.developer.myappointments.Io.ApiService
import com.developer.myappointments.Io.Response.LoginResponse
import com.developer.myappointments.Util.PreferenceHelper
import kotlinx.android.synthetic.main.activity_main.*

import com.developer.myappointments.Util.PreferenceHelper.get
import com.developer.myappointments.Util.PreferenceHelper.set
import com.developer.myappointments.R
import com.developer.myappointments.Util.toast
import com.google.android.material.snackbar.Snackbar
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainActivity : AppCompatActivity() {

    private val apiService : ApiService by lazy {
        ApiService.create()
    }

    private val snackBar by lazy {
        Snackbar.make(
            mainLayout,
            R.string.press_back_again, Snackbar.LENGTH_SHORT
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

//        val preferences = getSharedPreferences("general", Context.MODE_PRIVATE)
//        val session = preferences.getBoolean("session", false)


        val preferences =
            PreferenceHelper.defaultPrefs(this)
        if (preferences["token", ""].contains("."))
            goToMenuActivity()

        //Button to login
        btnLogin.setOnClickListener {
            //validate login

            performLogin()
        }

        //Button to register view
        tvGoToRegister.setOnClickListener {
            Toast.makeText(
                this,
                getString(R.string.please_fill_your_register_data),
                Toast.LENGTH_SHORT
            ).show()
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }
    }


    override fun onBackPressed() {
        if (snackBar.isShown)
            super.onBackPressed()
        else
            snackBar.show()
    }

    private fun goToMenuActivity() {
        Toast.makeText(this, getString(R.string.welcome_to_the_app), Toast.LENGTH_SHORT).show()
        val intent = Intent(this, MenuActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun performLogin(){

        val email = etEmail.text.toString()
        val password = etPassword.text.toString()

        if (email.trim().isEmpty() || password.trim().isEmpty()){
            toast(getString(R.string.error_empty_credentials))
            return
        }

        val call = apiService.postLogin(email, password)
        call.enqueue(object: Callback<LoginResponse> {
            override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                toast(t.localizedMessage)
            }

            override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
                if (response.code() == 401){
                    toast(getString(R.string.error_login_credentials))
                }

                if (response.isSuccessful){
                    val loginResponse = response.body()
                    if (loginResponse == null) {
                        toast(getString(R.string.error_login_response))
                        return
                    }

                    if (loginResponse.success) {
                        createSessionPreference(loginResponse.access_token)
                        toast(getString(R.string.welcome_name, loginResponse.user.name))
                        goToMenuActivity()
                    } else {
                        toast(getString(R.string.error_credentials_login))
                    }

                } else {
                    toast(getString(R.string.error_login_response))
                }
            }

        })
    }

    private fun createSessionPreference(access_token: String) {
//        val preferences = getSharedPreferences("general", Context.MODE_PRIVATE)
//        val editor = preferences.edit()
//        editor.putBoolean("session", true)
//        editor.apply()

        val preferences =
            PreferenceHelper.defaultPrefs(this)
        preferences["token"] = access_token
    }
}

package com.developer.myappointments

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val preferences = getSharedPreferences("general", Context.MODE_PRIVATE)
        val session = preferences.getBoolean("session", false)

        if (session)
            goToMenuActivity()

        //Button to login
        btnLogin.setOnClickListener {
            //validate login

            createSessionPreference()
            goToMenuActivity()
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

    private fun goToMenuActivity() {
        Toast.makeText(this, getString(R.string.welcome_to_the_app), Toast.LENGTH_SHORT).show()
        val intent = Intent(this, MenuActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun createSessionPreference(){
        val preferences = getSharedPreferences("general", Context.MODE_PRIVATE)
        val editor = preferences.edit()

        editor.putBoolean("session", true)
        editor.apply()
    }
}

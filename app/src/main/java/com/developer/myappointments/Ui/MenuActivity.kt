package com.developer.myappointments.Ui

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.developer.myappointments.Io.ApiService
import com.developer.myappointments.Util.PreferenceHelper
import com.developer.myappointments.Util.PreferenceHelper.set
import com.developer.myappointments.Util.PreferenceHelper.get
import com.developer.myappointments.R
import com.developer.myappointments.Util.toast
import kotlinx.android.synthetic.main.activity_menu.*
import retrofit2.Call
import retrofit2.Response

class MenuActivity : AppCompatActivity() {

    private val apiService : ApiService by lazy {
        ApiService.create()
    }

    private val preferences by lazy {
        PreferenceHelper.defaultPrefs(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_menu)


        btnCreateAppointment.setOnClickListener {
            val intent = Intent(this, CreateAppointmentActivity::class.java)
            startActivity(intent)
        }


        btnMyAppointments.setOnClickListener {
            val intent = Intent(this, AppointmentsActivity::class.java)
            startActivity(intent)
        }


        //Logout
        btnLogOut.setOnClickListener {
            performLogout()
        }

    }

    private fun performLogout(){
        val token = preferences["token", ""]
        val call = apiService.postLogout("Bearer $token" )
        call.enqueue(object: retrofit2.Callback<Void>{
            override fun onFailure(call: Call<Void>, t: Throwable) {
                toast(t.localizedMessage)
            }

            override fun onResponse(call: Call<Void>, response: Response<Void>) {
//                if (response.isSuccessful){
//                    clearSessionPreference()
//                }
                clearSessionPreference()
                val intent = Intent(this@MenuActivity, MainActivity::class.java)
                startActivity(intent)
                finish()
            }

        })
    }

    private fun clearSessionPreference(){
        /*
        val preferences = getSharedPreferences("general", Context.MODE_PRIVATE)
        val editor = preferences.edit()

        editor.putBoolean("session", false)
        editor.apply()

        */

        preferences["token"] = ""
    }
}

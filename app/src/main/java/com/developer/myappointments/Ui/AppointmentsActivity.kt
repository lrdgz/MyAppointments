package com.developer.myappointments.Ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.developer.myappointments.Model.Appointment
import com.developer.myappointments.R
import kotlinx.android.synthetic.main.activity_appointments.*

class AppointmentsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_appointments)

        val appointments = ArrayList<Appointment>()

        appointments.add(
            Appointment(1, "Medico Test", "29/12/2019", "3:00 PM")
        )

        appointments.add(
            Appointment(2, "Medico Test", "30/12/2019", "4:00 PM")
        )

        appointments.add(
            Appointment(3, "Medico Test", "31/12/2019", "2:00 PM")
        )

        appointments.add(
            Appointment(4, "Medico Test", "01/01/2020", "1:00 PM")
        )

        rvwAppointments.layoutManager = LinearLayoutManager(this) //GridLayoutManager
        rvwAppointments.adapter =
            AppointmentAdapter(appointments)

    }
}

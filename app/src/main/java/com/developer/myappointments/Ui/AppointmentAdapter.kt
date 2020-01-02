package com.developer.myappointments.Ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.developer.myappointments.Model.Appointment
import com.developer.myappointments.R
import kotlinx.android.synthetic.main.item_appointment.view.*

class AppointmentAdapter(private val appointments: ArrayList<Appointment>) :
    RecyclerView.Adapter<AppointmentAdapter.ViewHolder>() {

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        /*
            val tvAppointmentID = itemView.tvAppointmentID
            val tvDoctorName = itemView.tvDoctorName
            val tvScheduledDate = itemView.tvScheduledDate
            val tvScheduledTime = itemView.tvScheduledTime
        */

        fun bind(appointment: Appointment) = with(itemView) {
            tvAppointmentID.text = context.getString(R.string.item_appointment_id, appointment.id)
            tvDoctorName.text = appointment.doctorName
            tvScheduledDate.text = context.getString(R.string.item_appointment_date, appointment.scheduledDate)
            tvScheduledTime.text = context.getString(R.string.item_appointment_time, appointment.scheduledTime)
        }

    }

    //Inflates XML items
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.item_appointment,
                parent,
                false
            )
        )
    }

    //Binds data
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val appointment = appointments[position]
        holder.bind(appointment)
    }

    //Number of elements
    override fun getItemCount(): Int = appointments.size
}
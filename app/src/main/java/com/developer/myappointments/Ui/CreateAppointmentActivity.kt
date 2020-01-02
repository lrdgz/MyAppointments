package com.developer.myappointments.Ui

import android.app.DatePickerDialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.RadioButton
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.developer.myappointments.Io.ApiService
import com.developer.myappointments.Model.Doctor
import com.developer.myappointments.Model.Specialty
import com.developer.myappointments.R
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_create_appointment.*

import kotlinx.android.synthetic.main.card_view_step_one.*
import kotlinx.android.synthetic.main.card_view_step_two.*
import kotlinx.android.synthetic.main.card_view_step_three.*
import retrofit2.Call

import java.util.*
import retrofit2.Callback
import retrofit2.Response
import kotlin.collections.ArrayList

class CreateAppointmentActivity : AppCompatActivity() {

    private val apiService: ApiService by lazy {
        ApiService.create()
    }

    private val selectedCalendar = Calendar.getInstance()
    private var selectedTimeRadioBtn: RadioButton? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_appointment)

        btnNext.setOnClickListener {

            if (etDescription.text.toString().length < 3) {
                etDescription.error = getString(R.string.validate_appointment_description)
            } else {
                cvStep1.visibility = View.GONE
                cvStep2.visibility = View.VISIBLE
            }

        }

        btnNext2.setOnClickListener {

            when {
                etScheduledDate.text.toString().isEmpty() -> {
                    etScheduledDate.error = getString(R.string.validate_appointment_date)
                    Snackbar.make(
                        createAppointmentLinearLayout,
                        R.string.validate_appointment_date,
                        Snackbar.LENGTH_SHORT
                    ).show()
                }
                selectedTimeRadioBtn == null -> {
                    Snackbar.make(
                        createAppointmentLinearLayout,
                        R.string.validate_appointment_time,
                        Snackbar.LENGTH_SHORT
                    ).show()
                }
                else -> {
                    showAppointmentDataToConfirm()
                    //continue to step 3
                    cvStep2.visibility = View.GONE
                    cvStep3.visibility = View.VISIBLE
                }
            }
        }

        btnConfirmAppointment.setOnClickListener {
            Toast.makeText(this, "Cita registrada correctamente", Toast.LENGTH_LONG).show()
            finish()
        }


//        val specialtiesOptions = arrayOf("Specialty A", "Specialty B", "Specialty C", "Specialty D")
//
//        spinnerSpecialties.adapter =
//            ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, specialtiesOptions)

        loadSpecialties()
        listenSpecialtyChanges()

        val doctorOptions = arrayOf("Doctor A", "Doctor B", "Doctor C", "Doctor D")
        spinnerDoctors.adapter =
            ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, doctorOptions)

    }

    private fun showAppointmentDataToConfirm() {
        tvConfirmDescription.text = etDescription.text.toString()
        tvConfirmSpecialty.text = spinnerSpecialties.selectedItem.toString()

        val selectedRadioBtnID = radioGroupType.checkedRadioButtonId
        val selectedRadio = radioGroupType.findViewById<RadioButton>(selectedRadioBtnID)
        tvConfirmType.text = selectedRadio.text.toString()

        tvConfirmDoctorName.text = spinnerDoctors.selectedItem.toString()
        tvConfirmDate.text = etScheduledDate.text.toString()
        tvConfirmTime.text = selectedTimeRadioBtn?.text.toString()
    }

    fun onClickScheduledDate(v: View?) {

        val year = selectedCalendar.get(Calendar.YEAR)
        val month = selectedCalendar.get(Calendar.MONTH)
        val dayOfMonth = selectedCalendar.get(Calendar.DAY_OF_MONTH)

        val listener = DatePickerDialog.OnDateSetListener { datePicker, y, m, d ->
            Toast.makeText(this, "$y-$m-$d", Toast.LENGTH_LONG).show()
            selectedCalendar.set(y, m, d)
            etScheduledDate.setText(
                resources.getString(
                    R.string.date_format,
                    y,
                    m.twoDigits(),
                    d.twoDigits()
                )
            )
            etScheduledDate.error = null
            displayRadioButtons()
        }

        //new dialog
        val datePickerDialog = DatePickerDialog(this, listener, year, month, dayOfMonth)

        //set limits

        //min
        val datePicker = datePickerDialog.datePicker
        val calendar = Calendar.getInstance()
        calendar.add(Calendar.DAY_OF_MONTH, 1)

        datePicker.minDate = calendar.timeInMillis // +1 now

        //max
        calendar.add(Calendar.DAY_OF_MONTH, 29)
        datePicker.maxDate = calendar.timeInMillis // +30 now

        //show dialog
        datePickerDialog.show()
    }

    private fun displayRadioButtons() {

        //LIMPIAR PARA QUE NO SE DUPLIQUEN
//        radioGroup.clearCheck()
//        radioGroup.removeAllViews()

        selectedTimeRadioBtn = null;
        radioGroupLeft.removeAllViews()
        radioGroupRight.removeAllViews()


        val hours = arrayOf("3:00 PM", "3:30 PM", "4:00 PM", "4:30 PM", "5:00 PM")
        var goToLeft = true

        hours.forEach {

            val radioButton = RadioButton(this)
            radioButton.id = View.generateViewId()
            radioButton.text = it

            radioButton.setOnClickListener { view ->
                selectedTimeRadioBtn?.isChecked = false
                selectedTimeRadioBtn = view as RadioButton?
                selectedTimeRadioBtn?.isChecked = true
            }

            if (goToLeft)
                radioGroupLeft.addView(radioButton)
            else
                radioGroupRight.addView(radioButton)

            goToLeft = !goToLeft

        }
    }

    private fun Int.twoDigits() = if (this >= 10) this.toString() else "0$this"

    override fun onBackPressed() {

        when {
            cvStep3.visibility == View.VISIBLE -> {
                cvStep3.visibility = View.GONE
                cvStep2.visibility = View.VISIBLE

            }
            cvStep2.visibility == View.VISIBLE -> {
                cvStep2.visibility = View.GONE
                cvStep1.visibility = View.VISIBLE

            }
            cvStep1.visibility == View.VISIBLE -> {
                val builder = AlertDialog.Builder(this)
                builder.setTitle(getString(R.string.dialog_appointment_exit_message))
                builder.setMessage(getString(R.string.dialog_appointment_exit_message))
                builder.setPositiveButton(getString(R.string.dialog_appointment_exit_positive_btn)) { _, _ ->
                    finish()
                }

                builder.setNegativeButton(getString(R.string.dialog_appointment_exit_negative_btn)) { dialog, _ ->
                    dialog.dismiss()
                }

                val dialog = builder.create()
                dialog.show()
            }
        }

    }

    private fun loadSpecialties() {
        val call = apiService.getSpecialties()
        call.enqueue(object : Callback<ArrayList<Specialty>> {
            override fun onFailure(call: Call<ArrayList<Specialty>>, t: Throwable) {
                Toast.makeText(
                    this@CreateAppointmentActivity,
                    getString(R.string.error_loaded_specialties),
                    Toast.LENGTH_SHORT
                ).show()
                finish()
            }

            override fun onResponse(
                call: Call<ArrayList<Specialty>>,
                response: Response<ArrayList<Specialty>>
            ) {
                if (response.isSuccessful) { //200, 300
                    val specialties = response.body()

                    spinnerSpecialties.adapter =
                        ArrayAdapter<Specialty>(
                            this@CreateAppointmentActivity,
                            android.R.layout.simple_list_item_1,
                            specialties!!
                        )
                }
            }

        })
    }

    private fun listenSpecialtyChanges() {
        spinnerSpecialties.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

            override fun onItemSelected(
                adapter: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                val specialty = adapter?.getItemAtPosition(position) as Specialty
                //Toast.makeText(this@CreateAppointmentActivity, "Id: ${specialty.id}, Nombre: ${specialty.name}", Toast.LENGTH_SHORT).show()
                loadDoctors(specialty.id)
            }

        }
    }

    private fun loadDoctors(specialtyId: Int){
        val call = apiService.getDoctors(specialtyId)
        call.enqueue(object: Callback<ArrayList<Doctor>>{
            override fun onFailure(call: Call<ArrayList<Doctor>>, t: Throwable) {
                Toast.makeText(
                    this@CreateAppointmentActivity,
                    getString(R.string.error_loaded_doctors),
                    Toast.LENGTH_SHORT
                ).show()
                finish()
            }

            override fun onResponse(
                call: Call<ArrayList<Doctor>>,
                response: Response<ArrayList<Doctor>>
            ) {
                if (response.isSuccessful) { //200, 300
                    val doctors = response.body()

                    spinnerDoctors.adapter = ArrayAdapter<Doctor>(
                            this@CreateAppointmentActivity,
                            android.R.layout.simple_list_item_1,
                            doctors!!
                        )
                }
            }

        })
    }
}
package com.developer.myappointments.Model

data class Schedule(
    val morning: ArrayList<HourInterval>,
    val afternoon: ArrayList<HourInterval>
)
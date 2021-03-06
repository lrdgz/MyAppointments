package com.developer.myappointments.Io

import com.developer.myappointments.Io.Response.LoginResponse
import com.developer.myappointments.Model.Doctor
import com.developer.myappointments.Model.Schedule
import retrofit2.Call
import com.developer.myappointments.Model.Specialty
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*

interface ApiService {

    @GET("specialties")
    fun getSpecialties(): Call<ArrayList<Specialty>>

    @GET("specialties/{specialty}/doctors")
    fun getDoctors(@Path("specialty") specialtyId: Int): Call<ArrayList<Doctor>>

    @Headers(value = ["Accept: application/json", "Content-type:application/json"])
    @GET("schedule/hours")
    fun getHours(@Query("doctor_id") doctorId: Int, @Query("date") date: String): Call<Schedule>

    @Headers(value = ["Accept: application/json", "Content-type:application/json"])
    @POST("login")
    fun postLogin(@Query("email") email: String, @Query("password") password: String): Call<LoginResponse>

    @POST("logout")
    fun postLogout(@Header("Authorization") authHeader: String): Call<Void>

    companion object Factory {
        private const val BASE_URL = "http://10.0.2.2:8000/api/"

        fun create () : ApiService {

            val interceptor = HttpLoggingInterceptor()
            interceptor.level = HttpLoggingInterceptor.Level.BODY
            val client = OkHttpClient.Builder().addInterceptor(interceptor).build()

            val retrofit = Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build()

            return retrofit.create(ApiService::class.java)
        }
    }
}
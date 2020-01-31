package com.developer.myappointments.Io.Response

import com.developer.myappointments.Model.User

data class LoginResponse(
    val success: Boolean,
    val user: User,
    val access_token: String,
    val token_type: String,
    val expires_at: String
)
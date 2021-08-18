package com.learncodingrsa.lambdaecommerce.model

data class PasswordRequest(
    val username: String,
    val oldPassword: String,
    val newPassword: String
)

data class LoginRequest(
    val username: String,
    val password: String,
)
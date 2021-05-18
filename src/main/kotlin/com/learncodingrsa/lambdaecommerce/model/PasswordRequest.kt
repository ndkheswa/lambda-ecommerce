package com.learncodingrsa.lambdaecommerce.model

data class PasswordRequest(
    val username: String,
    val oldPassword: String,
    val newPassword: String
)
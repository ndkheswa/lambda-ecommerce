package com.learncodingrsa.lambdaecommerce.model

data class PasswordRequest(
    val userName: String,
    val oldPassword: String,
    val newPassword: String
)
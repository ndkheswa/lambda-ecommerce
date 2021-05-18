package com.learncodingrsa.lambdaecommerce.model

data class UserInfo(
    val userName: String,
    val emailAddress: String,
    val password: String
)

data class UserInfoResponse(
    val userName: String,
    val emailAddress: String,
)

data class LoginInfo(
    val userName: String,
    val emailAddress: String,
    var newPasswordRequired: Boolean
)
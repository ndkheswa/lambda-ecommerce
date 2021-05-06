package com.learncodingrsa.lambdaecommerce.model

abstract class UserInfo {
    abstract val userName: String
    abstract val emailAddress: String
}

data class LoginInfo(
    override val userName: String,
    override val emailAddress: String,
    val newPasswordRequired: Boolean
) : UserInfo()
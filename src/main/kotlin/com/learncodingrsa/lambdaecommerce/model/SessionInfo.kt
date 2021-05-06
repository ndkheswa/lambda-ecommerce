package com.learncodingrsa.lambdaecommerce.model

data class SessionInfo(
    val session: String,
    val accessToken: String,
    val challengeResult: String
)
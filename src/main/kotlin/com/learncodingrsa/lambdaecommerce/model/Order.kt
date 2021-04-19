package com.learncodingrsa.lambdaecommerce.model

data class OrderRequest(
    val PK: String,
    val SK: String,
    val address: String,
    val Date: String,
    val EntityType: String
)

data class OrderResponse(
    val partitionKey: String,
    val date: String
)


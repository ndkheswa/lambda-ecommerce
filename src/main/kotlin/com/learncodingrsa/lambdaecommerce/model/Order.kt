package com.learncodingrsa.lambdaecommerce.model

data class Order(
    val customerId: String,
    val address: String,
    val customerName: String,
    val orderId: String,
    val amount: Int

)
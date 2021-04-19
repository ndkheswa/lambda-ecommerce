package com.learncodingrsa.lambdaecommerce.repository

import com.learncodingrsa.lambdaecommerce.model.OrderRequest
import com.learncodingrsa.lambdaecommerce.model.OrderResponse
import reactor.core.publisher.Mono

interface IOrder {
    fun getOrder(PK: String, SK: String): Mono<OrderResponse>
    fun saveOrder(order: OrderRequest): Mono<OrderResponse>
}
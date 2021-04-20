package com.learncodingrsa.lambdaecommerce.repository

import com.learncodingrsa.lambdaecommerce.model.OrderRequest
import com.learncodingrsa.lambdaecommerce.model.OrderResponse
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

interface IOrder {
    fun getOrder(partitionKey: String, sortKey: String): Mono<OrderResponse>
    fun getOrders(partitionKey: String): Flux<OrderResponse>
    fun saveOrder(order: OrderRequest): Mono<OrderResponse>
}
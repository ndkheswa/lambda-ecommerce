package com.learncodingrsa.lambdaecommerce.controller

import com.learncodingrsa.lambdaecommerce.model.OrderRequest
import com.learncodingrsa.lambdaecommerce.model.OrderResponse
import com.learncodingrsa.lambdaecommerce.repository.OrderRepository
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@RestController
@CrossOrigin(origins = arrayOf("*"), allowedHeaders = ["*"])
class OrderController(private val orderRepository: OrderRepository) {

    private val logger = LoggerFactory.getLogger(javaClass)

    @RequestMapping(value = ["/orders"], method = [RequestMethod.POST])
    fun saveOrder(@RequestBody order: OrderRequest): Mono<ResponseEntity<OrderResponse>> {
        return orderRepository.saveOrder(order)
            .map { saved ->
                ResponseEntity.status(HttpStatus.CREATED).body(saved)
            }.onErrorResume { t ->
                logger.error(t.message, t)
                Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build())
            }
    }

    @RequestMapping(value = ["/order"], method = [RequestMethod.GET])
    fun getOrder(@RequestParam pk: String, @RequestParam sk: String) : Mono<ResponseEntity<OrderResponse>> {
        return orderRepository.getOrder(pk, sk)
            .map { item ->
                ResponseEntity.status(HttpStatus.ACCEPTED).body(item)
            }
    }

    @RequestMapping(value = ["/orders"], method = [RequestMethod.GET])
    fun getOrders(@RequestParam("partitionKey") partitionKey: String) : Flux<OrderResponse> {
        return orderRepository.getOrders(partitionKey)
    }
}
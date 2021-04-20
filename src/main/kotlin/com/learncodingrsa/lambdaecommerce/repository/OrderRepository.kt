package com.learncodingrsa.lambdaecommerce.repository

import com.learncodingrsa.lambdaecommerce.model.OrderRequest
import com.learncodingrsa.lambdaecommerce.model.OrderResponse
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Repository
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import software.amazon.awssdk.services.dynamodb.DynamoDbAsyncClient
import software.amazon.awssdk.services.dynamodb.model.AttributeValue
import software.amazon.awssdk.services.dynamodb.model.GetItemRequest
import software.amazon.awssdk.services.dynamodb.model.PutItemRequest
import software.amazon.awssdk.services.dynamodb.model.QueryRequest
import java.util.stream.Collectors

@Repository
class OrderRepository(private val client: DynamoDbAsyncClient,
                      @Value("\${application.dynamo.table}") private val orderTable: String) : IOrder {

    override fun saveOrder(order: OrderRequest): Mono<OrderResponse> {

        val partitionKey = "c#${order.PK}"
        val sortKey = "o#${order.SK}"

        val item = mapOf(
            "PK" to AttributeValue.builder().s(partitionKey).build(),
            "SK" to AttributeValue.builder().s(sortKey).build(),
            "address" to AttributeValue.builder().s(order.address).build(),
            "Date" to AttributeValue.builder().s(order.Date).build(),
            "EntityType" to AttributeValue.builder().s(order.EntityType).build()
        )

        val putItemRequest = PutItemRequest.builder()
            .item(item)
            .tableName(orderTable)
            .build()

        return Mono.fromCompletionStage(client.putItem(putItemRequest))
            .flatMap { getOrder(partitionKey, sortKey) }

    }

    override fun getOrder(partitionKey: String, sortKey: String): Mono<OrderResponse> {

        val key = mapOf(
            "PK" to AttributeValue.builder().s(partitionKey).build(),
            "SK" to AttributeValue.builder().s(sortKey).build()
        )

        val getItemRequest: GetItemRequest = GetItemRequest.builder()
            .key(key)
            .tableName(orderTable)
            .build()

        return Mono.fromCompletionStage(client.getItem(getItemRequest))
            .map { resp ->
                fromMap(resp.item())
            }
    }

    fun getOrders(partitionKey: String): Flux<OrderResponse> {

        val nameMap = mapOf("#order" to "PK")
        val valueMap = mapOf(":order" to AttributeValue.builder().s(partitionKey).build())

        val qSpec = QueryRequest
            .builder()
            .tableName(orderTable)
            .keyConditionExpression("#order=:order")
            .expressionAttributeNames(nameMap)
            .expressionAttributeValues(valueMap)
            .build()

        return  Mono.from(client.queryPaginator(qSpec)).flatMapIterable { resp ->
            resp.items().stream().map { item ->
                fromMap(item)
            }.collect(Collectors.toList())
        }
    }

    companion object {
        fun fromMap(map: Map<String, AttributeValue>) : OrderResponse {
            return OrderResponse(
                partitionKey = map["PK"]?.s() ?: "",
                date = map["Date"]?.s() ?: ""
            )
        }
    }
}
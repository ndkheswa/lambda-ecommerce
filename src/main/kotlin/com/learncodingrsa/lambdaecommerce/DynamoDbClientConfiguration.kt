package com.learncodingrsa.lambdaecommerce

import org.springframework.beans.factory.annotation.Value

class DynamoDbClientConfiguration(@Value("\${application.dynamo.region}") private val region: String,
                                  @Value("\${application.dynamo.endpoint}") private val endpoint: String) {

    fun dynamoDbAsyncClient(): DynamoDbAsyncClient? {
        return DynamoDbAsyncClient.builder()
            .region(Region.of(region))
            .endpointOverride(URI.create(endpoint))
            .credentialsProvider(DefaultCredentialsProvider.builder().build())
            .build()
    }
}
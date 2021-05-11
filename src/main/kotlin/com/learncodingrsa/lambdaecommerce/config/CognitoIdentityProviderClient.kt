package com.learncodingrsa.lambdaecommerce.config

import org.springframework.context.annotation.Bean
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider
import software.amazon.awssdk.regions.Region
import software.amazon.awssdk.services.cognitoidentity.model.CognitoIdentityProvider

class CognitoIdentityProviderClient {

    @Bean
    fun cognitoIdentityProviderClient(): CognitoIdentityProvider {
        return CognitoIdentityProvider
            .builder()
            .region(Region.EU_WEST_1)
            .credentialsProvider(
                DefaultCredentialsProvider.builder().build())
            .build()
    }
}
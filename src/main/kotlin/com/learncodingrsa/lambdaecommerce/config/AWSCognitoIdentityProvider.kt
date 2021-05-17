package com.learncodingrsa.lambdaecommerce.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider
import software.amazon.awssdk.regions.Region
import software.amazon.awssdk.services.cognitoidentityprovider.CognitoIdentityProviderClient

@Configuration
class AWSCognitoIdentityProvider {

    @Bean
    fun getAmazonCognitoIdentityClient() : CognitoIdentityProviderClient {
        return CognitoIdentityProviderClient
            .builder()
            .region(Region.EU_WEST_1)
            .credentialsProvider(DefaultCredentialsProvider.builder().build())
            .build()
    }

}
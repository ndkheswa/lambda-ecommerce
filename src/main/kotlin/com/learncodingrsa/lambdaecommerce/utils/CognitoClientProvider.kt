package com.learncodingrsa.lambdaecommerce.utils

import software.amazon.awssdk.services.cognitoidentity.model.CognitoIdentityProvider
import software.amazon.awssdk.auth.credentials.

class CognitoClientProvider {

    var identityProvider: CognitoIdentityProvider? = null

    fun getCredentials() : AwsCredentials {
        val credentials = AwsBasicCredentials.create()
    }
}
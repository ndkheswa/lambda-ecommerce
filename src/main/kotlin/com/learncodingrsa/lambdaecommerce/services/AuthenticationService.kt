package com.learncodingrsa.lambdaecommerce.services

import com.learncodingrsa.lambdaecommerce.model.UserInfo
import com.learncodingrsa.lambdaecommerce.model.UserInfoResponse
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import software.amazon.awssdk.services.cognitoidentity.model.CognitoIdentityProvider
import software.amazon.awssdk.services.cognitoidentityprovider.CognitoIdentityProviderClient
import software.amazon.awssdk.services.cognitoidentityprovider.model.*
import software.amazon.awssdk.services.iam.model.CreateUserRequest

@Service
class AuthenticationService(private val client: CognitoIdentityProviderClient,
                            @Value("\${cognito.pool-id}") private val poolId: String) : AuthenticationInterface {

    fun createNewUser(userInfo: UserInfo?) : String {
        try {

            val emailAddress: String? = userInfo?.let { it.emailAddress }

            if (emailAddress != null && emailAddress.isNotEmpty()) {

                val info: UserInfoResponse? = this.findUserByEmailAddress(emailAddress)

                if (info == null) {
                    val cognitoRequest = AdminCreateUserRequest
                        .builder()
                        .userPoolId(poolId)
                        .username(userInfo.let { it.userName })
                        .userAttributes(AttributeType.builder().name("email").value(emailAddress).build())

                    client.adminCreateUser(
                        AdminCreateUserRequest
                            .builder()
                            .userPoolId("eu-west-1_v7W8llPKp")
                            .username(userInfo.let { it.userName })
                            .temporaryPassword(userInfo.let { it.password} )
                            .userAttributes(AttributeType.builder().name("email").value(emailAddress).build())
                            .build()
                    )

                }
            }

        } catch (e: CognitoIdentityProviderException) {
            e.awsErrorDetails().errorMessage()
        }
        return poolId
    }

    fun findUserByEmailAddress(email: String?) : UserInfoResponse? {
        var info: UserInfoResponse? = null

        if (email != null && email.length >= 0) {
            val emailQuery: String = "email=\"$email\""
            try {

                val resp: ListUsersResponse = client.listUsers(
                    ListUsersRequest.builder()
                        .userPoolId("eu-west-1_v7W8llPKp")
                        .attributesToGet("email")
                        .filter(emailQuery)
                        .build()
                )

                val users = resp.users()

                if (users != null && users.isNotEmpty()) {

                    if (users.size == 1) {
                        val user = users[0]
                        val userName: String = user.username()
                        var emailAddress: String? = null
                        val attributes: List<AttributeType>? = user.attributes()

                        if (!attributes.isNullOrEmpty()) {
                            for (attr: AttributeType in attributes) {
                                if (attr.name().equals("email")) {
                                    emailAddress = attr.value()
                                }
                            }
                            if (!userName.isNullOrBlank() ) {
                                info = emailAddress?.let { UserInfoResponse(userName, it) }
                            }
                        }

                    }
                }

            } catch (e: CognitoIdentityProviderException) {
                println(e.awsErrorDetails().errorMessage())
            }
        }
        return info
    }

}
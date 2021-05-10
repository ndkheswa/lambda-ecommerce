package com.learncodingrsa.lambdaecommerce.services


import com.learncodingrsa.lambdaecommerce.model.UserInfo
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import software.amazon.awssdk.services.cognitoidentityprovider.CognitoIdentityProviderClient
import software.amazon.awssdk.services.cognitoidentityprovider.model.*

@Service
class AuthenticationService(private val client: CognitoIdentityProviderClient,
                            @Value("\${cognito.pool-id}") private val poolId: String) : AuthenticationInterface {


    fun createNewUser(userInfo: UserInfo?) : String {
         try {

             val emailAddress: String? = userInfo?.let { it.emailAddress }

             if (emailAddress != null && emailAddress.isNotEmpty()) {

                 val info: UserInfo? = this.findUserByEmailAddress(emailAddress)

                 if (info == null) {
                     val cognitoRequest = AdminCreateUserRequest
                         .builder()
                         .userPoolId(poolId)
                         .username(userInfo.let { it.userName })
                         .userAttributes(AttributeType.builder().name("email").value(emailAddress).build())

                     client.adminCreateUser { cognitoRequest }

                 }
             }

         } catch (e: CognitoIdentityProviderException) {
             e.awsErrorDetails().errorMessage()
         }
        return poolId
    }

    fun findUserByEmailAddress(email: String?) : UserInfo? {
        var info: UserInfo? = null

        if (email != null && email.length >= 0) {
            val emailQuery: String = "email=\"$email\""
            try {
                val usersRequest = ListUsersRequest.builder()
                    .userPoolId("eu-west-1_v7W8llPKp")
                    .attributesToGet("email")
                    .filter(emailQuery)
                    .build()

                val usersResult: ListUsersResponse = this.client.listUsers { usersRequest }

                val users: List<UserType>? = usersResult.users()

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
                                info = emailAddress?.let { UserInfo(userName, it) }
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
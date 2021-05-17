package com.learncodingrsa.lambdaecommerce.services

import com.learncodingrsa.lambdaecommerce.model.*
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import software.amazon.awssdk.services.cognitoidentityprovider.CognitoIdentityProviderClient
import software.amazon.awssdk.services.cognitoidentityprovider.model.*

@Service
class AuthenticationService(private val client: CognitoIdentityProviderClient,
                            @Value("\${cognito.pool-id}") private val poolId: String,
                            @Value("\${cognito.client-id}") private val clientId: String) : AuthenticationInterface {

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
                            .userPoolId(poolId)
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

    /**
     * fun login(userName: String, password: String) : LoginInfo? {
    var info: LoginInfo? = null
    var sessionInfo: SessionInfo? = sessionHandler(userName, password)

    if (sessionInfo != null) {
    val userInfo: UserInfo? = getUserInfo()
    }
    }
     */

    fun changeTemporaryPassword(passwordRequest: PasswordRequest) : AdminRespondToAuthChallengeResponse? {
        var challengeResponse: AdminRespondToAuthChallengeResponse? = null

        try {
            var sessionInfo: SessionInfo? = sessionHandler(passwordRequest.userName, passwordRequest.oldPassword)
            val sessionString = sessionInfo?.let { it.session }

            if (!sessionString.isNullOrEmpty()) {
                val challengeResponses = hashMapOf<String, String>()
                challengeResponses[USERNAME] = passwordRequest.userName
                challengeResponses[PASSWORD] = passwordRequest.oldPassword
                challengeResponses[NEW_PASSWORD] = passwordRequest.newPassword

                challengeResponse = client.adminRespondToAuthChallenge(
                    AdminRespondToAuthChallengeRequest
                        .builder()
                        .challengeName(ChallengeNameType.NEW_PASSWORD_REQUIRED)
                        .challengeResponses(challengeResponses)
                        .clientId(clientId)
                        .userPoolId(poolId)
                        .session(sessionString)
                        .build()
                )
            }
        } catch (e: CognitoIdentityProviderException) {
            e.awsErrorDetails().errorMessage()
        }

        return challengeResponse
    }

    fun sessionHandler(userName: String, password: String) : SessionInfo? {
        var info: SessionInfo? = null
        try {

            val authParams = mutableMapOf<String, String>()
            authParams["USERNAME"] = userName
            authParams["PASSWORD"] = password

            val authResult = client.adminInitiateAuth(
                AdminInitiateAuthRequest.builder()
                    .authFlow(AuthFlowType.ADMIN_NO_SRP_AUTH)
                    .userPoolId(poolId)
                    .clientId(clientId)
                    .authParameters(authParams)
                    .build()
            )

            if (authResult != null) {
                val session = authResult.session()
                var accessToken: String? = null
                val resultType = authResult.authenticationResult()
                if (resultType != null) {
                    accessToken = resultType.accessToken()
                }
                val challengeResult = authResult.challengeName().name
                info = accessToken?.let { SessionInfo(session, it, challengeResult) }
            }
        } catch (e: CognitoIdentityProviderException) {
            e.awsErrorDetails().errorMessage()
        }

        return info
    }

    /**
     * fun getUserInfo(userName: String) {

    }
     */

    fun findUserByEmailAddress(email: String?) : UserInfoResponse? {
        var info: UserInfoResponse? = null

        if (email != null && email.length >= 0) {
            val emailQuery: String = "email=\"$email\""
            try {

                val resp: ListUsersResponse = client.listUsers(
                    ListUsersRequest.builder()
                        .userPoolId(poolId)
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

    companion object {
        private val USERNAME: String = "USERNAME"
        private val PASSWORD: String = "PASSWORD"
        private val NEW_PASSWORD: String = "NEW_PASSWORD"
    }

}
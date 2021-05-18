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

    override fun createNewUser(userInfo: UserInfo?) : String {
        try {

            val emailAddress: String? = userInfo?.let { it.emailAddress }

            if (emailAddress != null && emailAddress.isNotEmpty()) {

                val info: UserInfoResponse? = this.findUserByEmailAddress(emailAddress)

                if (info == null) {

                    client.adminCreateUser(
                        AdminCreateUserRequest
                            .builder()
                            .userPoolId(poolId)
                            .username(userInfo.let { it.username })
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

    override fun login(username: String, password: String) : LoginInfo? {
        var info: LoginInfo? = null
        var newPasswordRequired: Boolean = false

        try {
            var sessionInfo: SessionInfo? = sessionHandler(username, password)

            if (sessionInfo != null) {
                val userInfo: UserInfoResponse? = getUserInfo(username)

                info = LoginInfo(userInfo!!.username, userInfo.emailAddress, newPasswordRequired)

                val challengeResult: String = sessionInfo.challengeResult
                if (!challengeResult.isNullOrEmpty()) {
                    info.newPasswordRequired = challengeResult == ChallengeNameType.NEW_PASSWORD_REQUIRED.name
                }
            }
        } catch (e: CognitoIdentityProviderException) {
            e.awsErrorDetails().errorMessage()
        }

        return info
    }

    override fun changeTemporaryPassword(passwordRequest: PasswordRequest) : AdminRespondToAuthChallengeResponse? {
        var challengeResponse: AdminRespondToAuthChallengeResponse? = null

        try {
            var sessionInfo: SessionInfo? = sessionHandler(passwordRequest.username, passwordRequest.oldPassword)
            val sessionString = sessionInfo?.let { it.session }

            if (!sessionString.isNullOrEmpty()) {
                val challengeResponses = hashMapOf<String, String>()
                challengeResponses[USERNAME] = passwordRequest.username
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

    override fun sessionHandler(username: String, password: String) : SessionInfo? {
        var info: SessionInfo? = null
        try {

            val authParams = mutableMapOf<String, String>()
            authParams["USERNAME"] = username
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

    override fun getUserInfo(username: String) : UserInfoResponse? {
        var info: UserInfoResponse? = null
        try {

            val userResponse = client.adminGetUser(
                AdminGetUserRequest
                    .builder()
                    .userPoolId(poolId)
                    .username(username)
                    .build()
            )

            val userAttr: List<AttributeType> = userResponse.userAttributes()
            var emailAddr: String? = null
            for (attr: AttributeType in userAttr) {
                if (attr.name().equals(EMAIL)) {
                    emailAddr = attr.value()
                }
            }

            val responseUsername: String = userResponse.username()
            if (!responseUsername.isNullOrEmpty()) {
                info = emailAddr?.let { UserInfoResponse(responseUsername, it) }
            }
        } catch (e: CognitoIdentityProviderException) {
            e.awsErrorDetails().errorMessage()
        }

        return info
    }

    override fun findUserByEmailAddress(email: String?) : UserInfoResponse? {
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
                        val username: String = user.username()
                        var emailAddress: String? = null
                        val attributes: List<AttributeType>? = user.attributes()

                        if (!attributes.isNullOrEmpty()) {
                            for (attr: AttributeType in attributes) {
                                if (attr.name().equals("email")) {
                                    emailAddress = attr.value()
                                }
                            }
                            if (!username.isNullOrBlank() ) {
                                info = emailAddress?.let { UserInfoResponse(username, it) }
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
        private val EMAIL: String = "email"
        private val USERNAME: String = "USERNAME"
        private val PASSWORD: String = "PASSWORD"
        private val NEW_PASSWORD: String = "NEW_PASSWORD"
    }

}
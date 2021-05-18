package com.learncodingrsa.lambdaecommerce.services

import com.learncodingrsa.lambdaecommerce.model.*
import software.amazon.awssdk.services.cognitoidentityprovider.model.AdminRespondToAuthChallengeResponse

interface AuthenticationInterface {
    fun createNewUser(userInfo: UserInfo?) : String
    fun login(userName: String, password: String) : LoginInfo?
    fun changeTemporaryPassword(passwordRequest: PasswordRequest) : AdminRespondToAuthChallengeResponse?
    fun sessionHandler(userName: String, password: String) : SessionInfo?
    fun getUserInfo(userName: String) : UserInfoResponse?
    fun findUserByEmailAddress(email: String?) : UserInfoResponse?

}
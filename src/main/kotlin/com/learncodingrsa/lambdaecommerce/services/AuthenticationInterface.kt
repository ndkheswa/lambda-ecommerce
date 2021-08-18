package com.learncodingrsa.lambdaecommerce.services

import com.learncodingrsa.lambdaecommerce.model.*
import software.amazon.awssdk.services.cognitoidentityprovider.model.AdminRespondToAuthChallengeResponse

interface AuthenticationInterface {
    /**
     * fun createNewUser(userInfo: UserInfo?) : String
    fun login(username: String, password: String) : LoginInfo?
    fun changeTemporaryPassword(passwordRequest: PasswordRequest) : AdminRespondToAuthChallengeResponse?
    fun sessionHandler(username: String, password: String) : SessionInfo?
    fun getUserInfo(username: String) : UserInfoResponse?
    fun findUserByEmailAddress(email: String?) : UserInfoResponse?
     */

}
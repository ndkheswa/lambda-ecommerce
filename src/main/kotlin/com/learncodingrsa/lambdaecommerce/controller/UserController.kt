package com.learncodingrsa.lambdaecommerce.controller

import com.learncodingrsa.lambdaecommerce.model.*
import com.learncodingrsa.lambdaecommerce.services.AuthenticationService
import org.springframework.web.bind.annotation.*
import software.amazon.awssdk.services.cognitoidentityprovider.model.AdminRespondToAuthChallengeResponse

@RestController
@CrossOrigin(origins = ["*"], allowedHeaders = ["*"])
class UserController(private val userService: AuthenticationService) {

    @RequestMapping(value = ["/users"], method = [RequestMethod.POST])
    fun create(@RequestBody user: UserInfo) : String {
        return userService.createNewUser(userInfo = user)
    }

    @RequestMapping(value = ["/users"], method = [RequestMethod.GET])
    fun find(@RequestParam email: String) : UserInfoResponse? {
        return userService.findUserByEmailAddress(email)
    }

    @RequestMapping(value = ["/users/login"], method = [RequestMethod.POST])
    fun login(@RequestBody loginRequest: LoginRequest) : LoginInfo? {
        return userService.login(loginRequest)
    }

    @RequestMapping(value = ["/users/change-password"], method = [RequestMethod.POST])
    fun changeTemporaryPassword(@RequestBody passwordRequest: PasswordRequest) : Unit {
        userService.changeTemporaryPassword(passwordRequest)
    }

    @RequestMapping(value = ["/users/session"], method = [RequestMethod.GET])
    fun sessionInfo(@RequestParam username: String, @RequestParam password: String) : SessionInfo? {
        return userService.sessionHandler(username, password)
    }

    @RequestMapping(value = ["/users/info"], method = [RequestMethod.GET])
    fun getUserInfo(@RequestParam username: String) : UserInfoResponse? {
        return userService.getUserInfo(username)
    }
}
package com.learncodingrsa.lambdaecommerce.controller

import com.learncodingrsa.lambdaecommerce.model.LoginInfo
import com.learncodingrsa.lambdaecommerce.model.SessionInfo
import com.learncodingrsa.lambdaecommerce.model.UserInfo
import com.learncodingrsa.lambdaecommerce.model.UserInfoResponse
import com.learncodingrsa.lambdaecommerce.services.AuthenticationService
import org.springframework.web.bind.annotation.*

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
    fun login(username: String, password: String) : LoginInfo? {
        return userService.login(username, password)
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
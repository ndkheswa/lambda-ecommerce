package com.learncodingrsa.lambdaecommerce.controller

import com.learncodingrsa.lambdaecommerce.model.UserInfo
import com.learncodingrsa.lambdaecommerce.services.AuthenticationService
import org.springframework.web.bind.annotation.*

@RestController
@CrossOrigin(origins = ["*"], allowedHeaders = ["*"])
class CreateUserController(private val userService: AuthenticationService) {

    @RequestMapping(value = ["/users"], method = [RequestMethod.POST])
    fun create(@RequestBody user: UserInfo) : String {
        return userService.createNewUser(userInfo = user)
    }

    @RequestMapping(value = ["/users/{email}"], method = [RequestMethod.GET])
    fun find(@PathVariable email: String) {
        userService.findUserByEmailAddress(email)
    }
}
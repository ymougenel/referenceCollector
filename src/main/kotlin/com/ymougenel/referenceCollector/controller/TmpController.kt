package com.ymougenel.referenceCollector.controller

import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.*
import java.security.Principal

@Controller
@RequestMapping("/tmp")
class TmpController {

    @GetMapping("/public")
    fun getPublicPage(): String = "publicPage"


    @GetMapping("/user")
    fun getUserPage(principal: Principal, model: Model): String = "userPage"


    @GetMapping("/admin")
    fun getAdminPage(principal: Principal, model: Model): String = "adminPage"
}
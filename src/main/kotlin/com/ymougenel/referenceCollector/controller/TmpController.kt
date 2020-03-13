package com.ymougenel.referenceCollector.controller

import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.*

@Controller
@RequestMapping("/tmp")
class TmpController {

    @GetMapping("/public")
    fun getPublicPage(): String = "publicPage"


    @GetMapping("/user")
    fun getUserPage(): String = "userPage"


    @GetMapping("/admin")
    fun getAdminPage(): String = "adminPage"
}
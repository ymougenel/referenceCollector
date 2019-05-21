package com.ymougenel.referenceCollector.controller

import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.*

@Controller
@RequestMapping("/")
class HomeController {

    @GetMapping
    fun getHomePage(): String = "redirect:/references"
}
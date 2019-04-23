package com.ymougenel.referenceCollector.controller

import com.ymougenel.referenceCollector.model.Label
import com.ymougenel.referenceCollector.persistence.LabelDAO
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam

@Controller
@RequestMapping("/labels")
class LabelController {

    private lateinit var labelDAO: LabelDAO

    @Autowired
    constructor(labelDAO: LabelDAO) {
        this.labelDAO = labelDAO
    }

    @GetMapping
    fun getLabels(model: Model): String {
        model.addAttribute("labels", labelDAO.findAll())
        return "labelList"
    }

    @PostMapping
    fun getLabels(model: Model, @RequestParam("name") name: String): String {
        this.labelDAO.save(Label(0L,name,null))
        return "labelList"
    }
}
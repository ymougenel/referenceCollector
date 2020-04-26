package com.ymougenel.referenceCollector.controller

import com.ymougenel.referenceCollector.model.Label
import com.ymougenel.referenceCollector.service.LabelService
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.validation.Errors
import org.springframework.web.bind.annotation.*
import java.security.Principal
import javax.validation.Valid

@Controller
@RequestMapping("/labels")
class LabelController {

    private var labelService: LabelService
    var logger = LoggerFactory.getLogger(LabelController::class.java)

    @Autowired
    constructor(labelService: LabelService) {
        this.labelService = labelService
    }


    @GetMapping
    fun getLabels(model: Model,
                  @RequestParam("id", required = false, defaultValue = "0") id: Long): String {

        logger.info("Getting labels: id=" + id)

        model.addAttribute("labels", labelService.findAll().sortedBy { it.name })

        // Retrieve label or create new one
        val label: Label = if (id != 0L) labelService.findById(id).get() else Label()

        model.addAttribute("label", label)
        return "labels/list"
    }


    @PostMapping
    fun updateLabel(@Valid label: Label, errors: Errors, principal: Principal, model: Model): String {
        logger.info("label: (id=" + label.id + ", name=" + label.name + ") updated by user: " + principal.name)
        if (errors.hasErrors()) {
            logger.info("Error with labelUpdate" + errors.allErrors)
            model.addAttribute("labels", labelService.findAll().sortedBy { it.name })
            return "labels/list"
        }
        labelService.save(label)
        return "redirect:/labels"
    }

    @GetMapping("/delete")
    fun deleteLabel(principal: Principal, model: Model, @RequestParam("id") id: Long): String {
        logger.info("label: " + id + " deletion by user: " + principal.name)
        labelService.deleteById(id)
        return "redirect:/labels"
    }
}
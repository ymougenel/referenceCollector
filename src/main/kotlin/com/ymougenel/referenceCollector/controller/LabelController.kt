package com.ymougenel.referenceCollector.controller

import com.ymougenel.referenceCollector.model.Label
import com.ymougenel.referenceCollector.model.Reference
import com.ymougenel.referenceCollector.persistence.LabelDAO
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.validation.Errors
import org.springframework.web.bind.annotation.*
import javax.validation.Valid

@Controller
@RequestMapping("/labels")
class LabelController {

    private lateinit var labelDAO: LabelDAO

    @Autowired
    constructor(labelDAO: LabelDAO) {
        this.labelDAO = labelDAO
    }


    @GetMapping
    fun getLabels(model: Model,
                 @RequestParam("mode", required = false, defaultValue = "list") mode: String,
                 @RequestParam("id", required = false, defaultValue = "0") id: Long): String {

        model.addAttribute("labels", labelDAO.findAll().sortedBy { it.name })

        // Retrieve label or create new one
        val label: Label = if (id!=0L) labelDAO.findById(id).get() else Label()

        model.addAttribute("label",label)
        model.addAttribute("mode", mode)
        return "label_list"
    }

    @PostMapping
    fun updateLabel(@Valid label: Label, errors: Errors): String {
        //TODO form mapping
        if(errors.hasErrors()) {
            //TODO error
            return "redirect:/labels"
        }
        labelDAO.save(label)
        return "redirect:/labels"
    }

    @GetMapping("/delete")
    fun deleteLabel(model: Model, @RequestParam("id") id: Long): String {
        labelDAO.deleteById(id)
        return "redirect:/labels"
    }
}
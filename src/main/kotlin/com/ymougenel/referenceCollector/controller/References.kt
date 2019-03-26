package com.ymougenel.referenceCollector.controller

import com.ymougenel.referenceCollector.model.Reference
import com.ymougenel.referenceCollector.persistence.ReferenceDAO
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.validation.Errors
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import javax.validation.Valid

@Controller
@RequestMapping("/references")
class References {
    private lateinit var referencesDao: ReferenceDAO

    @Autowired
    constructor(ReferencesDAO: ReferenceDAO) {
        this.referencesDao = ReferencesDAO
    }

    @GetMapping(path = arrayOf("/new"))
    fun getForm(model: Model): String {
        model.addAttribute("reference", Reference())
        return "refForm"
    }

    @PostMapping
    fun postRef(@Valid reference: Reference, errors: Errors): String {
        if (errors.hasErrors()) {
            return "refForm"
        }

        referencesDao.save(reference);
        return "redirect:/references"
    }

    @GetMapping
    fun paginate(model: Model,
                 @RequestParam("page", required = false, defaultValue = "0") page: Int,
                 @RequestParam("size", required = false, defaultValue = "5") size: Int): String {
        val findAll = referencesDao.findAll(PageRequest.of(page, size))
        model.addAttribute("page", findAll)
        return "refList"
    }
}
package com.ymougenel.referenceCollector.controller

import com.ymougenel.referenceCollector.model.Reference
import com.ymougenel.referenceCollector.model.ReferenceType
import com.ymougenel.referenceCollector.persistence.LabelDAO
import com.ymougenel.referenceCollector.persistence.ReferenceDAO
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.validation.Errors
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import javax.validation.Valid
import java.util.stream.Collectors
import java.util.stream.IntStream



@Controller
@RequestMapping("/references")
class References {
    private lateinit var referencesDao: ReferenceDAO
    private lateinit var labelDAO: LabelDAO

    private var PAGINATION_RANGE = 3;
    @Autowired
    constructor(referencesDAO: ReferenceDAO, labelDAO: LabelDAO) {
        this.referencesDao = referencesDAO
        this.labelDAO = labelDAO
    }

    @GetMapping(path = arrayOf("/new"))
    fun getForm(model: Model): String {
        model.addAttribute("reference", Reference())
        model.addAttribute("labels", labelDAO.findAll())
        model.addAttribute("types", ReferenceType.values())
        return "refForm"
    }

    @GetMapping(path = arrayOf("/edit"))
    fun editForm(model: Model, @RequestParam("id") id: Long): String {
        model.addAttribute("reference", referencesDao.findById(id))
        model.addAttribute("labels", labelDAO.findAll())
        model.addAttribute("types", ReferenceType.values())
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
                 @RequestParam("size", required = false, defaultValue = "2") size: Int,
                 @RequestParam("direction", required = false, defaultValue = "ASC") direction: String,
                 @RequestParam("filterBy", required = false, defaultValue = "id") filterBy: String
                ): String {
        val references = referencesDao.findAll(PageRequest.of(page, size, Sort.Direction.fromString(direction), filterBy))

        model.addAttribute("page", references)
        model.addAttribute("direction", direction)

        if (references.totalPages > 0) {
            val pageIndex = references.number +1
            val minRange: Int
            val maxRange: Int
            if (pageIndex == 1) {
                minRange = 1;
                maxRange = Math.min(PAGINATION_RANGE, references.totalPages)
            }
            else if (pageIndex >= references.totalPages) {
                maxRange = references.totalPages
                minRange = Math.max(1,references.totalPages - PAGINATION_RANGE + 1)
            }
            else {
                minRange = pageIndex - PAGINATION_RANGE / 2
                maxRange = pageIndex + PAGINATION_RANGE / 2
            }
            val pageNumbers = IntStream.range(minRange, maxRange +1)
                    .boxed()
                    .collect(Collectors.toList())
            model.addAttribute("pageNumbers", pageNumbers)
        }
        return "refList"
    }
}
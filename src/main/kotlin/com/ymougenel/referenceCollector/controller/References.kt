package com.ymougenel.referenceCollector.controller

import com.ymougenel.referenceCollector.model.Reference
import com.ymougenel.referenceCollector.model.ReferenceType
import com.ymougenel.referenceCollector.persistence.LabelDAO
import com.ymougenel.referenceCollector.persistence.ReferenceDAO
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.dao.EmptyResultDataAccessException
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.validation.Errors
import org.springframework.web.bind.annotation.*
import javax.validation.Valid
import java.util.stream.Collectors
import java.util.stream.IntStream


@Controller
@RequestMapping("/references")
class References {
    private lateinit var referencesDao: ReferenceDAO
    private lateinit var labelDAO: LabelDAO

    private var PAGINATION_RANGE = 3

    @Autowired
    constructor(referencesDAO: ReferenceDAO, labelDAO: LabelDAO) {
        this.referencesDao = referencesDAO
        this.labelDAO = labelDAO
    }

    @GetMapping(path = arrayOf("/new"))
    fun getForm(model: Model): String {
        model.addAttribute("reference", Reference())
        model.addAttribute("labels", labelDAO.findAll().sortedBy{ it.name })
        model.addAttribute("types", ReferenceType.values())
        return "refForm"
    }

    @GetMapping(path = arrayOf("/edit"))
    fun editForm(model: Model, @RequestParam("id") id: Long): String {
        model.addAttribute("reference", referencesDao.findById(id))
        model.addAttribute("labels", labelDAO.findAll().sortedBy{ it.name })
        model.addAttribute("types", ReferenceType.values())
        return "refForm"
    }

    // TODO: change me to deleteMapping
    @GetMapping(path = arrayOf("/delete"))
    fun deleteRef(model: Model, @RequestParam("id") id: Long): String {
        referencesDao.deleteById(id)
        return "redirect:/references"
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
                 @RequestParam("size", required = false, defaultValue = "5") size: Int,
                 @RequestParam("direction", required = false, defaultValue = "ASC") direction: String,
                 @RequestParam("orderBy", required = false, defaultValue = "id") orderBy: String,
                 @RequestParam("filterBy", required = false, defaultValue = "") filterBy: String,
                 @RequestParam("filter", required = false, defaultValue = "") filter: String
    ): String {


        var references: Page<Reference>
        val pageRequest = PageRequest.of(page, size, Sort.Direction.fromString(direction), orderBy)
        //TODO refactor Kotlin style
        if (filterBy == "" || orderBy == "") {
            references = referencesDao.findAll(pageRequest)
        } else if (filterBy == "url") {
            references = referencesDao.findReferenceByUrlContaining(filter, pageRequest)
        } else if (filterBy == "name") {
            references = referencesDao.findReferenceByNameContaining(filter, pageRequest)
        } else {
            try {
                val label = labelDAO.findByName(filter)
                references = referencesDao.findReferenceBylabelsContaining(label, pageRequest)
            } catch (e: EmptyResultDataAccessException) {
                //TODO log
                //TODO warn not found
                references = referencesDao.findAll(pageRequest)
            }
        }
        handlePagination(model, references, direction)
        model.addAttribute("filterBy", filterBy)
        model.addAttribute("filter", filter)
        model.addAttribute("orderBy", orderBy)
        model.addAttribute("direction", direction)
        return "refList"
    }


    fun handlePagination(model: Model, page: Page<Reference>, direction: String) {
        model.addAttribute("page", page)
        model.addAttribute("direction", direction)

        if (page.totalPages > 0) {
            val pageIndex = page.number + 1
            val minRange: Int
            val maxRange: Int
            if (pageIndex == 1) {
                minRange = 1;
                maxRange = Math.min(PAGINATION_RANGE, page.totalPages)
            } else if (pageIndex >= page.totalPages) {
                maxRange = page.totalPages
                minRange = Math.max(1, page.totalPages - PAGINATION_RANGE + 1)
            } else {
                minRange = pageIndex - PAGINATION_RANGE / 2
                maxRange = pageIndex + PAGINATION_RANGE / 2
            }
            val pageNumbers = IntStream.range(minRange, maxRange + 1)
                    .boxed()
                    .collect(Collectors.toList())
            model.addAttribute("pageNumbers", pageNumbers)
        }
    }
}
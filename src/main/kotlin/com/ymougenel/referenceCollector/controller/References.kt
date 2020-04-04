package com.ymougenel.referenceCollector.controller

import com.ymougenel.referenceCollector.model.Reference
import com.ymougenel.referenceCollector.persistence.LabelDAO
import com.ymougenel.referenceCollector.service.ReferenceService
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.validation.Errors
import org.springframework.web.bind.annotation.*
import java.lang.IllegalArgumentException
import java.security.Principal
import javax.validation.Valid
import java.util.stream.Collectors
import java.util.stream.IntStream


@Controller
@RequestMapping("/references")
class References {

    var logger = LoggerFactory.getLogger(References::class.java)
    private var referenceService: ReferenceService
    private var labelDAO: LabelDAO

    private var PAGINATION_RANGE = 3

    @Autowired
    constructor(referencesDAO: ReferenceService, labelDAO: LabelDAO) {
        this.referenceService = referencesDAO
        this.labelDAO = labelDAO
    }

    @GetMapping(path = arrayOf("/new"))
    fun getForm(model: Model): String {
        model.addAttribute("reference", Reference())
        model.addAttribute("labels", labelDAO.findAll().sortedBy { it.name })
        return "references/form"
    }

    @GetMapping(path = arrayOf("/edit"))
    fun editForm(model: Model, @RequestParam("id") id: Long): String {
        model.addAttribute("reference", referenceService.findById(id))
        model.addAttribute("labels", labelDAO.findAll().sortedBy { it.name })
        return "references/form"
    }

    // TODO: change me to deleteMapping
    @GetMapping(path = arrayOf("/delete"))
    fun deleteRef(principal: Principal, model: Model, @RequestParam("id") id: Long): String {
        logger.info("ref: " + id + " deletion by user: " + principal.name)
        logger.info("DeleteReference id=" + id)
        referenceService.deleteById(id)
        return "redirect:/references"
    }

    @PostMapping
    fun postRef(@Valid reference: Reference, principal: Principal, errors: Errors, model: Model): String {
        logger.info("ref: (id=" + reference.id + ", name=" + reference.name + ") posted by user: " + principal.name)
        if (errors.hasErrors()) {
            logger.info("PostReference errors: " + reference)
            model.addAttribute("labels", labelDAO.findAll().sortedBy { it.name })
            return "references/form"
        }
        reference.owner = principal.name
        referenceService.save(reference)
        return "redirect:/references"
    }

    @GetMapping
    fun paginate(model: Model,
                 @RequestParam("page", required = false, defaultValue = "0") page: Int,
                 @RequestParam("size", required = false, defaultValue = "16") size: Int,
                 @RequestParam("direction", required = false, defaultValue = "ASC") direction: String,
                 @RequestParam("orderBy", required = false, defaultValue = "id") orderBy: String,
                 @RequestParam("filterBy", required = false, defaultValue = "") filterBy: String,
                 @RequestParam("filter", required = false, defaultValue = "") filter: String
    ): String {


        val pageRequest = PageRequest.of(page, size, Sort.Direction.fromString(direction), orderBy)
        var references: Page<Reference>
        try {
            val cleanedFilterBy = if (filter.isEmpty()) {
                ""
            } else {
                filterBy
            } // Clean filterBy value if filter is undefined
            references = referenceService.findFromFilter(pageRequest, cleanedFilterBy, filter, orderBy)

        } catch (e: IllegalArgumentException) {
            references = Page.empty(pageRequest)
        }

        handlePagination(model, references, direction)
        model.addAttribute("filterBy", filterBy)
        model.addAttribute("filter", filter)
        model.addAttribute("orderBy", orderBy)
        model.addAttribute("direction", direction)
        return "references/list"
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
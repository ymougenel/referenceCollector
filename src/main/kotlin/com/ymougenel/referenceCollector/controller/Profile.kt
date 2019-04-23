package com.ymougenel.referenceCollector.controller

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.ymougenel.referenceCollector.model.Reference
import com.ymougenel.referenceCollector.model.ReferenceType
import com.ymougenel.referenceCollector.persistence.LabelDAO
import com.ymougenel.referenceCollector.persistence.ReferenceDAO
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.core.io.FileSystemResource
import org.springframework.core.io.Resource
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile
import org.springframework.web.servlet.mvc.support.RedirectAttributes
import java.io.File
import javax.servlet.http.HttpServletResponse


@Controller
@RequestMapping("/profile")
class Profile {
    private lateinit var referencesDao: ReferenceDAO
    private lateinit var labelDAO: LabelDAO
    private var mapper = jacksonObjectMapper()

    @Autowired
    constructor(referencesDAO: ReferenceDAO, labelDAO: LabelDAO) {
        this.referencesDao = referencesDAO
        this.labelDAO = labelDAO
    }

    @GetMapping(path = arrayOf("importExport"))
    fun importExport() = "import_export";


    @GetMapping(path = arrayOf("/importReferences"))
    fun importReferences(model: Model): String {
        model.addAttribute("reference", Reference())
        model.addAttribute("types", ReferenceType.values())
        return "refForm"
    }


    @PostMapping(path = arrayOf("/importReferences"))
    fun handleFileU463e1adb4748pload(@RequestParam("file") file: MultipartFile,
                         redirectAttributes: RedirectAttributes): String {
        val references = mapper.readValue<List<Reference>>(file.inputStream)

        // Persist all label (only once)
        references
                .flatMap { it.labels!!.asIterable() }
                .toSet()
                .forEach { labelDAO.save(it) }


        // Update reference ids
        for (ref in references)
            for (lab in ref.labels!!)
                lab.id = labelDAO.findByName(lab.name).id
        referencesDao.saveAll(references)
        redirectAttributes.addFlashAttribute("message",
                "You successfully uploaded " + file.getOriginalFilename() + "!");

        return "redirect:/references";
    }

    @RequestMapping(value = arrayOf("/exportReferences"), produces = arrayOf("application/text"))
    @ResponseBody
    fun downloadFile(response: HttpServletResponse): Resource {
        val file = File("/tmp/export.json")
        val references = referencesDao.findAll()
        file.writeText(mapper.writeValueAsString(references))
        response.setContentType("application/text")
        response.setHeader("Content-Disposition", "inline; filename=" + file.getName());
        response.setHeader("Content-Length", file.length().toString())
        return FileSystemResource(file)
    }

}
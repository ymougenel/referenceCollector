package com.ymougenel.referenceCollector.controller

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.ymougenel.referenceCollector.model.Reference
import com.ymougenel.referenceCollector.persistence.LabelDAO
import com.ymougenel.referenceCollector.persistence.ReferenceDAO
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.core.io.FileSystemResource
import org.springframework.core.io.Resource
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile
import org.springframework.web.servlet.mvc.support.RedirectAttributes
import java.io.File
import javax.servlet.http.HttpServletResponse

/**
 * Import/Export references
 * TODO change all implementation: json -> csv
 */
@Controller
@RequestMapping("/profile")
class Profile {
    private var referencesDao: ReferenceDAO
    private var labelDAO: LabelDAO
    private var mapper = jacksonObjectMapper()
    var logger = LoggerFactory.getLogger(Profile::class.java)

    @Autowired
    constructor(referencesDAO: ReferenceDAO, labelDAO: LabelDAO) {
        this.referencesDao = referencesDAO
        this.labelDAO = labelDAO
    }

    @GetMapping(path = arrayOf("importExport"))
    fun importExport() = "import_export";

    // TODO refactor & optimize
    @PostMapping(path = arrayOf("/importReferences"))
    fun handleFileUpload(@RequestParam("file") file: MultipartFile,
                         redirectAttributes: RedirectAttributes): String {
        logger.info("Importing References")
        val references = mapper.readValue<List<Reference>>(file.inputStream)

        val currentLabelsNames = labelDAO.findAll()
                .map { label -> label.name }
                .toList()

        // Persist all label (only once)
        references
                .flatMap { it.labels!!.asIterable() }
                .toSet()
                .stream()
                .filter { !currentLabelsNames.contains(it.name) }
                .forEach { it.id = 0; labelDAO.save(it) }


        for (reference in references) {
            if (referencesDao.findReferenceByNameContaining(reference.name!!).isEmpty()) {

                for (lab in reference.labels!!) {
                    lab.id = labelDAO.findByName(lab.name).id
                }
                referencesDao.save(reference)

            }
        }
        redirectAttributes.addFlashAttribute("message",
                "You successfully uploaded " + file.getOriginalFilename() + "!");

        return "redirect:/references";
    }

    @RequestMapping(value = arrayOf("/exportReferences"), produces = arrayOf("application/text"))
    @ResponseBody
    fun downloadFile(response: HttpServletResponse): Resource {
        logger.info("Exporting references")
        val file = File("/tmp/export.json")
        val references = referencesDao.findAll()
        file.writeText(mapper.writeValueAsString(references))
        response.setContentType("application/text")
        response.setHeader("Content-Disposition", "inline; filename=" + file.getName());
        response.setHeader("Content-Length", file.length().toString())
        return FileSystemResource(file)
    }

}
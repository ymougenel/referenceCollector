package com.ymougenel.referenceCollector.controller

import com.ymougenel.referenceCollector.service.ImportExportService
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.core.io.FileSystemResource
import org.springframework.core.io.Resource
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile
import org.springframework.web.servlet.mvc.support.RedirectAttributes
import java.io.File
import java.security.InvalidParameterException
import javax.servlet.http.HttpServletResponse

/**
 * Import/Export references
 * TODO change all implementation: json -> csv
 */
@Controller
@RequestMapping("/profile")
class Profile {
    private var importExportService: ImportExportService
    var logger = LoggerFactory.getLogger(Profile::class.java)

    @Autowired
    constructor(importExportService: ImportExportService) {
        this.importExportService = importExportService
    }

    @GetMapping(path = arrayOf("importExport"))
    fun importExport() = "import_export";

    // TODO refactor & optimize
    @PostMapping(path = arrayOf("/importReferences"))
    fun handleFileUpload(@RequestParam("file") file: MultipartFile,
                         redirectAttributes: RedirectAttributes): String {
        logger.info("Importing References")
        if (file.isEmpty) {
            return "redirect:/profile/importExport"
        } else if (file.originalFilename!!.endsWith(".json")) {
            importExportService.importReferencesJSON(file.inputStream)

        } else {
            importExportService.importReferencesCSV(file.inputStream)
        }

        redirectAttributes.addFlashAttribute("message",
                "You successfully uploaded " + file.getOriginalFilename() + "!");

        return "redirect:/references";
    }

    @RequestMapping(value = arrayOf("/exportReferences"), produces = arrayOf("application/text"))
    @ResponseBody
    fun downloadFile(@RequestParam("type", defaultValue = "csv") type: String,
                     response: HttpServletResponse): Resource {
        logger.info("Exporting references")

        val file: File
        if (type == "json") {
            file = File("/tmp/references.json")
            file.writeText(importExportService.exportReferencesJSON())
        } else if (type == "csv") {
            file = File("/tmp/references.csv")
            file.writeText(importExportService.exportReferencesCSV())
        }
        // Protect against path injection
        else {
            throw InvalidParameterException("Illegal file format")
        }

        response.setContentType("application/text")
        response.setHeader("Content-Disposition", "inline; filename=" + file.getName());
        response.setHeader("Content-Length", file.length().toString())
        return FileSystemResource(file)
    }

}
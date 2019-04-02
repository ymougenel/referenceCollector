package com.ymougenel.referenceCollector.controller

import com.fasterxml.jackson.module.kotlin.*
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import com.ymougenel.referenceCollector.model.Reference
import com.ymougenel.referenceCollector.model.ReferenceType
import com.ymougenel.referenceCollector.persistence.ReferenceDAO
import jdk.internal.org.objectweb.asm.TypeReference
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
import org.springframework.core.io.FileSystemResource
import org.springframework.core.io.Resource
import org.springframework.web.bind.annotation.ResponseBody
import org.springframework.web.bind.annotation.RequestMethod
import java.io.File
import javax.servlet.http.HttpServletResponse
import org.springframework.http.HttpEntity
import org.apache.catalina.manager.StatusTransformer.setContentType
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.util.FileCopyUtils
import java.io.IOException
import org.springframework.http.MediaType.APPLICATION_PDF
import org.springframework.web.multipart.MultipartFile
import org.springframework.web.servlet.mvc.support.RedirectAttributes
import java.util.function.Consumer


@Controller
@RequestMapping("/profile")
class Profile {
    private lateinit var referencesDao: ReferenceDAO
    private var mapper = jacksonObjectMapper()

    @Autowired
    constructor(ReferencesDAO: ReferenceDAO) {
        this.referencesDao = ReferencesDAO
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
    fun handleFileUpload(@RequestParam("file") file: MultipartFile,
                         redirectAttributes: RedirectAttributes): String {
        var references = mapper.readValue<List<Reference>>(file.inputStream)
        references.forEach(Consumer { r -> r.id = 0L })
        referencesDao.saveAll(references)
        redirectAttributes.addFlashAttribute("message",
                "You successfully uploaded " + file.getOriginalFilename() + "!");

        return "redirect:/references";
    }

    @RequestMapping(value = arrayOf("/exportReferences"), produces = arrayOf("application/text"))
    @ResponseBody
    fun downloadFile(response: HttpServletResponse): Resource {
        val file = File("/tmp/export.json")
        val references = referencesDao.findAll();
        file.writeText(mapper.writeValueAsString(references))
        response.setContentType("application/text")
        response.setHeader("Content-Disposition", "inline; filename=" + file.getName());
        response.setHeader("Content-Length", file.length().toString())
        return FileSystemResource(file)
    }

}
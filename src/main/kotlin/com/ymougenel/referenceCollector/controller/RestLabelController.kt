package com.ymougenel.referenceCollector.controller

import com.ymougenel.referenceCollector.model.Label
import com.ymougenel.referenceCollector.service.LabelService
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/label")
class RestLabelController @Autowired
constructor(private val labelService: LabelService) {

    var logger = LoggerFactory.getLogger(RestLabelController::class.java)

    @PostMapping
    fun postLabel(@RequestBody label: Label): Label {
        logger.info("PostLabel: " + label)
        //TODO handle error
        return labelService.save(label)
    }
}

package com.ymougenel.referenceCollector.controller

import com.ymougenel.referenceCollector.model.Label
import com.ymougenel.referenceCollector.persistence.LabelDAO
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/label")
class RestLabelController @Autowired
constructor(private val labelDAO: LabelDAO) {

    var logger = LoggerFactory.getLogger(RestLabelController::class.java)

    @PostMapping
    fun postLabel(@RequestBody label: Label): Label {
        logger.info("PostLabel: " + label)
        //TODO handle error
        return labelDAO.save(label)
    }
}

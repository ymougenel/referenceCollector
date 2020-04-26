package com.ymougenel.referenceCollector.service

import com.ymougenel.referenceCollector.model.Label
import com.ymougenel.referenceCollector.persistence.LabelDAO
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import java.util.*

@Service
class LabelService {

    var logger = LoggerFactory.getLogger(LabelService::class.java)

    private val labelDao: LabelDAO

    @Autowired
    constructor(labelDao: LabelDAO) {
        this.labelDao = labelDao
    }

    fun findByName(name: String): Label = labelDao.findByNameContainingIgnoreCase(name)
    fun findAll(): List<Label> = labelDao.findAll().sortedBy { it.name }
    fun findById(id: Long): Optional<Label> = labelDao.findById(id)
    // TODO change
    fun save(label: Label) = labelDao.save(label)

    fun deleteById(id: Long) = labelDao.deleteById(id)


}
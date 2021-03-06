package com.ymougenel.referenceCollector.service

import com.ymougenel.referenceCollector.model.Reference
import com.ymougenel.referenceCollector.persistence.LabelDAO
import com.ymougenel.referenceCollector.persistence.ReferenceDAO
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.dao.EmptyResultDataAccessException
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import java.lang.IllegalArgumentException
import java.util.*

@Service
class ReferenceService {

    var logger = LoggerFactory.getLogger(ReferenceService::class.java)

    private val referencesDao: ReferenceDAO

    private val labelService: LabelService

    @Autowired
    constructor(referenceDao: ReferenceDAO, labelService: LabelService) {
        this.referencesDao = referenceDao
        this.labelService = labelService
    }

    fun findById(id: Long): Optional<Reference> = referencesDao.findById(id)
    fun deleteById(id: Long) = referencesDao.deleteById(id)
    fun save(reference: Reference): Reference = referencesDao.save(reference)
    fun findAll(pageRequest: PageRequest): Page<Reference> = referencesDao.findAll(pageRequest)


    fun findFromFilter(pageRequest: Pageable, filterBy: String, filter: String, orderBy: String): Page<Reference> {
        //TODO refactor Kotlin style
        var references: Page<Reference>
        if (filterBy == "" || orderBy == "") {
            references = referencesDao.findAll(pageRequest)
        } else if (filterBy == "url") {
            references = referencesDao.findReferenceByUrlContaining(filter, pageRequest)
        } else if (filterBy == "name") {
            references = referencesDao.findReferenceByNameContaining(filter, pageRequest)
        } else if (filterBy == "owner") {
            references = referencesDao.findReferenceByOwner(filter, pageRequest)
        } else {
            try {
                val labels = labelService.findByName(filter)
                //TODO: Page combinaition (instead of first label found)
                references = referencesDao.findReferenceBylabelsContaining(labels.get(0), pageRequest)
            } catch (e: EmptyResultDataAccessException) {
                logger.error("Error while finding label from filter: " + e.message)
                throw IllegalArgumentException("No label matching: " + filter)
            }
        }
        return references
    }
}
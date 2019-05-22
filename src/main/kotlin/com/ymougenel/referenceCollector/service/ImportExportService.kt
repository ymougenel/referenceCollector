package com.ymougenel.referenceCollector.service

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.ymougenel.referenceCollector.model.Reference
import com.ymougenel.referenceCollector.persistence.LabelDAO
import com.ymougenel.referenceCollector.persistence.ReferenceDAO
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import java.io.InputStream

@Service
class ImportExportService {

    private var referencesDao: ReferenceDAO
    private var labelDAO: LabelDAO
    private var mapper = jacksonObjectMapper()


    @Autowired
    constructor(referencesDAO: ReferenceDAO, labelDAO: LabelDAO) {
        this.referencesDao = referencesDAO
        this.labelDAO = labelDAO
    }

    fun importReferences(inputStream: InputStream) {

        val references = mapper.readValue<List<Reference>>(inputStream)

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
    }

    fun exportReferences() : String {
        val references = referencesDao.findAll()
        return mapper.writeValueAsString(references)
    }
}
package com.ymougenel.referenceCollector.service

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.ymougenel.referenceCollector.model.Label
import com.ymougenel.referenceCollector.model.Reference
import com.ymougenel.referenceCollector.persistence.LabelDAO
import com.ymougenel.referenceCollector.persistence.ReferenceDAO
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import java.io.InputStream
import java.util.stream.Collectors
import java.io.InputStreamReader
import java.io.BufferedReader


@Service
class ImportExportService {

    private var referencesDao: ReferenceDAO
    private var labelDAO: LabelDAO
    private var mapper = jacksonObjectMapper()
    private val CSV_DELIMITER = "^"
    private val NEW_LINE = "\n"

    @Autowired
    constructor(referencesDAO: ReferenceDAO, labelDAO: LabelDAO) {
        this.referencesDao = referencesDAO
        this.labelDAO = labelDAO
    }

    fun importReferencesCSV(inputStream: InputStream) {

        val references = ArrayList<Reference>()

        val reader = BufferedReader(InputStreamReader(inputStream))
        reader.readLine() // Skip Header
        for (line in reader.readLines()) {
            var content = line.split(CSV_DELIMITER)

            val labels = content[3]
                    .substring(1, content[3].length - 1) // Removes brackets
                    .split(",")
                    .stream()
                    .filter { it.isNotEmpty() }
                    .map { it -> Label(0, it) }
                    .collect(Collectors.toList())

            references.add(Reference(0L, content[1], content[0], labels, content[2]))
        }

        persistImportedReference(references)
    }

    fun importReferencesJSON(inputStream: InputStream) {

        val references = mapper.readValue<List<Reference>>(inputStream)
        persistImportedReference(references)
    }


    fun exportReferencesCSV(): String {
        val references = referencesDao.findAll()
        val sb = StringBuilder()

        // Add header
        sb.append("NAME").append(CSV_DELIMITER).append("URL").append(CSV_DELIMITER).append("OWNER").append(CSV_DELIMITER).append("LABELS").append(NEW_LINE)

        for (ref in references) {
            sb.append(ref.name).append(CSV_DELIMITER)
                    .append(ref.url).append(CSV_DELIMITER)
                    .append(ref.owner).append(CSV_DELIMITER)
                    .append("[")
            sb.append(ref.labels.stream().map { label -> label.name }.collect(Collectors.joining(",")))
            sb.append("]").append(NEW_LINE)

        }
        return sb.toString()
    }

    fun exportReferencesJSON(): String {
        val references = referencesDao.findAll()
        return mapper.writeValueAsString(references)
    }

    private fun persistImportedReference(references: List<Reference>) {
        val currentLabelsNames = labelDAO.findAll()
                .map { label -> label.name }
                .toList()

        // Persist all label (only once)
        references
                .flatMap { it.labels.asIterable() }
                .toSet()
                .stream()
                .filter { !currentLabelsNames.contains(it.name) }
                .forEach { it.id = 0; labelDAO.save(it) }


        for (reference in references) {
            if (referencesDao.findReferenceByNameContaining(reference.name!!).isEmpty()) {

                for (lab in reference.labels) {
                    lab.id = labelDAO.findByNameContainingIgnoreCase(lab.name).get(0).id
                }
                referencesDao.save(reference)

            }
        }
    }
}
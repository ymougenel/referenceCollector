package com.ymougenel.referenceCollector

import com.ymougenel.referenceCollector.model.Label
import com.ymougenel.referenceCollector.model.Reference
import com.ymougenel.referenceCollector.model.ReferenceType
import com.ymougenel.referenceCollector.persistence.LabelDAO
import com.ymougenel.referenceCollector.persistence.ReferenceDAO
import com.ymougenel.referenceCollector.service.ImportExportService
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.junit4.SpringRunner
import java.io.ByteArrayInputStream


@RunWith(SpringRunner::class)
@SpringBootTest
class ImportExportTest {

    @Autowired
    internal lateinit var importExportService: ImportExportService

    @Autowired
    internal lateinit var labelDAO: LabelDAO

    @Autowired
    internal lateinit var referenceDAO: ReferenceDAO

    val labels: List<Label> = listOf(Label(0, "lab1"), Label(0, "lab2"))
    val ref1 = Reference(0, "https://github.com/", "ref1", listOf(labels.get(0)), ReferenceType.ARTICLE)
    val ref2 = Reference(0, "https://en.wikipedia.org/wiki/Alan_Turing", "ref2", listOf(labels.get(0), labels.get(1)), ReferenceType.ARTICLE)
    val ref3 = Reference(0, "", "ref3", ArrayList(), ReferenceType.ARTICLE)

    @Before
    fun clean() {
        labelDAO.deleteAll()
        referenceDAO.deleteAll()

        labelDAO.saveAll(labels)
        referenceDAO.save(ref1)
        referenceDAO.save(ref2)
        referenceDAO.save(ref3)
    }

    /**
     * Once exported and imported, the database content should be the same
     * NOTE: currently unreferenced labels are lost
     */
    @Test
    fun testImportExport() {


        val exportOutput = importExportService.exportReferencesCSV()
        labelDAO.deleteAll()
        referenceDAO.deleteAll()

        // Save all the references/labels based from the previous exportation
        importExportService.importReferencesCSV(ByteArrayInputStream(exportOutput.toByteArray()))

        // Assert the labels have been imported
        assertNotNull(labelDAO.findByName("lab1"))
        assertNotNull(labelDAO.findByName("lab2"))

        // Assert the 3 references have been imported (and no more)
        assertEquals(3L, referenceDAO.count())
        val l1: List<Reference> = referenceDAO.findReferenceByNameContaining("ref1")
        val l2: List<Reference> = referenceDAO.findReferenceByNameContaining("ref2")
        val l3: List<Reference> = referenceDAO.findReferenceByNameContaining("ref3")

        assertEquals(1, l1.size)
        assertEquals(1, l2.size)
        assertEquals(1, l3.size)

        val r1 = l1.get(0)
        val r2 = l2.get(0)
        val r3 = l3.get(0)

        // Check first reference (name, url and labels)
        assertEquals(ref1.name, r1.name)
        assertEquals(ref1.url, r1.url)
        assert(r1.labels.stream().anyMatch { hasName(it, "lab1") })
        assertEquals(1, r1.labels.size)

        assertEquals(ref2.name, r2.name)
        assertEquals(ref2.url, r2.url)
        assert(r2.labels.stream().anyMatch { hasName(it, "lab1") })
        assert(r2.labels.stream().anyMatch { hasName(it, "lab2") })
        assertEquals(2, r2.labels.size)

        assertEquals(ref3.name, r3.name)
        assert(r3.labels.isEmpty())
        assertEquals(ref3.url, r3.url)

    }

    fun hasName(lab: Label, name: String): Boolean {
        return lab.name == name
    }

    /**
     * Import twice reference/labels should have no impact
     */
    @Test
    fun importIdempotent() {
        val labelCount = labelDAO.count()
        val referencesCount = referenceDAO.count()

        val exportOutput = importExportService.exportReferencesCSV()
        labelDAO.deleteAll()
        referenceDAO.deleteAll()

        // Save all the references/labels based from the previous exportation
        importExportService.importReferencesCSV(ByteArrayInputStream(exportOutput.toByteArray()))

        assertEquals(labelCount, labelDAO.count())
        assertEquals(referencesCount, referenceDAO.count())

        importExportService.importReferencesCSV(ByteArrayInputStream(exportOutput.toByteArray()))

        assertEquals(labelCount, labelDAO.count())
        assertEquals(referencesCount, referenceDAO.count())

    }
}

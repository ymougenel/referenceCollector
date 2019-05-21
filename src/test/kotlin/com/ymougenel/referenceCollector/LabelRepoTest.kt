package com.ymougenel.referenceCollector

import com.ymougenel.referenceCollector.model.Label
import com.ymougenel.referenceCollector.model.Reference
import com.ymougenel.referenceCollector.model.ReferenceType
import com.ymougenel.referenceCollector.persistence.LabelDAO
import com.ymougenel.referenceCollector.persistence.ReferenceDAO
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.junit4.SpringRunner
import org.springframework.transaction.TransactionSystemException
import java.util.*

@RunWith(SpringRunner::class)
@SpringBootTest
class LabelRepoTest {

    @Autowired
    internal lateinit var labelDAO: LabelDAO

    @Autowired
    internal lateinit var referenceDAO: ReferenceDAO

    lateinit var lab1: Label;
    lateinit var ref1: Reference

    @Before
    fun clean() {
        labelDAO.deleteAll()
        referenceDAO.deleteAll()

        // Save related reference/label
        lab1 = Label(0, "lab1", null)
        ref1 = Reference(0, "", "ref1", listOf(lab1), ReferenceType.ARTICLE)

        lab1 = labelDAO.save(lab1)
        ref1 = referenceDAO.save(ref1)
    }

    @Test
    fun basic_CRUD_test() {

        // Create/Retrieve
        var lab2 = Label(0, "lab2", null)
        lab2 = labelDAO.save(lab2)
        assertNotNull(labelDAO.findByName("lab2"))

        // Update
        lab2.name = "updated_lab2"
        labelDAO.save(lab2)
        assertEquals(lab2.name, labelDAO.findById(lab2.id).get().name)

        // Delete
        labelDAO.delete(lab2)
        assertFalse(labelDAO.findById(lab2.id).isPresent)

    }

    @Test(expected = TransactionSystemException::class)
    fun label_with_name_not_empty() {
        lab1.name = ""
        labelDAO.save(lab1)
    }

    /**
     * When deleting a references it should not delete any related labels
     */
    @Test
    fun reference_deletion_without_cascading() {

        // Delete reference
        referenceDAO.delete(ref1)

        // Assert reference still exists, and has no labels attached
        val labelOutput: Optional<Label> = labelDAO.findById(lab1.id)
        assert(labelOutput.isPresent)
        assertTrue(labelOutput.get().references!!.isEmpty())

    }

}

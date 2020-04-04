package com.ymougenel.referenceCollector.persistence

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
class ReferenceRepoTest {

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
        lab1 = Label(0, "lab1")
        ref1 = Reference(0, "", "ref1", listOf(lab1), "user1", ReferenceType.ARTICLE)

        lab1 = labelDAO.save(lab1)
        ref1 = referenceDAO.save(ref1)
    }

    @Test
    fun basic_CRUD_test() {

        // Create/Retrieve
        var ref2 = Reference(0, "", "ref2", listOf(lab1), "user1", ReferenceType.ARTICLE)
        ref2 = referenceDAO.save(ref2)
        assertNotNull(referenceDAO.findReferenceByNameContaining("ref2"))

        // Update
        ref2.name = "updated_ref2"
        referenceDAO.save(ref2)
        assertEquals(ref2.name, referenceDAO.findById(ref2.id).get().name)

        // Delete
        referenceDAO.delete(ref2)
        assertFalse(labelDAO.findById(ref2.id).isPresent)

    }

    @Test(expected = TransactionSystemException::class)
    fun reference_with_name_not_empty() {
        ref1.name = ""
        referenceDAO.save(ref1)
    }

    @Test(expected = TransactionSystemException::class)
    fun reference_with_valid_url() {
        ref1.url = "Invalid url"
        referenceDAO.save(ref1)
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

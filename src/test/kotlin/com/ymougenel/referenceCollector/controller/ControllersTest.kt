package com.ymougenel.referenceCollector.controller

import com.ymougenel.referenceCollector.persistence.LabelDAO
import com.ymougenel.referenceCollector.service.ImportExportService
import com.ymougenel.referenceCollector.service.LabelService
import com.ymougenel.referenceCollector.service.ReferenceService
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.test.context.junit4.SpringRunner
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.test.web.servlet.MockMvc
import org.springframework.beans.factory.annotation.Autowired

import org.hamcrest.Matchers.containsString
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.security.test.context.support.WithMockUser
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*

//TODO: create tests for anonymous user
@RunWith(SpringRunner::class)
@AutoConfigureMockMvc
@WebMvcTest
class ControllersTest {

    @Autowired
    private val mockMvc: MockMvc? = null

    @MockBean
    private val labelDAO: LabelDAO? = null

    @MockBean
    private val referenceService: ReferenceService? = null

    @MockBean
    private val labelService: LabelService? = null

    @MockBean
    private val importExportService: ImportExportService? = null


    @Test
    @WithMockUser(username="user",roles=["USER"])
    fun test_home_page() {
        mockMvc?.perform(get("/"))!!.andExpect(status().isFound)
    }

    //TODO Fix me: page null due to missing referenceService injection
//    @Test
//    fun test_reference_list() {
//        mockMvc?.perform(get("/references"))!!.andExpect(status().isOk)
//    }

    @Test
    @WithMockUser(username="user",roles=["USER"])
    fun test_reference_new() {
        mockMvc?.perform(get("/references/new"))!!.andExpect(status().isOk)
    }

    @Test
    @WithMockUser(username="user",roles=["USER"])
    fun test_label_list() {
        mockMvc?.perform(get("/labels"))!!.andExpect(status().isOk)
                .andExpect(content().string(containsString("Add Label")))
    }

    @Test
    @WithMockUser(username="user",roles=["USER"])
    fun test_import_export() {
        mockMvc?.perform(get("/profile/importExport"))!!.andExpect(status().isOk)
                .andExpect(content().string(containsString("Import references")))
                .andExpect(content().string(containsString("Export references")))
    }
}
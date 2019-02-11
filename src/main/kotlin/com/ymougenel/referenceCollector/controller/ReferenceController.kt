package com.ymougenel.referenceCollector.controller

import com.ymougenel.referenceCollector.model.Reference
import com.ymougenel.referenceCollector.persistance.ReferenceDAO
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.*

@CrossOrigin
@RestController
@RequestMapping(path = arrayOf("/references"))
class ReferenceController {

    private var ReferencesDao: ReferenceDAO

    @Autowired
    constructor(ReferencesDAO: ReferenceDAO) {
        this.ReferencesDao = ReferencesDAO
    }

    @PostMapping(path = arrayOf("/"), consumes = [MediaType.APPLICATION_JSON_VALUE] )
    @ResponseStatus(HttpStatus.CREATED)
    fun create(@RequestBody Reference: Reference) {
        ReferencesDao.save(Reference)
    }

    @GetMapping(path = arrayOf("/{id}"), produces = [MediaType.APPLICATION_JSON_UTF8_VALUE])
    fun get(@PathVariable id: Long): Reference {
        return ReferencesDao.findById(id)
                .orElseThrow { IllegalAccessException("References with id ${id} not found") }
    }

    @PutMapping(consumes = [MediaType.APPLICATION_JSON_VALUE], path = arrayOf("/"))
    fun update(@RequestBody Reference: Reference) {
        // TODO handle error
        ReferencesDao.save(Reference)
    }

    @DeleteMapping("/{id}")
    fun delete(id: Long) {
        // TODO handle error
        ReferencesDao.deleteById(id)
    }
}
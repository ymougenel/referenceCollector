package com.ymougenel.referenceCollector.persistence

import com.ymougenel.referenceCollector.model.Label
import com.ymougenel.referenceCollector.model.Reference
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository

interface ReferenceDAO : JpaRepository<Reference, Long> {
    fun findReferenceBylabelsContaining(label: Label, pageable: Pageable): Page<Reference>
    fun findReferenceByNameContaining(name: String, pageable: Pageable): Page<Reference>
    fun findReferenceByNameContaining(name: String): List<Reference>
    fun findReferenceByUrlContaining(url: String, pageable: Pageable): Page<Reference>
    fun findReferenceByOwner(url: String, pageable: Pageable): Page<Reference>
}
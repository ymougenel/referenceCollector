package com.ymougenel.referenceCollector.persistence

import com.ymougenel.referenceCollector.model.Label
import org.springframework.data.jpa.repository.JpaRepository

interface LabelDAO : JpaRepository<Label, Long> {
    fun findByNameContainingIgnoreCase(name: String) : List<Label>
}
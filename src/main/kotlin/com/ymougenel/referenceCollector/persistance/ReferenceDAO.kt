package com.ymougenel.referenceCollector.persistance

import com.ymougenel.referenceCollector.model.Reference
import org.springframework.data.jpa.repository.JpaRepository

interface ReferenceDAO : JpaRepository<Reference, Long> {
}
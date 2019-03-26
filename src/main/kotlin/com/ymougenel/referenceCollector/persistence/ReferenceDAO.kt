package com.ymougenel.referenceCollector.persistence

import com.ymougenel.referenceCollector.model.Reference
import org.springframework.data.jpa.repository.JpaRepository

interface ReferenceDAO : JpaRepository<Reference, Long> {
}
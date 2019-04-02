package com.ymougenel.referenceCollector.model

import org.hibernate.validator.constraints.URL
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id
import javax.validation.constraints.NotBlank

@Entity
data class Reference(
        @GeneratedValue(strategy = GenerationType.AUTO)
        @Id
        var id: Long?,
        @get:URL
        val url: String?,
        @get:NotBlank(message = "Name is required")
        val name: String?,
        var type: ReferenceType?) {
    constructor() : this(0L, "", "", null)
}
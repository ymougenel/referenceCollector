package com.ymougenel.referenceCollector.model

import org.hibernate.validator.constraints.URL
import javax.persistence.*
import javax.validation.constraints.NotBlank

@Entity
data class Reference(
        @GeneratedValue(strategy = GenerationType.AUTO)
        @Id
        var id: Long?,
        @get:URL
        var url: String?,
        @get:NotBlank(message = "Name is required")
        var name: String?,
        @ManyToMany
        var labels: List<Label>?,
        var type: ReferenceType?) {
    constructor() : this(0L, "", "", null, null)
}
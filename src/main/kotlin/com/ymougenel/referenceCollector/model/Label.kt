package com.ymougenel.referenceCollector.model

import com.fasterxml.jackson.annotation.JsonIgnore
import javax.persistence.*
import javax.validation.constraints.NotBlank

@Entity
data class Label(
        @Id
        @GeneratedValue(strategy = GenerationType.AUTO)
        var id: Long,
        @Column(unique = true)
        @get:NotBlank(message = "Name is required")
        var name: String,
        @JsonIgnore
        @ManyToMany
        @JoinTable(name = "label_reference",
                joinColumns = arrayOf(JoinColumn(name = "label_id")),
                inverseJoinColumns = arrayOf(JoinColumn(name = "reference_id"))
        )
        var references: List<Reference>?
) {
    constructor() : this(0L, "", null)
}
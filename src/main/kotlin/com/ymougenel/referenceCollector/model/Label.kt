package com.ymougenel.referenceCollector.model

import com.fasterxml.jackson.annotation.JsonIgnore
import javax.persistence.*

@Entity
data class Label(
        @Id
        @GeneratedValue(strategy = GenerationType.AUTO)
        var id: Long,
        @Column(unique=true)
        var name: String,
        @JsonIgnore
        @ManyToMany(mappedBy = "labels")
        var references: List<Reference>?
) {
    constructor() : this(0L, "", null)
}
package com.ymougenel.referenceCollector.model

import javax.persistence.*

@Entity
data class Label(
        @Id
        @GeneratedValue(strategy = GenerationType.AUTO)
        var id: Long,
        @Column(unique=true)
        var name: String,
        @ManyToMany
        var references: List<Reference>?
) {
    constructor() : this(0L, "", null)
}
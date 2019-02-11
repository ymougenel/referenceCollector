package com.ymougenel.referenceCollector.model

import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id

@Entity
data class Reference(@GeneratedValue(strategy = GenerationType.AUTO) @Id
val id: Long?,
val url: String?,
var category: String?)
{
    constructor() : this(0L,"","")
}
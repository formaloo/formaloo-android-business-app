package com.formaloo.model
import java.io.Serializable

data class Tag(
    var address: String? = null,
    var color: String? = null,
    var title: String? = null,
    var slug: String? = null

): Serializable

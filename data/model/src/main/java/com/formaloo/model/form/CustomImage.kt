package com.formaloo.model.form

import java.io.Serializable

data class CustomImage(
    var image: String? = null,
    var base64: String? = null,
    var slug: String? = null,
    var position: Int? = null

) : Serializable

package com.formaloo.model.form.submitForm

import java.io.Serializable

data class TopFields(
    var title: String? = null,
    var slug: String? = null
): Serializable

package com.formaloo.model.form.submitForm

import java.io.Serializable

data class SubmitFormData(
    var row: SubmitedRow? = null
): Serializable

package com.formaloo.remote.submit


/**
 * Implementation of [FormSubmitService] interface
 */

class SubmitDatasource(private val service: FormSubmitService) {
    fun submitForm(
        slug: String,
        req: HashMap<String, Int>,
    ) = service.submitForm(slug, req)

}

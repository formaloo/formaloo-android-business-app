package com.formaloo.remote.submit

import com.formaloo.model.form.submitForm.SubmitFormRes
import com.formaloo.common.Constants.VERSION3DOT2
import retrofit2.Call
import retrofit2.http.*

interface FormSubmitService {
    companion object {
        private const val submitForm = "${VERSION3DOT2}form-displays/slug/{slug}/submit/"
    }

    @POST(submitForm)
    fun submitForm(
        @Path("slug") slug: String,
        @Body req: HashMap<String, Int>,
    ): Call<SubmitFormRes>

}

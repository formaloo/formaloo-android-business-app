package com.formaloo.repository.submit

import com.formaloo.model.Result
import com.formaloo.model.form.submitForm.SubmitFormRes

interface SubmitRepo {

    suspend fun submitForm(
        slug: String,
        req: HashMap<String, Int>,
    ): Result<SubmitFormRes>

}

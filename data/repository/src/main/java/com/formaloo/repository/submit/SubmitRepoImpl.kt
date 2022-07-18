package com.formaloo.repository.submit

import com.formaloo.model.Result
import com.formaloo.model.form.submitForm.SubmitFormRes
import com.formaloo.remote.submit.SubmitDatasource
import com.formaloo.repository.BaseRepo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

const val TAG = "SubmitRepoImpl"

class SubmitRepoImpl(
    val source: SubmitDatasource,
) : BaseRepo(), SubmitRepo {

    override suspend fun submitForm(
        slug: String,
        req: HashMap<String, Int>,
    ): Result<SubmitFormRes> {
        return withContext(Dispatchers.IO) {
            val call = source.submitForm(slug, req)
            try {
                callRequest(call, { it.toSubmitFormRes() }, SubmitFormRes.empty())
            } catch (e: Exception) {
                Result.Error(IllegalStateException())
            }
        }
    }


}

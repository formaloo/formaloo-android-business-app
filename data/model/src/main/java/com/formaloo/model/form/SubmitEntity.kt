package com.formaloo.model.form

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "submit")
data class SubmitEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    var uniqueId: Int,
    var hasFormError: Boolean?,
    var newRow: Boolean?,
    var rowSlug: String?,
    var formSlug: String?,
    var formReq: HashMap<String, String>,
    var files: HashMap<String, Fields>
)

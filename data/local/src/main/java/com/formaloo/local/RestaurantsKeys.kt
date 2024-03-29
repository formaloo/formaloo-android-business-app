package com.formaloo.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "sponsorsKeys")
data class RestaurantsKeys(
    @PrimaryKey
    val id: String,
    val prevkey: Int?,
    val nextKey: Int?
)

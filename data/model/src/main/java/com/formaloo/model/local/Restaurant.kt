package com.formaloo.model.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity()
data class Restaurant(
    @PrimaryKey
    var restaurantSlug: String,
    var row: String?//tojson
) : Serializable

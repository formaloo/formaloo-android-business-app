package com.formaloo.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy.REPLACE
import androidx.room.Query
import com.formaloo.local.RestaurantsKeys

@Dao
interface RestaurantKeysDao {

    @Insert(onConflict = REPLACE)
    suspend fun saveSponsorsKeys(redditKey: RestaurantsKeys)

    @Insert(onConflict = REPLACE)
    suspend fun saveSponsorsKeys(keys: List<RestaurantsKeys>)

    @Query("SELECT * FROM sponsorsKeys ORDER BY id DESC")
    suspend fun getSponsorsKeys(): List<RestaurantsKeys>

    @Query("SELECT * FROM sponsorsKeys WHERE id = :id")
    suspend fun getSponsorKeys(id: String): RestaurantsKeys

    @Query("DELETE FROM sponsorsKeys")
    abstract suspend fun deleteAllFromTable()

}

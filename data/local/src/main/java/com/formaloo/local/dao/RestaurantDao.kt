package com.formaloo.local.dao

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Query
import com.formaloo.model.local.Restaurant

@Dao
abstract class RestaurantDao : AppUIBaseDao<Restaurant>() {

    @Query("SELECT * FROM Restaurant ")
    abstract fun getRestaurants(): PagingSource<Int, Restaurant>

    @Query("SELECT * FROM Restaurant ")
    abstract fun getRestaurantList(): List<Restaurant>

    @Query("SELECT * FROM Restaurant WHERE restaurantSlug = :slug")
    abstract suspend fun getRestaurant(slug: String): Restaurant

    @Query("DELETE FROM Restaurant WHERE restaurantSlug = :slug")
    abstract suspend fun deleteRestaurant(slug: String)

    // ---
    @Query("DELETE FROM Restaurant")
    abstract suspend fun deleteAllFromTable()

    suspend fun save(restaurants: Restaurant) {
        insert(restaurants)
    }

    suspend fun save(restaurants: List<Restaurant>) {
        insert(restaurants)
    }

}

package com.formaloo.repository.board

import android.util.ArrayMap
import android.util.Log
import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import com.formaloo.local.RestaurantsKeys
import com.formaloo.local.dao.RestaurantDao
import com.formaloo.local.dao.RestaurantKeysDao
import com.formaloo.model.Converter
import com.formaloo.model.local.Restaurant
import com.formaloo.remote.boards.BoardsDatasource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import java.io.IOException

@OptIn(ExperimentalPagingApi::class)
open class RestaurantListRemoteMediator(
    private val source: BoardsDatasource,
    private val restaurantDao: RestaurantDao,
    private val restaurantKeysDao: RestaurantKeysDao,
    private val boardSlug: String,
    private val blockSlug: String,
    private val force: Boolean,
    private val params: ArrayMap<String, Any>
) : RemoteMediator<Int, Restaurant>() {


    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, Restaurant>
    ): MediatorResult {
        Log.e(TAG, "load:force $force")
        return try {
            val loadKey = when (loadType) {
                LoadType.REFRESH -> {
                    val remoteKeys = getRemoteKeyClosestToCurrentPosition(state)
                    remoteKeys?.nextKey?.minus(-1) ?: 1
                }
                LoadType.PREPEND -> {
                    val remoteKeys = getRemoteKeyForFirstItem(state)
                    // If remoteKeys is null, that means the refresh result is not in the database yet.
                    // We can return Success with `endOfPaginationReached = false` because Paging
                    // will call this method again if RemoteKeys becomes non-null.
                    // If remoteKeys is NOT NULL but its prevKey is null, that means we've reached
                    // the end of pagination for prepend.
                    val prevOffset = remoteKeys?.prevkey ?: return MediatorResult.Success(
                        endOfPaginationReached = remoteKeys != null
                    )

                    prevOffset
                }
                LoadType.APPEND -> {
                    val remoteKeys = getRemoteKeyForLastItem(state)

                    val nextOffset = remoteKeys?.nextKey ?: return MediatorResult.Success(
                        endOfPaginationReached = remoteKeys != null
                    )

                    nextOffset

                }
            }
            val endOfPaginationReached = if (force) {

                params["page"] = loadKey
                val response = source.getSharedBlockContentFlow(boardSlug, blockSlug, params)
                val body = response.body()
                val data = body?.data
//                Log.e(TAG, "load:SponsorListRemoteMediator ${response.body()}")

                val restaurantList = arrayListOf<Restaurant>()
                data?.rows?.forEach {
                    restaurantList.add(Restaurant(it.slug, Converter().from(it)))
                }
                Log.e(TAG, "restaurantList:SponsorListRemoteMediator ${restaurantList.size}")

                val isEnd = data?.current_page == data?.page_count

                withContext(Dispatchers.IO) {
                    if (loadType == LoadType.REFRESH) {
                        Log.e(TAG, "loadType == LoadType.REFRESH: ")
                        restaurantDao.deleteAllFromTable()
                        restaurantKeysDao.deleteAllFromTable()

                    } else {

                    }


                    val prevOffset = if (loadKey == 1) null else loadKey - 1
                    val nextOffset = if (isEnd) null else loadKey + 1

                    val restaurantKeys = restaurantList.map {
                        RestaurantsKeys(it.restaurantSlug, prevOffset, nextOffset)
                    }

                    restaurantKeysDao.saveSponsorsKeys(restaurantKeys)
                    restaurantDao.save(restaurantList.toList())

                }

                isEnd

            } else {
                true
            }

            MediatorResult.Success(endOfPaginationReached = endOfPaginationReached)

        } catch (exception: IOException) {
            MediatorResult.Error(exception)
        } catch (exception: HttpException) {
            MediatorResult.Error(exception)
        }

    }

    private suspend fun getRemoteKeyForLastItem(state: PagingState<Int, Restaurant>): RestaurantsKeys? {
        // Get the last page that was retrieved, that contained items.
        // From that last page, get the last item
        return state.pages.lastOrNull() { it.data.isNotEmpty() }?.data?.lastOrNull()
            ?.let { restaurant ->
                // Get the remote keys of the last item retrieved
                restaurantKeysDao.getSponsorKeys(restaurant.restaurantSlug)
            }
    }

    private suspend fun getRemoteKeyForFirstItem(state: PagingState<Int, Restaurant>): RestaurantsKeys? {
        // Get the first page that was retrieved, that contained items.
        // From that first page, get the first item
        return state.pages.firstOrNull { it.data.isNotEmpty() }?.data?.firstOrNull()
            ?.let { restaurant ->
                // Get the remote keys of the first items retrieved
                restaurantKeysDao.getSponsorKeys(restaurant.restaurantSlug)
            }
    }

    private suspend fun getRemoteKeyClosestToCurrentPosition(
        state: PagingState<Int, Restaurant>
    ): RestaurantsKeys? {
        // The paging library is trying to load data after the anchor position
        // Get the item closest to the anchor position
        return state.anchorPosition?.let { position ->
            state.closestItemToPosition(position)?.restaurantSlug?.let {
                restaurantKeysDao.getSponsorKeys(it)
            }
        }
    }


}

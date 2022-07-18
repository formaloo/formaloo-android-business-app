package com.formaloo.repository.board

import android.util.ArrayMap
import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.formaloo.common.exception.Failure
import com.formaloo.common.functional.Either
import com.formaloo.local.dao.*
import com.formaloo.model.Result
import com.formaloo.model.boards.block.Block
import com.formaloo.model.boards.block.content.BlockContentRes
import com.formaloo.model.boards.block.detail.BlockDetailRes
import com.formaloo.model.boards.block.list.BlockListRes
import com.formaloo.model.boards.board.Board
import com.formaloo.model.boards.board.detail.BoardDetailRes
import com.formaloo.model.form.Form
import com.formaloo.model.local.*
import com.formaloo.model.form.submitForm.Row
import com.formaloo.remote.boards.BoardsDatasource
import com.formaloo.repository.BaseRepo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import retrofit2.Response

const val TAG = "BoardRepoImpl"

class BoardRepoImpl(
    private val source: BoardsDatasource,
    private val blockDao: BlockDao,
    private val boardDao: BoardDao,
    private val formDao: FormDao,
    private val speakerDao: SpeakerDao,
    private val timeLineDao: TimeLineDao,
    private val newsDao: NewsDao,
    private val speakerKeysDao: SpeakerKeysDao,
    private val newsKeysDao: NewsKeysDao,
    private val timeLineKeysDao: TimeLineKeysDao,
    private val restaurantDao: RestaurantDao,
    private val restaurantKeysDao: RestaurantKeysDao,
    private val faqDao: FAQDao,
    private val faqKeysDao: FAQKeysDao,
) : BoardRepo, BaseRepo() {

    override suspend fun clearRoom() {
        blockDao.deleteAllFromTable()
        speakerDao.deleteAllFromTable()
        restaurantDao.deleteAllFromTable()
        faqDao.deleteAllFromTable()
        faqKeysDao.deleteAllFromTable()
        newsDao.deleteAllFromTable()
        speakerKeysDao.deleteAllFromTable()
        newsKeysDao.deleteAllFromTable()
        restaurantKeysDao.deleteAllFromTable()
    }

    override fun getSpeakerList(): List<Speaker> {
        return speakerDao.getSpeakerList()
    }

    override fun getSpobsorList(): List<Restaurant> {
        return restaurantDao.getRestaurantList()
    }

    override suspend fun getNews(slug: String): News? {
        return newsDao.getNews(slug)

    }

    override fun getFAQList(): List<FAQ> {
        return faqDao.getFAQList()
    }

    override suspend fun getFAQ(slug: String): FAQ? {
        return faqDao.getFAQ(slug)

    }

    override suspend fun getSpeaker(slug: String): Speaker? {
        return speakerDao.getSpeaker(slug)

    }

    override suspend fun getRestaurant(slug: String): Restaurant? {
        return restaurantDao.getRestaurant(slug)

    }

    @OptIn(ExperimentalPagingApi::class)
    override fun fetchSpeakers(
        boardSlug: String,
        blockSlug: String,
        force: Boolean,
        params: ArrayMap<String, Any>
    ): Flow<PagingData<Speaker>> {
        return Pager(
            PagingConfig(pageSize = 40, enablePlaceholders = false, prefetchDistance = 3),
            remoteMediator = SpeakerListRemoteMediator(
                source,
                speakerDao,
                speakerKeysDao,
                boardSlug,
                blockSlug,
                force, params
            ),
            pagingSourceFactory = {
                speakerDao.getSpeakers()
            }
        ).flow
    }

    @OptIn(ExperimentalPagingApi::class)
    override fun fetchRestaurants(
        boardSlug: String,
        blockSlug: String,
        force: Boolean,
        params: ArrayMap<String, Any>
    ): Flow<PagingData<Restaurant>> {
        return Pager(
            PagingConfig(pageSize = 40, enablePlaceholders = false, prefetchDistance = 3),
            remoteMediator = RestaurantListRemoteMediator(
                source,
                restaurantDao,
                restaurantKeysDao,
                boardSlug,
                blockSlug,
                force, params
            ),
            pagingSourceFactory = {
                restaurantDao.getRestaurants()
            }
        ).flow
    }

    @OptIn(ExperimentalPagingApi::class)
    override fun fetchFAQs(
        boardSlug: String,
        blockSlug: String,
        force: Boolean,
        params: ArrayMap<String, Any>
    ): Flow<PagingData<FAQ>> {
        return Pager(
            PagingConfig(pageSize = 40, enablePlaceholders = false, prefetchDistance = 3),
            remoteMediator = FAQListRemoteMediator(
                source,
                faqDao,
                faqKeysDao,
                boardSlug,
                blockSlug,
                force, params
            ),
            pagingSourceFactory = {
                faqDao.getFAQ()
            }
        ).flow
    }

    @OptIn(ExperimentalPagingApi::class)
    override fun fetchTimeLines(
        boardSlug: String,
        blockSlug: String,
        force: Boolean, params: ArrayMap<String, Any>
    ): Flow<PagingData<TimeLine>> {
        return Pager(
            PagingConfig(pageSize = 40, enablePlaceholders = false, prefetchDistance = 3),
            remoteMediator = TimeLineListRemoteMediator(
                source,
                timeLineDao,
                timeLineKeysDao,
                boardSlug,
                blockSlug,
                force, params
            ),
            pagingSourceFactory = {
                timeLineDao.getTimeLines()
            }
        ).flow
    }


    @OptIn(ExperimentalPagingApi::class)
    override fun fetchGalley(
        boardSlug: String,
        blockSlug: String,
        force: Boolean,
        params: ArrayMap<String, Any>
    ): Flow<PagingData<Row>> {
        return Pager(
            config = PagingConfig(
                pageSize = 10,
                enablePlaceholders = false
            ),
            pagingSourceFactory = { GalleryPagingSource(source, boardSlug, blockSlug, params) }
        ).flow
    }


    @OptIn(ExperimentalPagingApi::class)
    override fun fetchNews(
        boardSlug: String,
        blockSlug: String,
        force: Boolean,
        params: ArrayMap<String, Any>
    ): Flow<PagingData<News>> {
        return Pager(
            PagingConfig(pageSize = 40, enablePlaceholders = false, prefetchDistance = 3),
            remoteMediator = NewsListRemoteMediator(
                source,
                newsDao,
                newsKeysDao,
                boardSlug,
                blockSlug,
                force, params
            ),
            pagingSourceFactory = {
                newsDao.getNews()
            }
        ).flow
    }


    override suspend fun getBlocksFromDB(): List<Block> {
        return blockDao.getBlockList()
    }

    override suspend fun getBlockFromDB(slug: String): Block? {
        return blockDao.getBlock(slug)

    }


    override suspend fun saveBoard(board: Board) {
        return boardDao.save(board)

    }

    override suspend fun getBoardData(address: String): Board? {
        return boardDao.getBoard(address)

    }


    override suspend fun saveForm(form: Form) {
        return formDao.save(form)

    }

    override suspend fun getFormData(slug: String): Form? {
        return formDao.getForm(slug)

    }


    override suspend fun saveBlockToDB(block: Block) {
        return blockDao.save(block)

    }


    override suspend fun saveBlocksToDB(blocks: List<Block>) {
        return blockDao.save(blocks)

    }


    override suspend fun getSharedBlockList(
        shared_board_slug: String,
        page: Int
    ): Either<Failure, BlockListRes> {
        val call = source.getSharedBlockList(shared_board_slug, page)
        return try {
            request(call, { it.toBlockListRes() }, BlockListRes.empty())
        } catch (e: Exception) {
            Either.Left(Failure.Exception)
        }
    }

    override suspend fun getSharedBlockDetail(
        shared_board_slug: String,
        block_slug: String
    ): Either<Failure, BlockDetailRes> {
        val call = source.getSharedBlockDetail(shared_board_slug, block_slug)
        return try {
            request(call, { it }, BlockDetailRes.empty())
        } catch (e: Exception) {
            Either.Left(Failure.Exception)
        }
    }

    override suspend fun getSharedBlockContent(
        shared_board_slug: String,
        block_slug: String,
        field_choice: Map<String, Any>?
    ): Either<Failure, BlockContentRes> {
        val call = source.getSharedBlockContent(shared_board_slug, block_slug, field_choice)
        return try {
            request(call, { it.toBlockContentRes() }, BlockContentRes.empty())
        } catch (e: Exception) {
            Either.Left(Failure.Exception)
        }
    }

    override suspend fun getSharedBlockContentFlow(
        shared_board_slug: String,
        block_slug: String,
        params: Map<String, Any>?
    ): Response<BlockContentRes> {
        return source.getSharedBlockContentFlow(
            shared_board_slug,
            block_slug,
            params
        )

    }

    override suspend fun getBoard(shared_board_slug: String): Result<BoardDetailRes> {
        return withContext(Dispatchers.IO) {
            val call = source.getSharedBoardDetail(shared_board_slug)
            try {
                callRequest(call, { it.toBoardDetailRes() }, BoardDetailRes.empty())
            } catch (e: Exception) {
                Result.Error(IllegalStateException())
            }
        }
    }

    override suspend fun getBlock(
        shared_board_slug: String,
        blockSlug: String
    ): Result<BlockDetailRes> {
        return withContext(Dispatchers.IO) {
            val call = source.getSharedBlockDetail(shared_board_slug, blockSlug)
            try {
                callRequest(call, { it.toBlockDetailRes() }, BlockDetailRes.empty())
            } catch (e: Exception) {
                Result.Error(IllegalStateException())
            }
        }
    }

    override suspend fun getBlockContent(
        shared_board_slug: String,
        blockSlug: String, params: Map<String, Any>?
    ): Result<BlockContentRes> {
        return withContext(Dispatchers.IO) {
            val call = source.getSharedBlockContent(shared_board_slug, blockSlug, params)
            try {
                callRequest(call, { it.toBlockContentRes() }, BlockContentRes.empty())
            } catch (e: Exception) {
                Result.Error(IllegalStateException())
            }
        }
    }

    override suspend fun getSharedBoardDetail(
        shared_board_slug: String
    ): Either<Failure, BoardDetailRes> {
        val call = source.getSharedBoardDetail(shared_board_slug)
        return try {
            request(call, { it.toBoardDetailRes() }, BoardDetailRes.empty())
        } catch (e: Exception) {
            Either.Left(Failure.Exception)
        }
    }
}




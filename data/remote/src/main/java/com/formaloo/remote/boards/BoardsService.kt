package com.formaloo.remote.boards

import com.formaloo.common.Constants.VERSION3Dot3
import com.formaloo.model.boards.block.content.BlockContentRes
import com.formaloo.model.boards.block.detail.BlockDetailRes
import com.formaloo.model.boards.block.list.BlockListRes
import com.formaloo.model.boards.board.detail.BoardDetailRes
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.*

interface BoardsService {

    companion object {

        private const val SHARED_BOARD = "${VERSION3Dot3}shared-boards/{board_share_address}"
        private const val SHARED_BOARDS_BLOCKS = "${VERSION3Dot3}shared-boards/{board_share_address}/blocks/"
        private const val SHARED_BOARDS_BLOCK = "${VERSION3Dot3}shared-boards/{board_share_address}/blocks/{block_slug}"
        private const val SHARED_BOARDS_BLOCK_CONTENT = "${VERSION3Dot3}shared-boards/{board_share_address}/blocks/{block_slug}/content/"

    }

    @GET(SHARED_BOARDS_BLOCKS)
    fun getSharedBlockList(
        @Path("board_share_address") board_share_address: String,
        @Query("page") page: Int,
        @Query("page_size") page_size: Int
    ): Call<BlockListRes>

    @GET(SHARED_BOARDS_BLOCK)
    fun getSharedBlockDetail(
        @Path("board_share_address") form_slug: String,
        @Path("block_slug") block_slug: String,
    ): Call<BlockDetailRes>


    @GET(SHARED_BOARDS_BLOCK_CONTENT)
    @JvmSuppressWildcards
    fun getSharedBlockContent(
        @Path("board_share_address") form_slug: String,
        @Path("block_slug") block_slug: String,
        @QueryMap field_Choice: Map<String, Any>?
        ): Call<BlockContentRes>


    @GET(SHARED_BOARDS_BLOCK_CONTENT)
    @JvmSuppressWildcards
    suspend   fun getSharedBlockContentFlow(
        @Path("board_share_address") form_slug: String,
        @Path("block_slug") block_slug: String,
        @QueryMap field_Choice: Map<String, Any>?
        ): Response<BlockContentRes>



    @GET(SHARED_BOARD)
    fun getSharedBoardDetail(
        @Path("board_share_address") board_slug: String
    ): Call<BoardDetailRes>


}

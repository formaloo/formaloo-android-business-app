package com.formaloo.remote.boards



/**
 * Implementation of [BoardsService] interface
 */

class BoardsDatasource(private val service: BoardsService) {
    fun getSharedBoardDetail(shared_board_slug: String) = service.getSharedBoardDetail(shared_board_slug)
    fun getSharedBlockList(shared_board_slug: String,page: Int) = service.getSharedBlockList(shared_board_slug,page,20)
    fun getSharedBlockDetail(shared_board_slug: String,block_slug: String) = service.getSharedBlockDetail(shared_board_slug,block_slug)
    fun getSharedBlockContent(shared_board_slug: String,block_slug: String,        field_Choice: Map<String, Any>?) = service.getSharedBlockContent(shared_board_slug,block_slug,field_Choice)
    suspend    fun getSharedBlockContentFlow(shared_board_slug: String,block_slug: String,        params: Map<String, Any>?) = service.getSharedBlockContentFlow(shared_board_slug,block_slug,params)

}

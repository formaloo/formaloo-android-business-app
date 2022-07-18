package com.formaloo.repository.di

import com.formaloo.remote.di.remoteAllFormModulConstant
import com.formaloo.remote.di.remoteBoardsModulConstant
import com.formaloo.remote.di.remoteSubmitModulConstant
import com.formaloo.repository.FormzRepo
import com.formaloo.repository.FormzRepoImpl
import com.formaloo.repository.board.BoardRepo
import com.formaloo.repository.board.BoardRepoImpl
import com.formaloo.repository.submit.SubmitRepo
import com.formaloo.repository.submit.SubmitRepoImpl
import org.koin.core.qualifier.named
import org.koin.dsl.module

val formRepositoryModule = module(override = true) {

    single<BoardRepo>(named(boardsRepoConstants.RepoName)) {
        BoardRepoImpl(
            get(named(remoteBoardsModulConstant.DataSourceName)),
            get(),
            get(),
            get(),
            get(),
            get(),
            get(),
            get(),
            get(),
            get(),
            get(),
            get(),
            get(),
            get()
        )
    }

    single<SubmitRepo>(named(submitRepoConstants.RepoName)) {
        SubmitRepoImpl(
            get(named(remoteSubmitModulConstant.DataSourceName))
        )
    }

    single<FormzRepo>(named(formAllRepoConstants.RepoName)) {
        FormzRepoImpl(
            get(named(remoteAllFormModulConstant.DataSourceName))
        )
    }

}


object boardsRepoConstants {
    val RepoName = "BoardsRepo"

}

object submitRepoConstants {
    val RepoName = "SubmitRepo"

}

object formAllRepoConstants {
    val RepoName = "FormzRepo"

}



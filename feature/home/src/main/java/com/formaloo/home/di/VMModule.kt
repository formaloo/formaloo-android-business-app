package com.formaloo.home.di

import com.formaloo.home.ui.BoardViewModel
import com.formaloo.home.vm.*
import com.formaloo.repository.di.boardsRepoConstants
import com.formaloo.repository.di.formAllRepoConstants
import com.formaloo.repository.di.submitRepoConstants
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.qualifier.named
import org.koin.dsl.module

val boardModule = module {
    viewModel { BoardViewModel(get(named(boardsRepoConstants.RepoName))) }
    viewModel { HomeGroupViewModel(get(named(boardsRepoConstants.RepoName))) }
    viewModel { RestaurantViewModel(get(named(boardsRepoConstants.RepoName))) }
    viewModel {
        AboutViewModel(
            get(named(boardsRepoConstants.RepoName)),
            get(named(formAllRepoConstants.RepoName))
        )
    }
    viewModel {
        MenuViewModel(
            get(named(boardsRepoConstants.RepoName)),
            get(named(formAllRepoConstants.RepoName)),
            get(named(submitRepoConstants.RepoName))
        )
    }
    viewModel {
        CareerViewModel(
            get(named(boardsRepoConstants.RepoName)),
            get(named(formAllRepoConstants.RepoName))
        )
    }
}

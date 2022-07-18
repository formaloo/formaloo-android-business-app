package com.formaloo.home

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.formaloo.common.Constants.SHAREDBOARDADDRESS
import com.formaloo.home.ui.BoardViewModel
import com.formaloo.home.ui.BusinessApp
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.component.KoinComponent

class HomeActivity : AppCompatActivity(), KoinComponent {
    private val boardVM: BoardViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)

        boardVM.refreshBoard(SHAREDBOARDADDRESS)

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                boardVM.uiState.collectLatest {
                    it.board?.let {
                        setContent {
                            BusinessApp(it)
                        }
                    }

                }
            }
        }


    }

}

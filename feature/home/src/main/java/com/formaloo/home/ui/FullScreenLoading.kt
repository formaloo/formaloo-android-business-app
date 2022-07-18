package com.formaloo.home.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.formaloo.home.ui.theme.MyTheme

/**
 * Full screen circular progress indicator
 */
@Composable
fun FullScreenLoading() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MyTheme.colors.uiBackground)
            .wrapContentSize(Alignment.Center)
    ) {
        CircularProgressIndicator()
    }
}
/**
 * Full screen circular progress indicator
 */
@Composable
fun ScreenLoading() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .clickable {  }
            .wrapContentSize(Alignment.Center)
    ) {
        CircularProgressIndicator()
    }
}

package com.formaloo.home.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import com.formaloo.home.R

val colorBack = Color(0xFFEDB601)
val color = Color(0x80EB9B14)
val colorMid = Color(0x80EB9B14)

@Composable
fun MainHeader(
    navigationIconClicked: () -> Unit
) {

    Box {
        Image(
            painter = painterResource(id = R.drawable.food),
            contentDescription = null,
            modifier = Modifier
                .fillMaxWidth(),
            contentScale = ContentScale.FillWidth,

            )

        NavigationTopAppbar("", navigationIconClicked, Color.Black)

    }
}


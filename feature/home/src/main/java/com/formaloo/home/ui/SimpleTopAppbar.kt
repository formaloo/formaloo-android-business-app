package com.formaloo.home.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.formaloo.home.ui.components.InsetAwareTopAppBar

@Composable
fun NavigationTopAppbar(
    title: String,
    navigationIconClicked: () -> Unit,
    navBtnColor: Color
) {

    InsetAwareTopAppBar(
        backgroundColor = Color.Transparent,
        title = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentWidth(align = Alignment.Start)
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.h6,
                    color = LocalContentColor.current,
                    modifier = Modifier
                        .wrapContentWidth(align = Alignment.Start)
                )
            }
        },
        navigationIcon = {
            Icon(
                imageVector = Icons.Default.Menu,
                contentDescription = "",
                modifier = Modifier
                    .padding(start = 16.dp)
                    .clickable(onClick = {
                        navigationIconClicked()
                    }), tint =navBtnColor
            )
        },
        elevation = 0.dp,
    )
}

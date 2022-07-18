package com.formaloo.home.ui.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.formaloo.common.Constants.SHAREDBOARDADDRESS
import com.formaloo.home.R
import com.formaloo.home.ui.*
import com.formaloo.home.ui.components.MyScaffold
import com.formaloo.home.ui.components.MySurface
import com.formaloo.home.ui.theme.MyTheme
import com.formaloo.home.vm.HomeGroupViewModel
import com.formaloo.home.vm.HomeViewState
import com.formaloo.model.boards.block.Block
import com.formaloo.model.boards.board.Board
import com.google.accompanist.insets.LocalWindowInsets
import com.google.accompanist.insets.rememberInsetsPaddingValues
import com.google.accompanist.insets.statusBarsPadding
import com.google.accompanist.insets.systemBarsPadding
import org.koin.androidx.compose.viewModel
import kotlin.math.ceil


@Composable
fun MainScreen(
    scaffoldState: ScaffoldState = rememberScaffoldState(),
    navigateToDeals: (String) -> Unit,
    navigateToJob: (String) -> Unit,
    navigateToRestaurants: (String) -> Unit,
    navigateToAbout: (String) -> Unit,
    navigationIconClicked: () -> Unit,
) {
    val boardVM by viewModel<BoardViewModel>()
    val boardState by boardVM.uiState.collectAsState()
    boardVM.refreshBoard(SHAREDBOARDADDRESS)

    val homeVM by viewModel<HomeGroupViewModel>()
    val viewState by homeVM.state.collectAsState()

    val scrollState = rememberLazyListState()

    MyScaffold(
        scaffoldState = scaffoldState,
        snackbarHost = { SnackbarHost(hostState = it, modifier = Modifier.systemBarsPadding()) },
        backgroundColor = colorBack,
    ) { innerPadding ->
        val modifier = Modifier.padding(innerPadding)
        LoadingContent(
            empty = viewState.initialLoad,
            emptyContent = { FullScreenLoading() },
            loading = viewState.refreshing,
            onRefresh = { boardVM.refreshBoard(SHAREDBOARDADDRESS) },
            content = {
                MainContent(
                    board = boardState.board,
                    viewState = viewState,
                    scrollState = scrollState,
                    viewModel = homeVM,
                    navigateToDeals,
                    navigateToJob,
                    navigateToRestaurants,
                    navigateToAbout,
                    navigationIconClicked = navigationIconClicked
                )
            }
        )
    }

}

@Composable
fun MainContent(
    board: Board?,
    viewState: HomeViewState,
    scrollState: LazyListState,
    viewModel: HomeGroupViewModel,
    navigateToDeals: (String) -> Unit,
    navigateToJob: (String) -> Unit,
    navigateToRestaurants: (String) -> Unit,
    navigateToAbout: (String) -> Unit,
    navigationIconClicked: () -> Unit,

    ) {


    val menuBlocks = board?.blocks?.leftSideBar?.dropWhile {
        it.type == "stats"
    }

    Column(
    ) {
        LazyColumn(
            state = scrollState,
            contentPadding = rememberInsetsPaddingValues(
                insets = LocalWindowInsets.current.systemBars,
                applyTop = false
            )
        ) {

            item {
                MainHeader(navigationIconClicked)
                menuBlocks?.forEachIndexed { index, menuBlock ->
                    viewModel.fetchBlock(menuBlock.slug ?: "")
                    if (viewState.homeCategories.isNotEmpty()) {
                        val items = viewState.homeCategories
                        when (index) {
                            0 -> {
                                Text(
                                    text = menuBlock.title ?: "",
                                    modifier = Modifier.fillMaxWidth(),
                                    color = MyTheme.colors.textSecondary,
                                    textAlign = TextAlign.Center,
                                    style = MaterialTheme.typography.h5,
                                )

                                RowsList(
                                    viewModel,
                                    ArrayList(items),
                                    scrollState,
                                    navigateToDeals,
                                    navigateToJob,
                                    navigateToRestaurants,
                                    navigateToAbout
                                )
                            }
                        }
                    }
                }
            }
        }

    }


}

@Composable
fun RowsList(
    viewModel: HomeGroupViewModel,
    itemsList: ArrayList<Block>?,
    scrollState: LazyListState,
    navigateToDeals: (String) -> Unit,
    navigateToJob: (String) -> Unit,
    navigateToRestaurants: (String) -> Unit,
    navigateToAbout: (String) -> Unit,
) {


    Column(
        modifier = Modifier
            .statusBarsPadding()
    ) {
        StaggeredVerticalGrid(
            maxColumnWidth = 220.dp,
            modifier = Modifier.padding(12.dp)
        ) {
            itemsList?.forEachIndexed { index, item ->
                val icon = when (index) {
                    0 -> {
                        painterResource(R.drawable.burgeries)
                    }
                    1 -> {
                        painterResource(R.drawable.burgerland)
                    }
                    2 -> {
                        painterResource(R.drawable.ic_logo)
                    }
                    3 -> {
                        painterResource(R.drawable.burgerjob)
                    }
                    else -> {
                        painterResource(R.drawable.ic_logo)
                    }
                }
                MainRow(item, icon, { slug ->
                    when (index) {
                        0 -> {
                            navigateToDeals(slug)
                        }
                        1 -> {
                            navigateToRestaurants(slug)
                        }
                        2 -> {
                            navigateToAbout(slug)

                        }
                        3 -> {
                            navigateToJob(slug)

                        }
                        else -> {
                            navigateToAbout(slug)
                        }
                    }
                })

            }

        }
    }


}

@Composable
fun MainRow(
    block: Block,
    icon: Painter,
    onItemClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    MySurface(
        shape = MaterialTheme.shapes.medium,
        modifier = modifier.padding(
            start = 8.dp,
            end = 8.dp,
            bottom = 16.dp
        ),
        color = colorMid
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .clickable(onClick = { onItemClick(block.block?.slug ?: "") })
        ) {

            Image(
                painter = icon,
                contentDescription = stringResource(R.string.gallery),
                modifier = Modifier.height(150.dp),
                contentScale = ContentScale.FillWidth,
            )

            Text(
                text = block.title ?: "",
                style = MaterialTheme.typography.subtitle1,
                color = MyTheme.colors.textSecondary,
                modifier = Modifier.padding(top = 8.dp, bottom = 16.dp)
            )
        }
    }
}

@Composable
fun StaggeredVerticalGrid(
    modifier: Modifier = Modifier,
    maxColumnWidth: Dp,
    content: @Composable () -> Unit
) {
    Layout(
        content = content,
        modifier = modifier
    ) { measurables, constraints ->
        check(constraints.hasBoundedWidth) {
            "Unbounded width not supported"
        }
        val columns = ceil(constraints.maxWidth / maxColumnWidth.toPx()).toInt()
        val columnWidth = constraints.maxWidth / columns
        val itemConstraints = constraints.copy(maxWidth = columnWidth)
        val colHeights = IntArray(columns) { 0 } // track each column's height
        val placeables = measurables.map { measurable ->
            val column = shortestColumn(colHeights)
            val placeable = measurable.measure(itemConstraints)
            colHeights[column] += placeable.height
            placeable
        }

        val height = colHeights.maxOrNull()?.coerceIn(constraints.minHeight, constraints.maxHeight)
            ?: constraints.minHeight
        layout(
            width = constraints.maxWidth,
            height = height
        ) {
            val colY = IntArray(columns) { 0 }
            placeables.forEach { placeable ->
                val column = shortestColumn(colY)
                placeable.place(
                    x = columnWidth * column,
                    y = colY[column]
                )
                colY[column] += placeable.height
            }
        }
    }
}

private fun shortestColumn(colHeights: IntArray): Int {
    var minHeight = Int.MAX_VALUE
    var column = 0
    colHeights.forEachIndexed { index, height ->
        if (height < minHeight) {
            minHeight = height
            column = index
        }
    }
    return column
}



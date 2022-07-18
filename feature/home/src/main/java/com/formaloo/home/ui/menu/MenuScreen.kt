package com.formaloo.home.ui.menu

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ShoppingBasket
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.unit.dp
import com.formaloo.home.ui.*
import com.formaloo.home.ui.components.MyScaffold
import com.formaloo.home.ui.components.QuantitySelector
import com.formaloo.home.ui.theme.MyTheme
import com.formaloo.home.vm.MenuViewModel
import com.formaloo.model.form.Fields
import com.formaloo.model.form.Form
import com.formaloo.model.form.Image
import com.google.accompanist.insets.LocalWindowInsets
import com.google.accompanist.insets.navigationBarsPadding
import com.google.accompanist.insets.rememberInsetsPaddingValues
import com.google.accompanist.insets.systemBarsPadding
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.rememberPagerState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.koin.androidx.compose.viewModel
import timber.log.Timber

data class ProductDetailUiState(
    val productDetail: Fields? = null
)

data class ProductCountUiState(
    val count: Int = 0,
    val product: Fields? = null
)

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun MenuScreen(
    blockSlug: String,
    openWebView: (String) -> Unit,
    navigationIconClicked: () -> Unit,
    openHomePage: () -> Unit,
    scaffoldState: BottomSheetScaffoldState = rememberBottomSheetScaffoldState(),
) {
    val vm by viewModel<MenuViewModel>()
    vm.fetchBlock(blockSlug)

    val viewState by vm.uiState.collectAsState()
    val scope = rememberCoroutineScope()
    val uriHandler = LocalUriHandler.current
    val scrollState = rememberLazyListState()
    val closeSheet = { scope.launch { scaffoldState.bottomSheetState.collapse() } }
    val openSheet = { scope.launch { scaffoldState.bottomSheetState.expand() } }


    val _productDetailState = MutableStateFlow(ProductDetailUiState())
    val productDetailState: StateFlow<ProductDetailUiState> = _productDetailState.asStateFlow()

    BottomSheetScaffold(
        modifier = Modifier
            .background(MyTheme.colors.uiBackground)
            .clickable(onClick = {
                closeSheet()
            }),
        sheetBackgroundColor = (MyTheme.colors.uiBackground),
        backgroundColor = (MyTheme.colors.uiBackground),
        sheetContent = {
            ProductDetail(
                product = productDetailState.collectAsState().value.productDetail
            )
        },
        topBar = {
            NavigationTopAppbar(viewState.menuForm?.title ?: "", navigationIconClicked, Color.Black)

        },
        scaffoldState = scaffoldState,
        snackbarHost = { SnackbarHost(hostState = it, modifier = Modifier.systemBarsPadding()) },
        sheetPeekHeight = 0.dp,
    ) { innerPadding ->
        val modifier = Modifier.padding(innerPadding)
        LoadingContent(
            empty = viewState.initialLoad,
            emptyContent = { FullScreenLoading() },
            loading = viewState.loading,
            onRefresh = { vm.fetchBlock(blockSlug) },
            content = {
                MenuContent(
                    vm = vm,
                    menuForm = viewState.menuForm,
                    scrollState = scrollState,
                    openProductDetail = { rd ->
                        _productDetailState.update {
                            it.copy(rd)
                        }
                        openSheet()


                    },
                    openHomePage = openHomePage,
                    closeSheet = {
                        closeSheet()
                    },
                )
            }
        )
    }


}

@Composable
fun MenuContent(
    vm: MenuViewModel,
    menuForm: Form?,
    scrollState: LazyListState,
    openProductDetail: (Fields) -> Unit,
    openHomePage: () -> Unit,
    closeSheet: () -> Unit,
) {
    var sendOrder = remember { false }

    val orderReqData = hashMapOf<String, Int>()
    val orderUiStat by vm.orderUiState.collectAsState()

    if (orderUiStat.loading) {
        ScreenLoading()
    }
    val uriHandler = LocalUriHandler.current

    if (orderUiStat.orderDetail != null) {
        val submitterRefererAddress = orderUiStat.orderDetail?.submitter_referer_address
        uriHandler.openUri(submitterRefererAddress ?: "https://formaloo.net/k24ec")
        openHomePage()
    }

    MyScaffold(
        modifier = Modifier
            .clickable(onClick = {
                closeSheet()
            }),
        backgroundColor = MyTheme.colors.uiBackground,
        floatingActionButton = {
            ExtendedFloatingActionButton(
                text = { Text(text = menuForm?.button_text ?: "Submit") },
                icon = {
                    Icon(
                        Icons.Outlined.ShoppingBasket,
                        contentDescription = "Favorite"
                    )
                },
                shape = MaterialTheme.shapes.large.copy(CornerSize(percent = 50)),
                backgroundColor = MyTheme.colors.brand,
                contentColor = MyTheme.colors.uiBackground,
                onClick = {
                    if (orderReqData.values.isNotEmpty())
                        vm.submitOrder(menuForm?.slug ?: "", orderReqData)
                },
                modifier = Modifier
                    .navigationBarsPadding()
            )

        },
    ) {
        val modifier = Modifier.padding(bottom = 16.dp)

        menuForm?.let { form ->
            LazyColumn(
                modifier = modifier.padding(start = 16.dp, top = 16.dp, end = 16.dp),
                state = scrollState,
                contentPadding = rememberInsetsPaddingValues(
                    insets = LocalWindowInsets.current.systemBars,
                    applyTop = false
                )
            ) {
                item {
                    MenuExtraData(
                        form,
                        modifier,
                        { field, number ->
                            val slug = field.slug ?: ""
                            if (number == 0) {
                                orderReqData.remove(slug)
                            } else {
                                orderReqData[slug] = number

                            }
                        },
                        openProductDetail
                    )

                }

            }
        }
    }

}

@Composable
fun MenuExtraData(
    form: Form, modifier: Modifier, productCountChanged: (Fields, Int) -> Unit,
    openProductDetail: (Fields) -> Unit,
) {

    form.fields_list?.forEach { field ->
        when (field.type) {
            FIELD_PRODUCT -> {
                ProductRow(
                    field,
                    onProductClick = {},
                    productCountChanged,
                    openProductDetail,
                )
            }
            else -> {
                MenuTitle(field.title ?: "", modifier)
                HtmlText(html = field.description ?: "", modifier)

            }
        }


    }

}


@Composable
fun MenuTitle(title: String, modifier: Modifier) {
    Timber.e("MenuTitle $title")

    Text(
        modifier = modifier,
        text = title,
        style = MaterialTheme.typography.subtitle1
    )

}

@Composable
fun ProductRow(
    field: Fields,
    onProductClick: (String) -> Unit,
    productCountChanged: (Fields, Int) -> Unit,
    openProductDetail: (Fields) -> Unit,
) {

    Column(
        modifier = Modifier
            .height(112.dp)
            .padding(bottom = 16.dp)
            .background(MyTheme.colors.uiBackground)
    ) {
        Row(modifier = Modifier.clickable(onClick = {
            openProductDetail(field)
        })) {
            Column(
                modifier = Modifier.weight(1f)
            ) {
                ProductImage(field.images)
            }

            Column(
                modifier = Modifier
                    .padding(4.dp)
                    .weight(2f)
            ) {
                ProductName(field.title ?: "")
                ProductExtraData(field)

            }
            Column(
                modifier = Modifier.weight(1f)
            ) {
                CartBar(field, productCountChanged)
            }
        }
    }

}

@Composable
fun ProductImage(images: ArrayList<Image>?) {
    FormImage(
        imageUrl = images?.get(0)?.image ?: "",
        contentDescription = null,
        modifier = Modifier.aspectRatio(1f)
    )

}

@Composable
fun ProductName(title: String) {

    Text(
        text = title,
        modifier = Modifier
            .padding(bottom = 4.dp),
        style = MaterialTheme.typography.subtitle1

    )


}

@Composable
fun ProductExtraData(field: Fields) {
    Text(
        text = "${field.unit_price ?: ""}$",
        modifier = Modifier,
        style = MaterialTheme.typography.body1,
        maxLines = 1

    )


}


@OptIn(ExperimentalPagerApi::class)
@Composable
fun ProductDetail(
    product: Fields?,
) {

    product?.let {
        val images = product.images
        Column(
            modifier = Modifier
                .padding(start = 24.dp, top = 20.dp, bottom = 36.dp, end = 24.dp)
                .background(MyTheme.colors.uiBackground)
        ) {
            Slider(images)

            Text(
                text = product.title ?: "",
                style = MaterialTheme.typography.h5,
                modifier = Modifier.padding(bottom = 16.dp, top = 16.dp)
            )

            ProductExtraData(product)

        }
    }

}


@Composable
private fun CartBar(
    field: Fields,
    productCountChanged: (Fields, Int) -> Unit,
) {
    val maxValue = field.max_value?.toInt() ?: 0
    val minValue = field.min_value?.toInt() ?: 0
    val (count, updateCount) = remember { mutableStateOf(0) }

    QuantitySelector(
        count = count,
        decreaseItemCount = {
            if (count > minValue) {
                updateCount(count - 1)
                productCountChanged(field, count - 1)
            }
        },
        increaseItemCount = {
            if (count < maxValue) {
                updateCount(count + 1)
                productCountChanged(field, count + 1)

            }
        }
    )
}

private val BottomBarHeight = 56.dp
private val HzPadding = Modifier.padding(horizontal = 24.dp)

const val FIELD_PRODUCT = "product"

@OptIn(ExperimentalPagerApi::class)
@Composable
fun Slider(images: ArrayList<Image>?) {
    val state = rememberPagerState(pageCount = images?.size ?: 0)
    HorizontalPager(state = state) { page ->

        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            FormImage(
                imageUrl = images?.get(page)?.image ?: "",
                contentDescription = "",
                modifier = Modifier.height(200.dp)
            )
        }

    }

    Spacer(modifier = Modifier.padding(4.dp))

    DotsIndicator(
        totalDots = images?.size ?: 0,
        selectedIndex = state.currentPage,
        selectedColor = MyTheme.colors.brand,
        unSelectedColor = MyTheme.colors.uiBorder,

        )
}

@Composable
fun DotsIndicator(
    totalDots: Int,
    selectedIndex: Int,
    selectedColor: Color,
    unSelectedColor: Color,
) {

    Box(modifier = Modifier.fillMaxWidth(),
        contentAlignment = Alignment.Center,
        content = {
            LazyRow(
                modifier = Modifier
                    .wrapContentWidth()
                    .wrapContentHeight()


            ) {

                items(totalDots) { index ->
                    if (index == selectedIndex) {
                        Box(
                            modifier = Modifier
                                .size(10.dp)
                                .clip(CircleShape)
                                .background(selectedColor)
                        )
                    } else {
                        Box(
                            modifier = Modifier
                                .size(10.dp)
                                .clip(CircleShape)
                                .background(unSelectedColor)
                        )
                    }

                    if (index != totalDots - 1) {
                        Spacer(modifier = Modifier.padding(horizontal = 3.dp))
                    }
                }
            }
        })

}




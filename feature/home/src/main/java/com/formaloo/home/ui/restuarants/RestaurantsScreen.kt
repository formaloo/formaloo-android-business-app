package com.formaloo.home.ui.restuarants

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.text.ClickableText
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Directions
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.startActivity
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import com.formaloo.home.ui.NavigationTopAppbar
import com.formaloo.home.ui.theme.MyTheme
import com.formaloo.home.vm.RestaurantViewModel
import com.formaloo.model.Converter
import com.formaloo.model.boards.block.Block
import com.formaloo.model.local.Restaurant
import com.formaloo.model.form.submitForm.RenderedData
import com.formaloo.model.form.submitForm.Row
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.apache.commons.lang3.math.NumberUtils
import org.koin.androidx.compose.viewModel


data class RestaurantDetailUiState(
    val resDetail: Map<String, RenderedData>? = null
)

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun RestaurantsScreen(
    blockSlug: String,
    onRestaurantClick: (String) -> Unit,
    navigationIconClicked: () -> Unit,

    ) {


    val scope = rememberCoroutineScope()
    val scaffoldState = rememberBottomSheetScaffoldState()
    val closeSheet = { scope.launch { scaffoldState.bottomSheetState.collapse() } }
    val openSheet = { scope.launch { scaffoldState.bottomSheetState.expand() } }

    val _rdState = MutableStateFlow(RestaurantDetailUiState())
    val rdState: StateFlow<RestaurantDetailUiState> = _rdState.asStateFlow()


    val current = LocalContext.current
    BottomSheetScaffold(
//        drawerGesturesEnabled = scaffoldState.drawerState.isOpen,
        sheetBackgroundColor = (MyTheme.colors.uiBackground),
//        sheetContentColor = (EventTheme.colors.uiBackground),
        backgroundColor = (MyTheme.colors.uiBackground),
        sheetContent = {
            RestaurantDetail(renderedData = rdState.collectAsState().value.resDetail)
        },
        scaffoldState = scaffoldState,
        topBar = {
            NavigationTopAppbar("Restaurants", navigationIconClicked, Color.Black)
        },
        floatingActionButton = {
            if (scaffoldState.bottomSheetState.isExpanded) {
                FloatingActionButton(
                    onClick = {
                        openRestaurantLocation(rdState.value.resDetail?.values, current)
                    },
                    backgroundColor = MyTheme.colors.brand,
                    contentColor = MyTheme.colors.uiBackground,
                ) {
                    Icon(Icons.Default.Directions, contentDescription = "Localized description")
                }
            }
        },
        floatingActionButtonPosition = FabPosition.End,
        sheetPeekHeight = 0.dp,
    ) { innerPadding ->

        val vm by viewModel<RestaurantViewModel>()

        vm.fetchBlock(blockSlug)

        val viewState by vm.uiState.collectAsState()
        RestaurantContent(
            isRefreshing = viewState.loading,
            block = viewState.restaurantBlock,
            viewModel = vm,
            onRestaurantClick = onRestaurantClick,
            openResDetail = { rd ->
                _rdState.update {
                    it.copy(rd)
                }
                openSheet()

            },
            closeSheet = {
                closeSheet()
            }

        )


    }
}


@Composable
fun RestaurantContent(
    isRefreshing: Boolean,
    block: Block?,
    viewModel: RestaurantViewModel,
    onRestaurantClick: (String) -> Unit,
    openResDetail: (Map<String, RenderedData>) -> Unit,
    closeSheet: () -> Unit,
) {

    val context = LocalContext.current
    var myLoc = remember { false }

    var isMapLoaded by remember { mutableStateOf(false) }

    val LOCATION_PERMS = Manifest.permission.ACCESS_FINE_LOCATION


    val launcher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        myLoc = isGranted
        if (isGranted) {
            // Permission Accepted: Do something
            Log.d("ExampleScreen", "PERMISSION GRANTED")

        } else {
            // Permission Denied: Do something
            Log.d("ExampleScreen", "PERMISSION DENIED")
        }
    }

    // Check permission
    when (PackageManager.PERMISSION_GRANTED) {
        ContextCompat.checkSelfPermission(context, LOCATION_PERMS) -> {
            // Some works that require permission
            Log.d("ExampleScreen", "Code requires permission")
        }
        else -> {
            // Asking for permission
            SideEffect {
                launcher.launch(LOCATION_PERMS)

            }
        }
    }

    val flow = viewModel.fetchRestaurantList(block?.slug ?: "", true)

    val lazyPagingItems: LazyPagingItems<Restaurant> = flow.collectAsLazyPagingItems()

    val cameraPositionState = rememberCameraPositionState()

    LazyRow(modifier = Modifier.fillMaxSize()){
        item {
            LazyColumn(modifier = Modifier.fillParentMaxWidth()){
                item {
                    GoogleMap(
                        modifier = Modifier.fillParentMaxHeight(),
                        cameraPositionState = cameraPositionState,
                        properties = MapProperties(isMyLocationEnabled = myLoc),
                        onMapLoaded = {
                            isMapLoaded = true
                        },
                        onMapClick = {
                            closeSheet()
                        }

                    ) {


                        lazyPagingItems.itemSnapshotList.forEachIndexed { index, restaurant ->
                            restaurant?.let { it ->
                                val row = Converter().to<Row>(it.row)
                                val values = row?.rendered_data?.values

                                val name = values?.findLast {
                                    it.alias == "name"
                                }?.let {
                                    it.raw_value?.toString()
                                } ?: ""

                                val latitude = values?.findLast {
                                    it.alias == "lat"
                                }?.let {
                                    it.raw_value?.toString()
                                }

                                val longitude = values?.findLast {
                                    it.alias == "long"
                                }?.let {
                                    it.raw_value?.toString()
                                }

                                if (latitude != null && NumberUtils.isCreatable(latitude) && longitude != null && NumberUtils.isCreatable(
                                        longitude
                                    )
                                ) {
                                    val restaurantLoc =
                                        LatLng(latitude.toDouble(), longitude.toDouble())

                                    cameraPositionState.position =
                                        CameraPosition.fromLatLngZoom(restaurantLoc, 14f)

                                    Marker(
                                        state = MarkerState(position = restaurantLoc),
//                            title = name,

                                        snippet = name,
                                        onClick = {
                                            it.showInfoWindow()
                                            row.rendered_data?.let { rd ->
                                                openResDetail(rd)

                                            }

                                            true
                                        }

                                    )


                                }


                            }
                        }
                    }
                }

            }
        }
    }



    if (!isMapLoaded) {
        RadioButton(
            selected = true,
            enabled = true,
            onClick = {
                cameraPositionState.move(CameraUpdateFactory.zoomIn())

            }
        )
    }

}


@Composable
fun RestaurantDetail(
    renderedData: Map<String, RenderedData>?,
    modifier: Modifier = Modifier
) {
    val values = renderedData?.values

    val title = values?.findLast {
        it.alias == "name"
    }?.let {
        it.raw_value?.toString() ?: ""
    } ?: ""

    Column(
        modifier = Modifier
            .padding(start = 24.dp, top = 20.dp, bottom = 36.dp, end = 24.dp)
            .background(MyTheme.colors.uiBackground)
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.h5,
            modifier = Modifier.padding(bottom = 16.dp)
        )
        removeExtraData(values)?.forEach {
            Column() {
                val value = it.raw_value?.toString() ?: "-"
                Text(
                    text = it.title ?: "",
                    modifier = Modifier.padding(bottom = 8.dp),
                    style = MaterialTheme.typography.body2,
                    color = MyTheme.colors.brandSecondary

                )
                if (it.alias == "website") {
                    val uriHandler = LocalUriHandler.current

                    ClickableText(
                        text = AnnotatedString(value),
                        onClick = { offset ->
                            uriHandler.openUri(value)
                        },
                        style = MaterialTheme.typography.overline,
                        modifier = Modifier.padding(bottom = 16.dp),

                        )
                } else {
                    SelectionContainer {
                        Text(
                            text = value,
                            style = MaterialTheme.typography.subtitle1,
                            modifier = Modifier.padding(bottom = 16.dp)

                        )


                    }

                }

            }

        }

    }
}

fun removeExtraData(values: Collection<RenderedData>?): Collection<RenderedData>? {

    return values?.filter {
        !it.alias.equals("name")
    }?.filter {
        !it.alias.equals("long")
    }?.filter {
        !it.alias.equals("lat")
    }
}

fun openRestaurantLocation(values: Collection<RenderedData>?, current: Context) {
    val latData = values?.findLast {
        it.alias == "lat"
    }
    val longData = values?.findLast {
        it.alias == "long"
    }
    val titleData = values?.findLast {
        it.alias == "name"
    }
    val lat = latData?.value
    val long = longData?.value
    val title = titleData?.value

    if (lat != null && long != null) {
        val gmmIntentUri: Uri =
            Uri.parse("geo:$lat,$long?q=" + Uri.encode("$title"))
        val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
        mapIntent.setPackage("com.google.android.apps.maps")
        startActivity(current, mapIntent, null)

    }


}



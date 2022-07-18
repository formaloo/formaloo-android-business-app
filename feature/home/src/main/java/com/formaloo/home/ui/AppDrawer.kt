package com.formaloo.home.ui


import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.formaloo.home.ui.theme.MyTheme
import com.formaloo.home.vm.HomeGroupViewModel
import com.formaloo.model.boards.block.Block
import com.formaloo.home.R
import org.koin.androidx.compose.viewModel
import timber.log.Timber

@Composable
fun AppDrawer(
    menuBlocks: ArrayList<Block>,
    currentRoute: String,
    navigateToHome: () -> Unit,
    navigateToDeals: (String) -> Unit,
    navigateToJob: (String) -> Unit,
    navigateToRestaurants: (String) -> Unit,
    navigateToAbout: (String) -> Unit,
    closeDrawer: () -> Unit
) {
    val homeVM by viewModel<HomeGroupViewModel>()
    val viewState by homeVM.state.collectAsState()


    Column(modifier = Modifier.fillMaxSize()) {
        Spacer(Modifier.height(4.dp))
        Logo(Modifier.padding(4.dp))
        Divider(color = MyTheme.colors.textSecondary.copy(alpha = .2f))
        DrawerButton(
            icon = Icons.Filled.Home,
            label = stringResource(id = R.string.home),
            isSelected = currentRoute == MainDestinations.HOME_ROUTE,
            action = {
                navigateToHome()
                closeDrawer()
            }
        )

        menuBlocks.forEachIndexed { index, menuBlock ->

            homeVM.fetchBlock(menuBlock.slug ?: "")

            if (viewState.homeCategories.isNotEmpty()) {
                val items = viewState.homeCategories
                Timber.e("homeBlocks Items $items")
                when (index) {
                    0 -> {
                        val dealsBlock = if (items.isNotEmpty()) items[0] else null
                        val restaurantBlock = if (items.size > 1) items[1] else null
                        val aboutBlock = if (items.size > 2) items[2] else null
                        val jobBlock = if (items.size > 3) items[3] else null

                        DrawerButton(
                            icon = Icons.Filled.Fastfood,
                            label = dealsBlock?.title ?: "",
                            isSelected = currentRoute == "${MainDestinations.DEALS_ROUTE}/${dealsBlock?.slug ?: ""}",
                            action = {
                                navigateToDeals(dealsBlock?.block?.slug ?: "")
                                closeDrawer()
                            }
                        )

                        DrawerButton(
                            icon = Icons.Filled.FoodBank,
                            label = restaurantBlock?.title ?: "",
                            isSelected = currentRoute == "${MainDestinations.RESTAURANT_ROUTE}/${restaurantBlock?.slug ?: ""}",
                            action = {
                                navigateToRestaurants(restaurantBlock?.block?.slug ?: "")
                                closeDrawer()
                            }
                        )

                        DrawerButton(
                            icon = Icons.Filled.Work,
                            label = jobBlock?.title ?: "",

                            isSelected = currentRoute == MainDestinations.JOB_ROUTE,
                            action = {
                                navigateToJob(jobBlock?.block?.slug ?: "")
                                closeDrawer()
                            }
                        )
                        DrawerButton(
                            icon = Icons.Filled.Info,
                            label = aboutBlock?.title ?: "",
                            isSelected = currentRoute == MainDestinations.ABOUT_ROUTE,
                            action = {
                                navigateToAbout(aboutBlock?.block?.slug ?: "")
                                closeDrawer()
                            }
                        )
                    }
                    else -> {
                        items.forEach {
                            DrawerButton(
                                icon = Icons.Filled.ListAlt,
                                label = it.title ?: "",
                                isSelected = currentRoute == MainDestinations.HOME_ROUTE,
                                action = {
                                    closeDrawer()
                                }
                            )
                        }
                    }
                }
            }
        }

    }
}

@Composable
private fun Logo(modifier: Modifier = Modifier) {
    Row(modifier = modifier) {
        Image(
            painter = painterResource(R.drawable.ic_logo),
            contentDescription = null, // decorative
//            colorFilter = ColorFilter.tint(MaterialTheme.colors.primary)
        )

    }
}

@Composable
private fun DrawerButton(
    icon: ImageVector,
    label: String,
    isSelected: Boolean,
    action: () -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = MyTheme.colors
    val imageAlpha = if (isSelected) {
        1f
    } else {
        0.6f
    }
    val textIconColor = if (isSelected) {
        colors.textSecondary
    } else {
        colors.textSecondary.copy(alpha = 0.6f)
    }
    val backgroundColor = if (isSelected) {
        colors.textSecondary.copy(alpha = 0.12f)
    } else {
        Color.Transparent
    }

    val surfaceModifier = modifier
        .padding(start = 8.dp, top = 8.dp, end = 8.dp)
        .fillMaxWidth()
    Surface(
        modifier = surfaceModifier,
        color = backgroundColor,
        shape = MaterialTheme.shapes.small
    ) {
        TextButton(
            onClick = action,
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                horizontalArrangement = Arrangement.Start,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Image(
                    imageVector = icon,
                    contentDescription = null, // decorative
                    colorFilter = ColorFilter.tint(textIconColor),
                    alpha = imageAlpha
                )
                Spacer(Modifier.width(16.dp))
                Text(
                    text = label,
                    style = MaterialTheme.typography.body2,
                    color = textIconColor
                )
            }
        }
    }
}


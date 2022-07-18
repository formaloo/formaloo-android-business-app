package com.formaloo.home.ui

import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ScaffoldState
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.lifecycle.Lifecycle
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.formaloo.home.ui.MainDestinations.BLOCK_SLUG
import com.formaloo.home.ui.about.AboutScreen
import com.formaloo.home.ui.career.CareerScreen
import com.formaloo.home.ui.home.MainScreen
import com.formaloo.home.ui.menu.MenuScreen
import com.formaloo.home.ui.restuarants.RestaurantsScreen
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

object MainDestinations {
    const val HOME_ROUTE = "home"
    const val RESTAURANT_ROUTE = "restaurant"
    const val DEALS_ROUTE = "deals"
    const val JOB_ROUTE = "job"
    const val ABOUT_ROUTE = "about"

    const val BLOCK_SLUG = "block_slug"

}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun EventNavGraph(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController(),
    scaffoldState: ScaffoldState = rememberScaffoldState(),
    startDestination: String = MainDestinations.HOME_ROUTE,
) {

    val coroutineScope = rememberCoroutineScope()
    val openDrawer: () -> Unit = { coroutineScope.launch { scaffoldState.drawerState.open() } }
    val closeDrawer: () -> Unit = { coroutineScope.launch { scaffoldState.drawerState.close() } }

    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier
    ) {
        composable(
            MainDestinations.HOME_ROUTE
        ) { backStackEntry ->

            MainScreen(
                scaffoldState,
                navigateToDeals = { blockSlug -> navController.navigate("${MainDestinations.DEALS_ROUTE}/${blockSlug}") },
                navigateToJob = { blockSlug -> navController.navigate("${MainDestinations.JOB_ROUTE}/${blockSlug}") },
                navigateToRestaurants = { blockSlug -> navController.navigate("${MainDestinations.RESTAURANT_ROUTE}/${blockSlug}") },
                navigateToAbout = { blockSlug -> navController.navigate("${MainDestinations.ABOUT_ROUTE}/${blockSlug}") },
                navigationIconClicked = {
                    checkDrawer(coroutineScope, scaffoldState)
                },
            )
        }

        composable(
            "${MainDestinations.DEALS_ROUTE}/{$BLOCK_SLUG}",
            arguments = listOf(navArgument(BLOCK_SLUG) { type = NavType.StringType })
        ) { backStackEntry ->
            val arguments = requireNotNull(backStackEntry.arguments)
            val blockSlug = arguments.getString(BLOCK_SLUG) ?: ""
            MenuScreen(
                blockSlug, openWebView = {},
                navigationIconClicked = {
                    checkDrawer(coroutineScope, scaffoldState)
                },
                openHomePage={
                    navController.navigateUp()
                }
            )


        }
        composable(
            "${MainDestinations.RESTAURANT_ROUTE}/{$BLOCK_SLUG}",
            arguments = listOf(navArgument(BLOCK_SLUG) { type = NavType.StringType })
        ) { backStackEntry ->
            val arguments = requireNotNull(backStackEntry.arguments)
            val blockSlug = arguments.getString(BLOCK_SLUG) ?: ""

            RestaurantsScreen(
                blockSlug, onRestaurantClick = {},
                navigationIconClicked = {
                    checkDrawer(coroutineScope, scaffoldState)
                },
            )
        }
        composable(
            "${MainDestinations.JOB_ROUTE}/{$BLOCK_SLUG}",
            arguments = listOf(navArgument(BLOCK_SLUG) { type = NavType.StringType })
        ) { backStackEntry ->
            val arguments = requireNotNull(backStackEntry.arguments)
            val blockSlug = arguments.getString(BLOCK_SLUG) ?: ""
            CareerScreen(
                blockSlug = blockSlug,
                navigationIconClicked = {
                    checkDrawer(coroutineScope, scaffoldState)
                },
            )

        }

        composable(
            "${MainDestinations.ABOUT_ROUTE}/{$BLOCK_SLUG}",
            arguments = listOf(navArgument(BLOCK_SLUG) { type = NavType.StringType })
        ) { backStackEntry ->
            val arguments = requireNotNull(backStackEntry.arguments)
            val blockSlug = arguments.getString(BLOCK_SLUG) ?: ""
            AboutScreen(
                blockSlug = blockSlug,
                navigationIconClicked = {
                    checkDrawer(coroutineScope, scaffoldState)
                },
            )
        }

    }
}

fun checkDrawer(coroutineScope: CoroutineScope, scaffoldState: ScaffoldState) {
    coroutineScope.launch { if (scaffoldState.drawerState.isClosed) scaffoldState.drawerState.open() else scaffoldState.drawerState.close() }

}

/**
 * If the lifecycle is not resumed it means this NavBackStackEntry already processed a nav event.
 *
 * This is used to de-duplicate navigation events.
 */
private fun NavBackStackEntry.lifecycleIsResumed() =
    this.lifecycle.currentState == Lifecycle.State.RESUMED


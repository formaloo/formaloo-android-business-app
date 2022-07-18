package com.formaloo.home.ui

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import androidx.compose.foundation.layout.padding
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.formaloo.home.ui.theme.MyTheme
import com.formaloo.model.boards.board.Board
import com.google.accompanist.insets.ProvideWindowInsets
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import kotlinx.coroutines.launch
import com.formaloo.home.R
import com.formaloo.home.ui.components.MyScaffold

@Composable
fun BusinessApp(board: Board) {

    MyTheme {
        ProvideWindowInsets {
            val systemUiController = rememberSystemUiController()
            val darkIcons = MaterialTheme.colors.isLight
            SideEffect {
                systemUiController.setSystemBarsColor(Color.Transparent, darkIcons = darkIcons)
            }

            board.blocks?.leftSideBar?.dropWhile {
                it.type == "stats"
            }


            val navController = rememberNavController()
            val coroutineScope = rememberCoroutineScope()
            val scaffoldState = rememberScaffoldState()
            val navBackStackEntry by navController.currentBackStackEntryAsState()
            val currentRoute = navBackStackEntry?.destination?.route ?: MainDestinations.HOME_ROUTE

            val lsBlocks = board.blocks?.leftSideBar?.dropWhile {
                it.type == "stats"
            }

            MyScaffold(
                scaffoldState = scaffoldState,
                drawerContent = {
                    AppDrawer(
                        menuBlocks = ArrayList(lsBlocks ?: listOf()),
                        currentRoute = currentRoute,
                        navigateToHome = { navController.navigate(MainDestinations.HOME_ROUTE) },
                        navigateToDeals = { blockSlug -> navController.navigate("${MainDestinations.DEALS_ROUTE}/${blockSlug}") },
                        navigateToJob = { blockSlug -> navController.navigate("${MainDestinations.JOB_ROUTE}/${blockSlug}") },
                        navigateToRestaurants = { blockSlug -> navController.navigate("${MainDestinations.RESTAURANT_ROUTE}/${blockSlug}") },
                        navigateToAbout = { blockSlug -> navController.navigate("${MainDestinations.ABOUT_ROUTE}/${blockSlug}") },
                        closeDrawer = { coroutineScope.launch { scaffoldState.drawerState.close() } },
                    )
                },
            ) { innerPaddingModifier ->
                EventNavGraph(
                    modifier = Modifier.padding(innerPaddingModifier),
                    navController = navController,
                    scaffoldState = scaffoldState

                )
            }

        }

    }
}

@Suppress("DEPRECATION")
private fun checkIfOnline(context: Context): Boolean {
    val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        val capabilities = cm.getNetworkCapabilities(cm.activeNetwork) ?: return false

        capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) &&
                capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED)
    } else {
        cm.activeNetworkInfo?.isConnectedOrConnecting == true
    }
}

@Composable
fun OfflineDialog(onRetry: () -> Unit) {
    AlertDialog(
        onDismissRequest = {},
        title = { Text(text = stringResource(R.string.no_internet)) },
        text = { Text(text = stringResource(R.string.no_internet)) },
        confirmButton = {
            TextButton(onClick = onRetry) {
                Text(stringResource(R.string.try_again))
            }
        }
    )
}


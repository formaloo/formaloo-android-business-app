package com.formaloo.home.ui.career

import android.graphics.Bitmap
import android.view.ViewGroup
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.Scaffold
import androidx.compose.material.ScaffoldState
import androidx.compose.material.SnackbarHost
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.formaloo.home.ui.FullScreenLoading
import com.formaloo.home.ui.LoadingContent
import com.formaloo.home.ui.NavigationTopAppbar
import com.formaloo.home.ui.theme.MyTheme
import com.formaloo.home.vm.CareerViewModel
import com.formaloo.model.form.Form
import com.google.accompanist.insets.LocalWindowInsets
import com.google.accompanist.insets.rememberInsetsPaddingValues
import com.google.accompanist.insets.systemBarsPadding
import org.koin.androidx.compose.viewModel
import timber.log.Timber
import com.formaloo.home.R
import com.formaloo.home.ui.components.MyScaffold

@Composable
fun CareerScreen(
    blockSlug: String,
    scaffoldState: ScaffoldState = rememberScaffoldState(),
    navigationIconClicked: () -> Unit,

    ) {
    val vm by viewModel<CareerViewModel>()
    vm.fetchBlock(blockSlug)
    val viewState by vm.uiState.collectAsState()
    val scrollState = rememberLazyListState()


    MyScaffold(
        scaffoldState = scaffoldState,
        snackbarHost = { SnackbarHost(hostState = it, modifier = Modifier.systemBarsPadding()) },
        backgroundColor = MyTheme.colors.uiBackground,
    ) { innerPadding ->
        val modifier = Modifier.padding(innerPadding)
        LoadingContent(
            empty = viewState.initialLoad,
            emptyContent = { FullScreenLoading() },
            loading = viewState.loading,
            onRefresh = { vm.fetchBlock(blockSlug) },
            content = {
                CareerContent(
                    isRefreshing = viewState.loading,
                    careerForm = viewState.careerForm,
                    navigationIconClicked = navigationIconClicked,
                    scrollState = scrollState


                )
            }
        )
    }


}


@Composable
fun CareerContent(
    isRefreshing: Boolean, careerForm: Form?, navigationIconClicked: () -> Unit,
    scrollState: LazyListState
) {
    val modifier = Modifier.padding(bottom = 16.dp)

    Timber.e("careerForm $careerForm")

    careerForm?.let { form ->
        val mUrl = stringResource(id = R.string.base_url) + form.address

        Column() {
            NavigationTopAppbar("", navigationIconClicked, Color.Black)

            LazyColumn(
                modifier = modifier,
                state = scrollState,
                contentPadding = rememberInsetsPaddingValues(
                    insets = LocalWindowInsets.current.systemBars,
                    applyTop = false
                )
            ) {

                item {


                    var backEnabled by remember { mutableStateOf(false) }
                    var webView: WebView? = null
                    AndroidView(
                        modifier = modifier,
                        factory = { context ->
                            WebView(context).apply {
                                layoutParams = ViewGroup.LayoutParams(
                                    ViewGroup.LayoutParams.MATCH_PARENT,
                                    ViewGroup.LayoutParams.MATCH_PARENT
                                )

                                webViewClient = object : WebViewClient() {
                                    override fun onPageStarted(
                                        view: WebView,
                                        url: String?,
                                        favicon: Bitmap?
                                    ) {
                                        backEnabled = view.canGoBack()
                                    }
                                }
                                initWebView(settings)
                                webView?.setInitialScale(1)

                                loadUrl(mUrl)
                                webView = this
                            }
                        },
                        update = {
                            webView = it
                        })

                    BackHandler(enabled = backEnabled) {
                        webView?.goBack()
                    }


                }
            }

        }

    }


}

private fun initWebView(settings: WebSettings) {

    settings.userAgentString = "Android"
    settings.loadWithOverviewMode = true
    settings.setJavaScriptEnabled(true)
    settings.useWideViewPort = true
    settings.databaseEnabled = true
    settings.allowContentAccess = true
    settings.allowFileAccessFromFileURLs = true
    settings.domStorageEnabled = true
    settings.allowFileAccess = true
    settings.setGeolocationEnabled(true)
    settings.setAppCacheEnabled(true)
    settings.setSupportMultipleWindows(true)
    settings.cacheMode = WebSettings.LOAD_DEFAULT
}




package com.mural.pochoclito.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.TextFieldValue
import androidx.navigation.NavController
import androidx.paging.LoadState
import androidx.paging.PagingData
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.items
import com.mural.domain.TvShow
import com.mural.domain.Watchable
import com.mural.pochoclito.R
import com.mural.pochoclito.ui.elements.*
import com.mural.pochoclito.viewmodel.TvShowsViewModel
import kotlinx.coroutines.flow.Flow

@Composable
fun TvShowsScreen(tvShowsViewModel: TvShowsViewModel, navController: NavController) {
    val textState = remember { mutableStateOf(TextFieldValue("")) }

    Column(Modifier.background(Color.DarkGray)) {
        SearchView(textState)
        Box() {
            TvShowList(viewModel = tvShowsViewModel, navController = navController)
            SearchList(navController = navController, state = textState, itemType = Watchable.TV)
        }
    }
}

@Composable
fun TvShowList(
    modifier: Modifier = Modifier,
    viewModel: TvShowsViewModel,
    navController: NavController
) {
    TvShowInfoList(modifier, tvShowsListData = viewModel.tvShowsData, navController = navController)
}


@Composable
fun TvShowInfoList(
    modifier: Modifier,
    tvShowsListData: Flow<PagingData<TvShow>>,
    navController: NavController
) {
    val tvShowsListItemsData: LazyPagingItems<TvShow> = tvShowsListData.collectAsLazyPagingItems()

    LazyColumn {
        items(tvShowsListItemsData) { tvShow ->
            tvShow?.let {
                ItemRow(
                    title = "${tvShow.name}",
                    subtitle = "${stringResource(id = R.string.popularity)}: ${tvShow.popularity?.toInt()} <> ${
                        stringResource(
                            id = R.string.top_rated
                        )
                    }: ${tvShow.voteAverage}",
                    imagePath = tvShow.backdropPath,
                    navController = navController,
                    itemId = tvShow.tvId,
                    itemType = Watchable.TV,
                    rowAlignment = RowAlignment.END
                )
            }
        }
        tvShowsListItemsData.apply {
            when {
                loadState.refresh is LoadState.Loading -> {
                    //You can add modifier to manage load state when first time response page is loading
                    item { LoadingView(modifier = Modifier.fillParentMaxSize()) }
                }
                loadState.append is LoadState.Loading -> {
                    //You can add modifier to manage load state when next response page is loading
                    item { LoadingItem() }
                }
                loadState.refresh is LoadState.Error -> {
                    //You can use modifier to show error message for all items
                    val e = tvShowsListItemsData.loadState.refresh as LoadState.Error
                    item {
                        ErrorItemWithRetry(
                            message = stringResource(id = R.string.error_generic),
                            modifier = if (tvShowsListItemsData.itemCount == 0) Modifier.fillParentMaxSize() else Modifier,
                            onClickRetry = { retry() }
                        )
                    }
                }
                loadState.append is LoadState.Error -> {
                    //You can use modifier to show error message for more items
                    val e = tvShowsListItemsData.loadState.append as LoadState.Error
                    item {
                        ErrorItemWithRetry(
                            message = stringResource(id = R.string.error_generic),
                            onClickRetry = { retry() }
                        )
                    }
                }
            }
        }
    }
}

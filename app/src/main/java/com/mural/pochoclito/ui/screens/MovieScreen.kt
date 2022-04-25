package com.mural.pochoclito.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.paging.LoadState
import androidx.paging.PagingData
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.items
import com.mural.domain.Movie
import com.mural.domain.Watchable
import com.mural.pochoclito.R
import com.mural.pochoclito.ui.elements.*
import com.mural.pochoclito.viewmodel.MovieViewModel
import kotlinx.coroutines.flow.Flow

@Composable
fun MovieScreen(movieViewModel: MovieViewModel, navController: NavController) {
    val textState = remember { mutableStateOf(TextFieldValue("")) }

    Column() {
        SearchView(textState)
        Box() {
            MovieList(viewModel = movieViewModel, navController = navController)
            SearchList(navController = navController, state = textState, itemType = Watchable.MOVIE)
        }
    }
}

@Composable
fun MovieList(
    modifier: Modifier = Modifier,
    viewModel: MovieViewModel,
    navController: NavController
) {
    MovieInfoList(modifier, movieDataList = viewModel.movies, navController)
}


@Composable
fun MovieInfoList(
    modifier: Modifier,
    movieDataList: Flow<PagingData<Movie>>,
    navController: NavController
) {
    val moviesListItems: LazyPagingItems<Movie> = movieDataList.collectAsLazyPagingItems()

    LazyColumn() {
        items(moviesListItems) { movie ->
            movie?.let {
                ItemRow(
                    title = "${movie.title}",
                    subtitle = "${stringResource(id = R.string.popularity)}: ${movie.popularity?.toInt()} <> ${stringResource(id = R.string.top_rated)}: ${movie.voteAverage}",
                    imagePath = movie.backdropPath,
                    navController = navController,
                    itemId = movie.movieId,
                    itemType = Watchable.MOVIE
                )
            }
        }
        moviesListItems.apply {
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
                    val e = moviesListItems.loadState.refresh as LoadState.Error
                    item {
                        ErrorItemWithRetry(
                            message = stringResource(id = R.string.error_generic),
                            modifier = if (moviesListItems.itemCount == 0) Modifier.fillParentMaxSize() else Modifier,
                            onClickRetry = { retry() }
                        )
                    }
                }
                loadState.append is LoadState.Error -> {
                    //You can use modifier to show error message for more items
                    val e = moviesListItems.loadState.append as LoadState.Error
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

    if (moviesListItems.itemCount == 0) {
        Box(Modifier.heightIn(min = 40.dp)) {
            Text(
                text = stringResource(id = R.string.no_items),
                fontSize = 14.sp,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp)
            )
        }
    }
}
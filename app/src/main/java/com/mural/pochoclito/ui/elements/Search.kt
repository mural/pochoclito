package com.mural.pochoclito.ui.elements

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.items
import com.mural.domain.Movie
import com.mural.domain.TvShow
import com.mural.domain.Watchable
import com.mural.pochoclito.ui.PochoclitoScreen
import com.mural.pochoclito.viewmodel.MovieViewModel
import com.mural.pochoclito.viewmodel.TvShowsViewModel


@Composable
fun SearchView(state: MutableState<TextFieldValue>) {
    TextField(
        value = state.value,
        onValueChange = { value ->
            state.value = value
        },
        modifier = Modifier
            .fillMaxWidth(),
        textStyle = TextStyle(color = Color.White, fontSize = 18.sp),
        leadingIcon = {
            Icon(
                Icons.Default.Search,
                contentDescription = "",
                modifier = Modifier
                    .padding(15.dp)
                    .size(24.dp)
            )
        },
        trailingIcon = {
            if (state.value != TextFieldValue("")) {
                IconButton(
                    onClick = {
                        state.value =
                            TextFieldValue("")
                        // Remove text from TextField when you press the 'X' icon
                    }
                ) {
                    Icon(
                        Icons.Default.Close,
                        contentDescription = "",
                        modifier = Modifier
                            .padding(15.dp)
                            .size(24.dp)
                    )
                }
            }
        },
        singleLine = true,
        shape = RectangleShape,
        colors = TextFieldDefaults.textFieldColors(
            textColor = Color.White,
            cursorColor = Color.White,
            leadingIconColor = Color.White,
            trailingIconColor = Color.White,
            backgroundColor = Color.DarkGray,
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
            disabledIndicatorColor = Color.Transparent
        )
    )
}

@Preview(showBackground = true)
@Composable
fun SearchViewPreview() {
    val textState = remember { mutableStateOf(TextFieldValue("")) }
    SearchView(textState)
}

@Composable
fun SearchListItem(text: String, onItemClick: (String) -> Unit) {
    Row(
        modifier = Modifier
            .clickable(onClick = { onItemClick(text) })
            .background(Color.Black)
            .height(57.dp)
            .fillMaxWidth()
            .padding(PaddingValues(8.dp, 16.dp))
    ) {
        Text(text = text, fontSize = 18.sp, color = Color.White)
    }
}

@Preview(showBackground = true)
@Composable
fun SearchListItemPreview() {
    SearchListItem(text = "Batman (2001)", onItemClick = { })
}

@Composable
fun SearchList(
    navController: NavController,
    state: MutableState<TextFieldValue>,
    itemType: Watchable = Watchable.MOVIE,
) {

    val movieViewModel: MovieViewModel = hiltViewModel()
    val tvShowsViewModel: TvShowsViewModel = hiltViewModel()
    var moviesListItems: LazyPagingItems<Movie>? = null
    var tvListItems: LazyPagingItems<TvShow>? = null
    if (Watchable.MOVIE == itemType) {
        moviesListItems =
            movieViewModel.searchMovieByName(state.value.text).collectAsLazyPagingItems()
    } else {
        tvListItems =
            tvShowsViewModel.searchTvShowByName(state.value.text).collectAsLazyPagingItems()
    }

    LazyColumn(modifier = Modifier.fillMaxWidth()) {
        moviesListItems?.let {
            items(it) { filteredItem ->
                SearchListItem(
                    text = "${filteredItem?.title} (${filteredItem?.releaseDate})",
                    onItemClick = {
                        navController.navigate("${PochoclitoScreen.Details.name}/${filteredItem?.movieId}/${itemType.ordinal}")
                    }
                )
            }
        }
        tvListItems?.let {
            items(it) { filteredItem ->
                SearchListItem(
                    text = "${filteredItem?.name} (${filteredItem?.firstAirDate})",
                    onItemClick = {
                        navController.navigate("${PochoclitoScreen.Details.name}/${filteredItem?.tvId}/${itemType.ordinal}")
                    }
                )
            }
        }
    }
}


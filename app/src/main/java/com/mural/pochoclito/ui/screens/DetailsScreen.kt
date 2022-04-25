package com.mural.pochoclito.ui.screens

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.google.accompanist.coil.rememberCoilPainter
import com.mural.data.repository.NetworkResult
import com.mural.domain.Movie
import com.mural.domain.TvShow
import com.mural.domain.Watchable
import com.mural.pochoclito.IMAGE_BASE_URL
import com.mural.pochoclito.R
import com.mural.pochoclito.ui.elements.FadingTopBar
import com.mural.pochoclito.viewmodel.MovieViewModel
import com.mural.pochoclito.viewmodel.TvShowsViewModel
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.YouTubePlayerCallback
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView
import kotlin.reflect.KFunction0

const val ND_STRING = "n/d"

@OptIn(ExperimentalComposeUiApi::class)
@SuppressLint("CoroutineCreationDuringComposition")
@Composable
fun DetailsScreen(
    watchable: Watchable,
    itemId: Long, navController: NavController
) {
    val movieViewModel: MovieViewModel = hiltViewModel()
    val tvShowsViewModel: TvShowsViewModel = hiltViewModel()
    val keyboardController = LocalSoftwareKeyboardController.current
    keyboardController?.hide()

    val state by
    if (Watchable.MOVIE == watchable) movieViewModel.response.observeAsState(initial = NetworkResult.Loading())
    else tvShowsViewModel.response.observeAsState(
        initial = NetworkResult.Loading()
    )
    val navigationUp = navController::navigateUp

    LaunchedEffect(Unit) {
        if (Watchable.MOVIE == watchable) {
            movieViewModel.getMovieDetails(itemId)
        } else {
            tvShowsViewModel.getTvShowDetails(itemId)
        }
    }
    when (state) {
        is NetworkResult.Success -> {
            setDetailItem(watchable, state, navigationUp)
        }
        is NetworkResult.Cached -> {
            Log.d("MainFragment", "NetworkResult.Cached")
            setDetailItem(watchable, state, navigationUp)
        }

        is NetworkResult.Error -> {
            Log.d("MainFragment", "NetworkResult.Error")
            setDetailItem(watchable, state, navigationUp)
        }

        is NetworkResult.Loading -> {
            Log.d("MainFragment", "NetworkResult.Loading")
            Text(
                text = stringResource(id = R.string.loading_wait),
                fontSize = 14.sp,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 22.dp)
            )
        }
    }
}

@Composable
private fun setDetailItem(
    watchable: Watchable,
    state: NetworkResult<out Any>,
    navigationUp: KFunction0<Boolean>
) {
    if (Watchable.MOVIE == watchable) {
        DetailItem(
            watchable = Watchable.MOVIE,
            movie = state.data as Movie,
            navigateUp = navigationUp
        )
    } else {
        DetailItem(
            watchable = Watchable.TV,
            tv = state.data as TvShow,
            navigateUp = navigationUp
        )
    }
}

@Composable
fun DetailItem(
    watchable: Watchable,
    movie: Movie? = null,
    tv: TvShow? = null,
    navigateUp: () -> Boolean
) {
    val scrollState = rememberScrollState()
    val itemLoaded: Any? = if (Watchable.MOVIE == watchable) movie else tv
    val title = if (Watchable.MOVIE == watchable) movie?.title else tv?.name
    val overview = if (Watchable.MOVIE == watchable) movie?.overview else tv?.overview
    val image = if (Watchable.MOVIE == watchable) movie?.backdropPath else tv?.backdropPath
    val date = if (Watchable.MOVIE == watchable) movie?.releaseDate else tv?.firstAirDate
    val videos = if (Watchable.MOVIE == watchable) movie?.videos else tv?.videos

    itemLoaded?.let {
        Column(
            Modifier
                .padding(bottom = 24.dp)
                .verticalScroll(state = scrollState)
        ) {
            Box() {
                image?.let {
                    if (title?.isNotBlank() == true) {
                        val imageLoaded = rememberCoilPainter(
                            request = "${IMAGE_BASE_URL}${image}",
                            fadeIn = true
                        )
                        Image(
                            painter = imageLoaded,
                            contentDescription = null,
                            modifier = Modifier
                                .width(480.dp)
                                .heightIn(min = 100.dp, max = 300.dp)
                                .clip(shape = RoundedCornerShape(0.dp))
                                .padding(horizontal = 0.dp, vertical = 2.dp),
                            contentScale = ContentScale.Crop
                        )
                    } else {
                        Text(text = stringResource(id = R.string.no_image))
                    }
                } ?: Text(text = stringResource(id = R.string.no_image))
            }

            Text(
                text = stringResource(id = R.string.date_title),
                fontSize = 20.sp,
                textAlign = if (Watchable.MOVIE == watchable) TextAlign.Start else TextAlign.End,
                color = Color.White,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 16.dp, end = 16.dp, top = 20.dp, bottom = 6.dp)
            )

            Text(
                text = date ?: ND_STRING,
                fontSize = 16.sp,
                textAlign = if (Watchable.MOVIE == watchable) TextAlign.Start else TextAlign.End,
                color = Color.White,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 26.dp, end = 16.dp, top = 6.dp, bottom = 12.dp)
            )

            if (Watchable.MOVIE == watchable) {
                Text(
                    text = stringResource(id = R.string.budget_title),
                    fontSize = 20.sp,
                    textAlign = TextAlign.Start,
                    color = Color.White,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 16.dp, end = 16.dp, top = 12.dp, bottom = 6.dp)
                )

                Text(
                    text = if (movie?.budget == 0L) ND_STRING else "${movie?.budget ?: ND_STRING}",
                    fontSize = 16.sp,
                    textAlign = if (Watchable.MOVIE == watchable) TextAlign.Start else TextAlign.End,
                    color = Color.White,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 26.dp, end = 16.dp, top = 6.dp, bottom = 12.dp)
                )
            } else {
                Text(
                    text = stringResource(id = R.string.is_current_production_title),
                    fontSize = 20.sp,
                    textAlign = if (Watchable.MOVIE == watchable) TextAlign.Start else TextAlign.End,
                    color = Color.White,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 16.dp, end = 16.dp, top = 12.dp, bottom = 6.dp)
                )

                Text(
                    text = if (tv?.inProduction == true) stringResource(
                        id = R.string.yes
                    ) else stringResource(id = R.string.no),
                    fontSize = 16.sp,
                    textAlign = if (Watchable.MOVIE == watchable) TextAlign.Start else TextAlign.End,
                    color = Color.White,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 26.dp, end = 16.dp, top = 6.dp, bottom = 12.dp)
                )
            }

            Text(
                text = stringResource(id = R.string.detail_title),
                fontSize = 20.sp,
                textAlign = if (Watchable.MOVIE == watchable) TextAlign.Start else TextAlign.End,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 16.dp, end = 16.dp, top = 12.dp, bottom = 6.dp)
            )

            Text(
                text = overview ?: ND_STRING,
                fontSize = 16.sp,
                textAlign = if (Watchable.MOVIE == watchable) TextAlign.Start else TextAlign.End,
                color = Color.White,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 26.dp, end = 16.dp, top = 6.dp, bottom = 12.dp)
            )

            Text(
                text = stringResource(id = R.string.video_title),
                fontSize = 20.sp,
                textAlign = if (Watchable.MOVIE == watchable) TextAlign.Start else TextAlign.End,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 16.dp, end = 16.dp, top = 12.dp, bottom = 6.dp)
            )

            val youtubeSite = "YouTube"
            Log.d("DetailScreen", "check Videos ${videos?.size}")
            videos?.firstOrNull()?.site?.let { site ->
                if (youtubeSite == site) {
                    val videoKey = videos.firstOrNull()?.key
                    Log.d("DetailScreen", "Load Android View $videoKey")
                    videoKey?.let { key ->
                        var youTubePlayerView: YouTubePlayerView? = null
                        AndroidView(
                            modifier = Modifier,
                            factory = {
                                Log.d("DetailScreen", "init youTubePlayerView")
                                youTubePlayerView = YouTubePlayerView(it)
                                youTubePlayerView?.getYouTubePlayerWhenReady(object :
                                    YouTubePlayerCallback {
                                    override fun onYouTubePlayer(youTubePlayer: YouTubePlayer) {
                                        youTubePlayer.cueVideo(
                                            key, 0f
                                        )
                                    }
                                })
                                youTubePlayerView!!
                            },
                            update = { },
                        )
                        DisposableEffect(key1 = youTubePlayerView) {
                            onDispose { youTubePlayerView?.release() }
                        }
                    }
                } else {
                    Text(
                        text = stringResource(id = R.string.not_supported_format),
                        fontSize = 14.sp,
                        textAlign = if (Watchable.MOVIE == watchable) TextAlign.Start else TextAlign.End,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 32.dp)
                    )
                }
            } ?: run {
                Text(
                    text = stringResource(id = R.string.video_error_generic),
                    fontSize = 14.sp,
                    textAlign = if (Watchable.MOVIE == watchable) TextAlign.Start else TextAlign.End,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 32.dp)
                )
            }
            if (videos?.isEmpty() == true) {
                Box(Modifier.heightIn(min = 40.dp)) {
                    Text(
                        text = stringResource(id = R.string.no_videos),
                        fontSize = 14.sp,
                        textAlign = if (Watchable.MOVIE == watchable) TextAlign.Start else TextAlign.End,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 12.dp)
                    )
                }
            }
        }
    } ?: run {
        Text(
            text = stringResource(id = R.string.error_generic),
            fontSize = 14.sp,
            textAlign = if (Watchable.MOVIE == watchable) TextAlign.Start else TextAlign.End,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp)
        )
    }

    FadingTopBar(
        title = title ?: "",
        modifier = Modifier,
        scrollState = scrollState,
        navigateUp = navigateUp
    )
}

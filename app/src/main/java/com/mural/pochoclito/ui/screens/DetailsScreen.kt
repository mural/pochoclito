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
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
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

@SuppressLint("CoroutineCreationDuringComposition")
@Composable
fun DetailsScreen(
    watchable: Watchable,
    movieViewModel: MovieViewModel,
    tvShowsViewModel: TvShowsViewModel,
    itemId: Long, navController: NavController
) {
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
    movie?.let {
        Column(
            Modifier
                .padding(bottom = 24.dp)
                .verticalScroll(state = scrollState)
        ) {
            Box() {
                movie.backdropPath?.let {
                    if (it.isNotBlank()) {
                        val image = rememberCoilPainter(
                            request = "${IMAGE_BASE_URL}${movie.backdropPath}",
                            fadeIn = true
                        )
                        Image(
                            painter = image,
                            contentDescription = null,
                            modifier = Modifier
                                .width(480.dp)
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
                text = "Fecha estreno: 2032-13-03",
                fontSize = 16.sp,
                textAlign = if (Watchable.MOVIE == watchable) TextAlign.Start else TextAlign.End,
                color = Color.White,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp)
            )

            Text(
                text = "Presupuesto: USD ${movie.budget}",
                fontSize = 16.sp,
                textAlign = if (Watchable.MOVIE == watchable) TextAlign.Start else TextAlign.End,
                color = Color.White,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp)
            )

            Text(
                text = "Varios datos de la peli muchos muchos",
                fontSize = 14.sp,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 22.dp)
            )

            Text(
                text = stringResource(id = R.string.video_title),
                fontSize = 14.sp,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp)
            )

            val youtubeSite = "YouTube"
            movie.videos.firstOrNull()?.site?.let { site ->
                if (youtubeSite == site) {
                    Log.d("DetailScreen", "Load Android View")
                    var youTubePlayerView: YouTubePlayerView? = null
                    // Se probo YouTube library oficial, pero tiene varios bugs y no va bien con Compose ademas...
                    AndroidView(
                        modifier = Modifier,
                        factory = { it ->
                            Log.d("DetailScreen", "init youTubePlayerView")
                            youTubePlayerView = YouTubePlayerView(it)
                            youTubePlayerView?.getYouTubePlayerWhenReady(object :
                                YouTubePlayerCallback {
                                override fun onYouTubePlayer(youTubePlayer: YouTubePlayer) {
                                    Log.d(
                                        "DetailScreen",
                                        "Videos: ${movie.videos.size} ${movie.videos.firstOrNull()?.key ?: "error"}"
                                    )
                                    movie.videos.firstOrNull()?.key?.let { key ->
                                        youTubePlayer.cueVideo(
                                            key, 0f
                                        )
                                    }
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
            } ?: run {
                Text(
                    text = "Formato no soportado",
                    fontSize = 14.sp,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 32.dp)
                )
            }

            Text(
                text = "Otros datos de la peli muchos muchos",
                fontSize = 14.sp,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 32.dp)
            )

            Text(
                text = "Poner algo mas para mostrar el scroll y la animaction de la barra top",
                fontSize = 14.sp,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 22.dp)
            )
        }
    } ?: run {
        Text(
            text = "Error: movieData vacia",
            fontSize = 14.sp,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp)
        )
    }
    FadingTopBar(
        title = "${movie?.title}",
        modifier = Modifier,
        scrollState = scrollState,
        navigateUp = navigateUp
    )
}

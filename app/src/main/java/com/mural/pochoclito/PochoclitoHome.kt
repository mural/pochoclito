package com.mural.pochoclito

import androidx.compose.animation.AnimatedContentScope
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.navArgument
import com.google.accompanist.navigation.animation.AnimatedNavHost
import com.google.accompanist.navigation.animation.composable
import com.google.accompanist.navigation.animation.rememberAnimatedNavController
import com.mural.domain.Watchable
import com.mural.pochoclito.ui.PochoclitoScreen
import com.mural.pochoclito.ui.elements.PochoclitoTabRow
import com.mural.pochoclito.ui.screens.DetailsScreen
import com.mural.pochoclito.ui.screens.MovieScreen
import com.mural.pochoclito.ui.screens.TvShowsScreen
import com.mural.pochoclito.ui.theme.PochoclitoTheme
import com.mural.pochoclito.viewmodel.MovieViewModel
import com.mural.pochoclito.viewmodel.TvShowsViewModel

const val IMAGE_BASE_URL = "https://image.tmdb.org/t/p/w500/"

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun PochoclitoHome(movieViewModel: MovieViewModel, tvShowsViewModel: TvShowsViewModel) {
    PochoclitoTheme {
        val tabsScreens = listOf(PochoclitoScreen.Movies, PochoclitoScreen.TvShows)
        val navController = rememberAnimatedNavController()
        val backstackEntry = navController.currentBackStackEntryAsState()
        val currentScreen = PochoclitoScreen.fromRoute(
            backstackEntry.value?.destination?.route
        )
        Scaffold(
            topBar = {
                PochoclitoTabRow(
                    tabsScreens = tabsScreens,
                    onTabSelected = { screen ->
                        navController.navigate(screen.name)
                    },
                    currentScreen = currentScreen,
                )
            }
        ) { innerPadding ->
            innerPadding.hashCode()
            PochoclitoNavHost(navController, Modifier, movieViewModel, tvShowsViewModel)
        }
    }
}

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun PochoclitoNavHost(
    navController: NavHostController,
    modifier: Modifier = Modifier,
    movieViewModel: MovieViewModel,
    tvShowsViewModel: TvShowsViewModel
) {
    val detailsName = PochoclitoScreen.Details.name
    AnimatedNavHost(
        navController = navController,
        startDestination = PochoclitoScreen.Movies.name,
    ) {
        composable(
            PochoclitoScreen.Movies.name,
            enterTransition = null,
            exitTransition = null,
            //popEnter and popExit defaults to enter and exit when not set
//            popEnterTransition = {},
//            popExitTransition = {}
        ) {
            MovieScreen(movieViewModel = movieViewModel, navController = navController)
        }
        composable(
            PochoclitoScreen.TvShows.name,
            enterTransition = null,
            exitTransition = null
        ) {
            TvShowsScreen(
                tvShowsViewModel = tvShowsViewModel,
                navController = navController
            )
        }
        composable(
            "$detailsName/{key_item_id}/{key_item_type}",
            arguments = listOf(
                navArgument("key_item_id") {
                    type = NavType.LongType
                },
                navArgument("key_item_type") {
                    type = NavType.IntType
                }
            ),
            enterTransition = {
                when (initialState.destination.route) {
                    PochoclitoScreen.Movies.name ->
                        slideIntoContainer(
                            AnimatedContentScope.SlideDirection.Right,
                            animationSpec = tween(700)
                        )
                    else -> null
                }
            },
            exitTransition = {
                when (targetState.destination.route) {
                    PochoclitoScreen.Movies.name ->
                        slideOutOfContainer(
                            AnimatedContentScope.SlideDirection.Left,
                            animationSpec = spring(Spring.DampingRatioMediumBouncy)
                        )
                    else -> null
                }
            },
        ) { entry -> // Look up "name" in NavBackStackEntry's arguments
            val itemType = entry.arguments?.getInt("key_item_type") ?: -1
            val itemId = entry.arguments?.getLong("key_item_id")
            itemId?.let {
                DetailsScreen(
                    Watchable.values()[itemType],
                    it,
                    navController = navController
                )
            } ?: run {
                Text(
                    text = stringResource(id = R.string.error_generic),
                    fontSize = 14.sp,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 22.dp)
                )
            }
        }
    }
}
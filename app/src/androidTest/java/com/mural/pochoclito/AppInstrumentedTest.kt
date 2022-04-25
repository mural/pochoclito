package com.mural.pochoclito

import androidx.activity.viewModels
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import com.mural.pochoclito.viewmodel.MovieViewModel
import com.mural.pochoclito.viewmodel.TvShowsViewModel
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Instrumented test, which will execute on an Android device.
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
@RunWith(AndroidJUnit4::class)
@LargeTest
class AppInstrumentedTest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    @Before
    fun setup() {
        composeTestRule.setContent {
            PochoclitoHome(
                composeTestRule.activity.viewModels<MovieViewModel>().value,
                composeTestRule.activity.viewModels<TvShowsViewModel>().value
            )
        }
    }

    @Test
    fun check_tabs_movie_list_and_enter_movie_details() {
        composeTestRule.waitForIdle()

        val button = composeTestRule.onNode(hasTestTag("RowSelected"), useUnmergedTree = true)
        button.assertIsDisplayed()

        composeTestRule.onNodeWithText(
            text = "The Batman",
            substring = true,
            ignoreCase = true,
            useUnmergedTree = true
        ).assertIsDisplayed().performClick()

        composeTestRule.onNodeWithText(
            text = "Video promocional",
            substring = true,
            ignoreCase = true,
            useUnmergedTree = true
        ).assertIsDisplayed()
    }

    @Test
    fun check_tabs_click_tv_list_find_tv_show() {
        composeTestRule.waitForIdle()

        val button = composeTestRule.onNode(hasTestTag("RowUnSelected"), useUnmergedTree = true)
        button.assertIsDisplayed()
        button.performClick()

        composeTestRule.onNodeWithText(
            "Halo",
            substring = true,
            ignoreCase = true,
            useUnmergedTree = true
        ).assertIsDisplayed()
    }

}
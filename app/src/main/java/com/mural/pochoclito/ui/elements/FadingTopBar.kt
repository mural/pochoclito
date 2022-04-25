package com.mural.pochoclito.ui.elements

import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.Divider
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.math.max

@Composable
fun FadingTopBar(
    title: String,
    modifier: Modifier,
    scrollState: ScrollState,
    navigateUp: () -> Boolean
) {
    Column() {
        Box(modifier = modifier) {
            Box(
                modifier = Modifier
                    .alpha(
                        max(
                            0.3f,
                            scrollState.value / scrollState.maxValue.toFloat()
                        )
                    )
                    .fillMaxWidth()
                    .height(56.dp)
                    .background(color = Color.Black)
            )
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.height(56.dp)) {
                Icon(
                    modifier = Modifier
                        .padding(16.dp)
                        .clickable { navigateUp() },
                    imageVector = Icons.Default.ArrowBack,
                    tint = Color.White,
                    contentDescription = "Atras",
                )
                Text(
                    text = title,
                    fontSize = 22.sp,
                    textAlign = TextAlign.Start,
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 4.dp)
                )
            }
        }
        Divider(
            color = Color.White,
            modifier = Modifier
                .alpha(
                    max(
                        0.3f,
                        scrollState.value / scrollState.maxValue.toFloat()
                    )
                )
                .fillMaxWidth(),
            thickness = 0.5.dp
        )
    }
}
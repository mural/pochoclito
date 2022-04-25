package com.mural.pochoclito.ui.elements

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.google.accompanist.coil.rememberCoilPainter
import com.mural.domain.Watchable
import com.mural.pochoclito.IMAGE_BASE_URL
import com.mural.pochoclito.R
import com.mural.pochoclito.ui.PochoclitoScreen

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun ItemRow(
    title: String,
    subtitle: String,
    imagePath: String?,
    itemId: Long = 0,
    itemType: Watchable = Watchable.MOVIE,
    navController: NavController? = null,
    rowAlignment: RowAlignment = RowAlignment.START
) {

    Column(
        Modifier
            .padding(bottom = 24.dp)
            .clickable { navController?.navigate("${PochoclitoScreen.Details.name}/$itemId/${itemType.ordinal}") }) {
        Box() {
            imagePath?.also {
                if (it.isNotBlank()) {
                    val image = rememberCoilPainter(
                        request = "${IMAGE_BASE_URL}$imagePath",
                        fadeIn = true
                    )
                    Image(
                        painter = image,
                        contentDescription = null,
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(min = 100.dp, max = 300.dp)
                            .clip(shape = RoundedCornerShape(0.dp))
                            .padding(horizontal = 0.dp, vertical = 2.dp),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Text(text = stringResource(id = R.string.no_image))
                }
            } ?: run { Box(modifier = Modifier.height(200.dp)) { Text(text = stringResource(id = R.string.no_image)) } }

            Text(
                text = subtitle,
                fontSize = 16.sp,
                textAlign = if (RowAlignment.START == rowAlignment) TextAlign.Start else TextAlign.End,
                color = Color.White,
                modifier = Modifier
                    .background(color = Color.Black.copy(alpha = 0.5f))
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 2.dp)
                    .align(Alignment.BottomEnd)
            )
        }
        Text(
            text = title,
            fontSize = 22.sp,
            textAlign = if (RowAlignment.START == rowAlignment) TextAlign.Start else TextAlign.End,
            fontWeight = FontWeight.Bold,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 2.dp)
        )
    }
}

@Preview(showBackground = true)
@Composable
fun ItemRowPreview() {
    ItemRow(title = "The Batman", subtitle = "Otros datos importantes", imagePath = "some.img")
}

@Preview(showBackground = true)
@Composable
fun ItemRowPreviewNullImage() {
    ItemRow(title = "The Batman", subtitle = "Otros datos importantes", imagePath = null)
}
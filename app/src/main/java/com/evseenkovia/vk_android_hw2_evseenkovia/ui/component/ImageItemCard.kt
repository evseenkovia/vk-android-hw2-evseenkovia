package com.evseenkovia.vk_android_hw2_evseenkovia.ui.component

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Card
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.evseenkovia.vk_android_hw2_evseenkovia.R
import com.evseenkovia.vk_android_hw2_evseenkovia.ui.ImageUi


@Composable
fun ImageItemCard(item: ImageUi, onClick: () -> Unit, modifier: Modifier) {
    val loader = rememberGifImageLoader()

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = { onClick })
    ) {
        AsyncImage(
            model = ImageRequest.Builder(LocalContext.current)
                .data(item.url)
                .crossfade(true)
                .build(),
            imageLoader = loader,
            contentDescription = stringResource(R.string.image_content_desciption),
            modifier = Modifier.fillMaxWidth(),
            contentScale = ContentScale.Crop
        )
    }
}
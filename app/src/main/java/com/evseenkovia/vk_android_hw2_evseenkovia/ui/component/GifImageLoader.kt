package com.evseenkovia.vk_android_hw2_evseenkovia.ui.component

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import coil.ImageLoader
import coil.decode.GifDecoder

@Composable
fun rememberGifImageLoader(): ImageLoader {
    val context = LocalContext.current // получаем контекст внутри @Composable
    return remember {
        ImageLoader.Builder(context)
            .components {
                add(GifDecoder.Factory()) // GIF декодер работает на всех версиях
            }
            .build()
    }
}
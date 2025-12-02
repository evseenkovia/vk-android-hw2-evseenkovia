package com.evseenkovia.vk_android_hw2_evseenkovia.ui

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridItemSpan
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.foundation.lazy.staggeredgrid.itemsIndexed
import androidx.compose.foundation.lazy.staggeredgrid.rememberLazyStaggeredGridState
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.core.app.NotificationCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.evseenkovia.vk_android_hw2_evseenkovia.config.AppConfig
import com.evseenkovia.vk_android_hw2_evseenkovia.R
import com.evseenkovia.vk_android_hw2_evseenkovia.data.local.DatabaseProvider
import com.evseenkovia.vk_android_hw2_evseenkovia.data.repository.ImageRepositoryImpl
import com.evseenkovia.vk_android_hw2_evseenkovia.domain.ImageListViewModel
import com.evseenkovia.vk_android_hw2_evseenkovia.ui.component.ImageItemCard

class ImageListFragment : Fragment() {

    private val viewModel: ImageListViewModel by viewModels {
        object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                val db = DatabaseProvider.get(requireContext())
                val repo = ImageRepositoryImpl(
                    dao = db.imageDao(),
                    apiKey = AppConfig.GIPHY_API_KEY
                )
                @Suppress("UNCHECKED_CAST")
                return ImageListViewModel(repo) as T
            }
        }
    }

    // Регистрация запроса разрешения
    private val requestNotificationPermission =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { granted -> }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Создаём канал уведомлений
        createNotificationChannel(requireContext())
        // Запрашиваем разрешение на уведомления
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            requestNotificationPermission.launch(android.Manifest.permission.POST_NOTIFICATIONS)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            setContent {
                val state by viewModel.state.collectAsState()
                val context = LocalContext.current

                ImageListScreen(
                    state = state,
                    onRetry = { viewModel.retry() },
                    onLoadNext = { viewModel.loadMore() },
                    onImageClick = { index, item ->
                        viewModel.onImageClick(item) // твоя логика ViewModel
                        context.showImageNotification(index + 1, item.id)
                    }
                )
            }
        }
    }

    private fun createNotificationChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channelId = "image_channel"
            val name = "Image Notifications"
            val description = "Notifications about clicked images"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(channelId, name, importance).apply {
                this.description = description
            }
            val notificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }
}

fun Context.showImageNotification(index: Int, imageId: String) {
    val channelId = "image_channel"
    val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    val notification = NotificationCompat.Builder(this, channelId)
        .setContentTitle("Вы выбрали изображение")
        .setContentText("Порядковый номер: $index, ID: $imageId")
        .setSmallIcon(R.drawable.ic_launcher_foreground)
        .setAutoCancel(true)
        .setPriority(NotificationCompat.PRIORITY_DEFAULT)
        .build()

    // уникальный ID, чтобы уведомления не заменяли друг друга
    notificationManager.notify(System.currentTimeMillis().toInt(), notification)
}


@Composable
fun ImageListScreen(
    state: ImageListUiState,
    onRetry: () -> Unit = {},
    onLoadNext: () -> Unit = {},
    onImageClick: (Int, ImageUi) -> Unit = {_, _ ->}
){
    when (state){
        ImageListUiState.Loading -> LoadingState()
        is ImageListUiState.Content -> ContentState(
            items = state.items,
            onLoadNext = onLoadNext,
            onImageClick = onImageClick,
            isPaginationLoading = state.isPaginationLoading
        )
        is ImageListUiState.Error -> ErrorState(onRetry)
    }
}

@Composable
fun ContentState(
    items: List<ImageUi>,
    onLoadNext: () -> Unit,
    onImageClick: (Int, ImageUi) -> Unit,
    isPaginationLoading: Boolean = false
) {
    val listState = rememberLazyStaggeredGridState()

    LazyVerticalStaggeredGrid(
        columns = StaggeredGridCells.Fixed(2),
        state = listState,
        verticalItemSpacing = 8.dp,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier
            .fillMaxSize()
            .padding(8.dp)
    ) {
        val uniqueItems = items.distinctBy { it.uuid }
        // Используем itemsIndexed, чтобы получить индекс
        itemsIndexed(uniqueItems, key = { _, item -> item.uuid }) { index, item ->
            ImageItemCard(
                item = item,
                onClick = { onImageClick(index, item) },
                modifier = Modifier.height(item.height)
            )
        }

        if (isPaginationLoading) {
            item(span = StaggeredGridItemSpan.FullLine) {
                PaginationLoader()
            }
        }
    }

    LaunchedEffect(listState, items, isPaginationLoading) {
        snapshotFlow {
            val lastVisible = listState.layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: 0
            lastVisible >= items.lastIndex
        }.collect { endReached ->
            if (endReached && !isPaginationLoading) {
                onLoadNext()
            }
        }
    }
}

@Composable
fun LoadingState() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator()
    }
}

@Composable
fun ErrorState(onRetry: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ){
        Text(text = stringResource(R.string.error_title))
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = onRetry){
            Text(stringResource(R.string.retry))
        }
    }
}

@Composable
fun PaginationLoader(){
    Box(
        modifier = Modifier.fillMaxWidth().padding(16.dp),
        contentAlignment = Alignment.Center
    ){
        CircularProgressIndicator()
    }
}

package com.evseenkovia.vk_android_hw2_evseenkovia.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.evseenkovia.vk_android_hw2_evseenkovia.R
import com.evseenkovia.vk_android_hw2_evseenkovia.data.GiphyRepository
import com.evseenkovia.vk_android_hw2_evseenkovia.domain.ImageListViewModel
import com.evseenkovia.vk_android_hw2_evseenkovia.ui.component.ImageItemCard

class ImageListFragment : Fragment() {

    // создаём ViewModel с кастомной фабрикой, чтобы передать GiphyRepository с API ключом
    private val viewModel: ImageListViewModel by viewModels {
        object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                val repo = GiphyRepository(apiKey = "K2SBnYfIczRgCmbs1Cm7rvsHNgSiTlSS")
                @Suppress("UNCHECKED_CAST")
                return ImageListViewModel(repo) as T
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,   //объект для превращения compose-элементов в реальный View
        container: ViewGroup?,      //родительский ViewGroup
        savedInstanceState: Bundle? //объект для восстановления состояния фрагмента
    ): View {
        return ComposeView(requireContext()).apply {    //контейнер для использования Compose
            //здесь описываем UI
            setContent {
                //делегат для автоматического создания ViewModel
                //для ее сохранения при пересоздании фрагмента
                val state by viewModel.state.collectAsState()

                ImageListScreen(
                    state = state,
                    onRetry = { viewModel.retry() },
                    onLoadNext = { viewModel.loadMore() },
                    onImageClick = { item -> viewModel.onImageClick(item) }
                )
            }
        }
    }
}

@Composable
fun ImageListScreen(
    state: ImageListUiState = ImageListUiState.Loading,
    onRetry: () -> Unit = {},
    onLoadNext: () -> Unit = {},
    onImageClick: (ImageUi) -> Unit = {}
){
    when (state){
        ImageListUiState.Loading -> LoadingState()
        is ImageListUiState.Content -> ContentState(
            items = state.items,
            onLoadNext = onLoadNext,
            onImageClick = onImageClick,
        )
        is ImageListUiState.Error -> ErrorState(onRetry)
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
fun ContentState(
    items: List<ImageUi>,
    onLoadNext: () -> Unit,
    onImageClick: (ImageUi) -> Unit
) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        modifier = Modifier
            .fillMaxSize()
            .padding(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(items.size) { index ->
            val item = items[index]

            ImageItemCard(
                item = item,
                onClick = { onImageClick(item) }
            )

            if (index == items.lastIndex){
                onLoadNext()
            }
        }
    }
}

@Composable
fun LoadingState() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(48.dp),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator()
    }

}

@Composable
fun PaginationLoader(){
    Box(
        modifier = Modifier
        .fillMaxWidth()
        .padding(16.dp),
        contentAlignment = Alignment.Center
    ){
        CircularProgressIndicator()
    }
}

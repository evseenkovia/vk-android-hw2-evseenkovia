package com.evseenkovia.vk_android_hw2_evseenkovia.ui.component

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.evseenkovia.vk_android_hw2_evseenkovia.R
import com.evseenkovia.vk_android_hw2_evseenkovia.ui.ImageListFragment
import com.evseenkovia.vk_android_hw2_evseenkovia.ui.ImageUi
import java.util.UUID
import kotlin.uuid.Uuid

private const val ARG_PARAM1 = "param1"

class SingleImageFragment : Fragment() {

    private var param1: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return ComposeView(requireContext()).apply {
            setContent {
                ShowImage(param1, {
                    parentFragmentManager.beginTransaction()
                        .replace(R.id.fragment_container,
                            ImageListFragment.newInstance())
                        .commit()
                })
            }
        }
    }

    companion object {
        @JvmStatic
        fun newInstance(imageUrl: String) =
            SingleImageFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, imageUrl)
                }
            }
    }

    @Composable
    fun ShowImage(url: String?, onBack: () -> Unit) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (url == null) {
                Text("Нет изображения")
            } else {
                ImageItemCard(
                    item = ImageUi("0", url, (150..300).random().dp, "0"),
                    onClick = { },
                    modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentHeight()
                        .padding(bottom = 16.dp),
                    contentScale = ContentScale.Fit
                )
            }

            Button(
                onClick = onBack,
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                Text(text = "Назад")
            }
        }
    }
}
@file:Suppress("UNCHECKED_CAST")

package com.learn.facepoto.ui

import android.Manifest
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.asFlow
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.paging.PagingData
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import coil.compose.AsyncImage
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.face.Face
import com.learn.facepoto.data.Constants.LOADING
import com.learn.facepoto.data.Constants.PERMISSION_MESSAGE
import com.learn.facepoto.data.Constants.SOMETHING_WENT_WRONG_MESSAGE
import com.learn.facepoto.data.GalleryImage
import com.learn.facepoto.data.ImageState
import com.learn.facepoto.viewmodel.ImageViewModel
import kotlinx.coroutines.flow.Flow

@Composable
fun MainView() {
    val viewModel: ImageViewModel = viewModel()
    var viewState = viewModel.imageStateData.asFlow().collectAsState(ImageState.Loading)
    var permissionGranted = remember { mutableStateOf(true) }

    when (val state = viewState.value) {
        ImageState.Loading -> {
            ShowLoader()
            val launcher = rememberLauncherForActivityResult(
                contract = ActivityResultContracts.RequestPermission(),
                onResult = { isGranted ->
                    permissionGranted.value = isGranted
                    viewModel.fetchImages()
                }
            )

            LaunchedEffect(key1 = Unit) {
                launcher.launch(Manifest.permission.READ_MEDIA_IMAGES)
            }
        }

        is ImageState.Success -> {
            ImageGridView(
                state.pagedImages,
                permissionGranted
            )
        }

        is ImageState.Error -> {
            ShowMessage(SOMETHING_WENT_WRONG_MESSAGE)
        }

    }
}

@Composable
fun ImageGridView(
    images: Flow<PagingData<GalleryImage>>,
    permissionGranted: MutableState<Boolean>
) {
    val lazyPagingItems = images.collectAsLazyPagingItems()

    if (permissionGranted.value) {
        if (lazyPagingItems.itemCount == 0) {
            ShowMessage(LOADING)
        } else {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                ImageGrid(lazyPagingItems)

            }
        }
    } else {
        if (permissionGranted.value.not()) {
            ShowMessage(PERMISSION_MESSAGE)
        }
    }


}

@Composable
fun ShowMessage(message: String) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text(message)
    }
}

@Composable
fun ImageGrid(lazyPagingItems: LazyPagingItems<GalleryImage>) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(3),
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(lazyPagingItems.itemCount) { index ->
            val image = lazyPagingItems[index]
            if (image != null) {
                ImageItem(image)
            } else {
                ShowLoader()
            }
        }
    }
}

@Composable
fun ImageItem(image: GalleryImage) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(1f)
            .clip(RoundedCornerShape(8.dp))
    ) {
        AsyncImage(
            model = image.uri,
            contentDescription = image.name,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop //
        )

        image.uri?.let {
            DrawBounds(imageUri = it)
        }
    }
}

@Composable
fun DrawBounds(imageUri: Uri) {
    var bounds: List<Face> by remember { mutableStateOf(emptyList()) }
    val viewModel: ImageViewModel = viewModel()

    viewModel.faceDetector.process(InputImage.fromFilePath(LocalContext.current, imageUri))
        .addOnSuccessListener {
            bounds = it
        }

    androidx.compose.foundation.Canvas(modifier = Modifier.fillMaxWidth()) {
        bounds.forEach { face: Face ->
            val bound = face.boundingBox
            drawRect(
                color = Color.Red,
                topLeft = Offset((bound.left).div(2).toFloat(), (bound.top).div(2).toFloat()),
                size = Size(bound.width().div(2).toFloat(), bound.height().div(2).toFloat()),
                style = Stroke(width = 5F)
            )
        }
    }
}

@Composable
fun ShowLoader() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator()
    }
}

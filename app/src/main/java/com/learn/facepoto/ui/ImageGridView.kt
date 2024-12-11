package com.learn.facepoto.ui

import android.Manifest
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import androidx.paging.PagingData
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import coil.compose.AsyncImage
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.face.Face
import com.google.mlkit.vision.face.FaceDetector
import com.learn.facepoto.data.GalleryImage
import kotlinx.coroutines.flow.Flow

@Composable
fun ImageGridView(images: Flow<PagingData<GalleryImage>>, faceDetector: FaceDetector) {
    val lazyPagingItems = images.collectAsLazyPagingItems()

    var permissionGranted = remember { mutableStateOf(false) }


    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { isGranted ->
            permissionGranted.value = isGranted
        }
    )

    LaunchedEffect(key1 = Unit) {
        launcher.launch(Manifest.permission.READ_MEDIA_IMAGES)

    }

    Box(
        Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        if (permissionGranted.value) {
            ImageGrid(lazyPagingItems, faceDetector)
        } else if (lazyPagingItems.itemCount == 0) {
            Text("No images found")
        } else {
            showPermissionView()
        }
    }


}

@Composable
fun ImageGrid(lazyPagingItems: LazyPagingItems<GalleryImage>, faceDetector: FaceDetector) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(3),
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(lazyPagingItems.itemCount) { index ->
            val image = lazyPagingItems[index]
            if (image != null) {
                ImageItem(image, faceDetector)
            } else {
                // Display placeholder while loading
                Box(modifier = Modifier.fillMaxSize()) {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }
            }
        }

        // Handle loading state for infinite scrolling
        when (lazyPagingItems.loadState.append) {
            is androidx.paging.LoadState.Loading -> {
                item(span = { GridItemSpan(3) }) {
                    // Display loading indicator at the bottom
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                    }
                }
            }

            is androidx.paging.LoadState.Error -> {
            }

            else -> {} // Do nothing when loaded successfully
        }
    }
}

@Composable
fun ImageItem(image: GalleryImage, faceDetector: FaceDetector) {
    // Assuming 'image' is a data class or object containing image information
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

        var bounds: List<Face> by remember { mutableStateOf(emptyList()) }
        image.uri?.let {
            faceDetector.process(InputImage.fromFilePath(LocalContext.current, image.uri))
                .addOnSuccessListener {
                    bounds = it
                }
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
}

@Composable
fun showPermissionView() {
    Box(
        modifier = Modifier.fillMaxWidth(),
        contentAlignment = Alignment.Center
    ) {
        Text("Please provide permission to access photos and detect faces")
    }
}
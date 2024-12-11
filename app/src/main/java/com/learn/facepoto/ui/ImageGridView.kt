package com.learn.facepoto.ui

import android.Manifest
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.paging.PagingData
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import coil.compose.AsyncImage
import com.learn.facepoto.data.GalleryImage
import kotlinx.coroutines.flow.Flow

@Composable
fun ImageGridView(images: Flow<PagingData<GalleryImage>>) {
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
            ImageGrid(lazyPagingItems)
        } else if (lazyPagingItems.itemCount == 0) {
            Text("No images found")
        } else {
            showPermissionView()
        }
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
                /*Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Text("No images found")
                }*/
            }

            else -> {} // Do nothing when loaded successfully
        }
    }
}

@Composable
fun ImageItem(image: GalleryImage) {
    // Assuming 'image' is a data class or object containing image information
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(1f) // Maintain aspect ratio for square images
            .clip(RoundedCornerShape(8.dp)) // Optional: Add rounded corners
    ) {
        AsyncImage(
            model = image.uri, // Replace with your image URL or resource
            contentDescription = image.name, // Replace with image description
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop // Adjust content scale as needed
        )
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
package com.learn.facepoto.data

import androidx.paging.Pager
import androidx.paging.PagingData
import com.google.mlkit.vision.face.Face
import kotlinx.coroutines.flow.Flow

sealed class ImageState {
    data object Loading : ImageState()
    data class Success(val pagedImages: Flow<PagingData<GalleryImage>>) : ImageState()
    data class Error(val message: String) : ImageState()
    data class Success2(val galleryImage: List<GalleryImage>) : ImageState()

}
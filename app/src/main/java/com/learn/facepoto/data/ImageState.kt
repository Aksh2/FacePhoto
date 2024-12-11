package com.learn.facepoto.data

import com.google.mlkit.vision.face.Face

sealed class ImageState {
    object Loading : ImageState()
    data class Success(val faces: List<GalleryImage>) : ImageState()
    data class Error(val message: String) : ImageState()

}
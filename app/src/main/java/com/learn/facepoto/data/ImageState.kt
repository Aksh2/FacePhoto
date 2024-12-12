package com.learn.facepoto.data

import androidx.paging.PagingData
import kotlinx.coroutines.flow.Flow

sealed class ImageState {
    // Loading state
    data object Loading : ImageState()
    // On Successful load, returns the Paging Data to be used be the pager in the view
    data class Success(val pagedImages: Flow<PagingData<GalleryImage>>) : ImageState()
    // Error state to display the error message
    data class Error(val message: String) : ImageState()

}
package com.learn.facepoto.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import com.google.mlkit.vision.face.FaceDetector
import com.learn.facepoto.data.GalleryImage
import com.learn.facepoto.data.ImageState
import com.learn.facepoto.paging.GalleryImagePagingSource
import com.learn.facepoto.repository.ImageRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * [ImageViewModel] holds the core logic to fetch images
 */

@HiltViewModel
class ImageViewModel @Inject constructor(
    private val imageRepository: ImageRepository,
    val faceDetector: FaceDetector
) : ViewModel() {

    // MutableLiveData to be accessed only inside Viewmodel.
    val _imageStateData = MutableLiveData<ImageState>()
    val imageStateData: LiveData<ImageState> = _imageStateData



    /*
     * Fetches images from the image repository and returns [Pager]
     * to be used in [ImageGridView]
     */
    fun fetchPagedImages(): Pager<Int, GalleryImage> {
        return Pager(
            config = PagingConfig(pageSize = 10),
            pagingSourceFactory = { GalleryImagePagingSource(imageRepository) }
        )
    }

    fun fetchImages() = viewModelScope.launch {
        imageRepository.fetchImages().collect {
            _imageStateData.postValue(ImageState.Success2(it))

        }
    }

}
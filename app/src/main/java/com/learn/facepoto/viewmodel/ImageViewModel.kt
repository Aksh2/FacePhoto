package com.learn.facepoto.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import com.google.mlkit.vision.face.FaceDetector
import com.learn.facepoto.data.ImageState
import com.learn.facepoto.repository.ImageRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
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


    fun fetchImages() {
        viewModelScope.launch(Dispatchers.IO) {
            _imageStateData.postValue(
                ImageState.Success(
                    imageRepository.fetchPagedGalleryImages().cachedIn(viewModelScope)
                )
            )
        }
    }
}

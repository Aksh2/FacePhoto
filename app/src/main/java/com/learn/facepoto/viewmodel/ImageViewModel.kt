package com.learn.facepoto.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.face.Face
import com.google.mlkit.vision.face.FaceDetector
import com.learn.facepoto.data.GalleryImage
import com.learn.facepoto.data.ImageState
import com.learn.facepoto.paging.GalleryImagePagingSource
import com.learn.facepoto.repository.ImageRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * [ImageViewModel] holds the core logic
 */

@HiltViewModel
class ImageViewModel @Inject constructor(
    private val imageRepository: ImageRepository,
    val faceDetector: FaceDetector
) : ViewModel() {


    // MutableLiveData to be accessed only inside Viewmodel.
    private val _humanFaceLiveData = MutableLiveData<List<GalleryImage>>()


    /**
     * Detects human faces in the [InputImage] and returns a list of [Face] objects.
     * Returns error message in case of error.
     */
    /*fun detectFaces() {
        val humanImages = mutableListOf<GalleryImage>()
        viewModelScope.launch(Dispatchers.IO) {
            imageRepository.fetchGalleryImages().forEach { galleryImage ->
                faceDetector.process(InputImage.fromFilePath())
                    .addOnSuccessListener { faces ->
                        Log.d("Akshay", "success ${faces}")
                        humanImages.add(galleryImage)
                    }.addOnFailureListener { e ->
                    }.addOnCompleteListener { faces ->
                        Log.d("Akshay", "complete ${humanImages}")
                        _humanFaceLiveData.postValue(humanImages)
                    }
            }

        }
    }*/

    fun fetchPagedImages(): Pager<Int, GalleryImage> {
        return Pager(
            config = PagingConfig(pageSize = 10),
            pagingSourceFactory = { GalleryImagePagingSource(imageRepository) }
        )
    }

}
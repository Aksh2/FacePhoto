package com.learn.facepoto.repository

import androidx.paging.PagingData
import com.learn.facepoto.data.GalleryImage
import kotlinx.coroutines.flow.Flow

interface ImageRepository {
     fun fetchGalleryImages(): List<GalleryImage>
     fun fetchPagedGalleryImages(): Flow<PagingData<GalleryImage>>
}
package com.learn.facepoto.repository

import com.learn.facepoto.data.GalleryImage
import kotlinx.coroutines.flow.Flow

interface ImageRepository {
     fun fetchGalleryImages(): List<GalleryImage>
     fun fetchImages(): Flow<List<GalleryImage>>
}
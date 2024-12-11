package com.learn.facepoto.repository

import com.learn.facepoto.data.GalleryImage

interface ImageRepository {
    fun fetchGalleryImages(): List<GalleryImage>
}
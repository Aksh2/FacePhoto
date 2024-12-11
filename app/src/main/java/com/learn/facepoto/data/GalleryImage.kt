package com.learn.facepoto.data

import android.net.Uri
import com.google.mlkit.vision.face.Face

 data class GalleryImage(
    val id: Long? = null,
    val name: String? = null,
    val uri: Uri? = null,
    val faceBounds: Face? = null
)
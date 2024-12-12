package com.learn.facepoto.repository

import android.content.ContentUris
import android.content.Context
import android.os.Build
import android.provider.MediaStore
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.learn.facepoto.data.GalleryImage
import com.learn.facepoto.paging.GalleryImagePagingSource
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * [ImageRepositoryImpl] repository for fetching images from the content provider and also sending
 * the data in paged form
 */
class ImageRepositoryImpl @Inject constructor(
    private val context: Context,
) : ImageRepository {
    val uniquePath = HashSet<String>()
    override fun fetchGalleryImages(): List<GalleryImage> {
        val images = mutableListOf<GalleryImage>()

        val collection = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        } else {
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        }

        val projection = arrayOf(
            MediaStore.Images.Media._ID,
            MediaStore.Images.Media.DISPLAY_NAME,
            MediaStore.Images.Media.DATE_ADDED,
        )

        val sortOrder = "${MediaStore.Images.Media.DATE_ADDED} DESC"


        context.contentResolver.query(
            collection,
            projection,
            null,
            null,
            sortOrder
        )?.use { cursor ->
            val idColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID)
            val nameColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DISPLAY_NAME)

            while (cursor.moveToNext()) {
                val id = cursor.getLong(idColumn)
                val name = cursor.getString(nameColumn)

                val contentUri = ContentUris.withAppendedId(
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                    id
                )
                if (uniquePath.add(name)) {
                    images.add(GalleryImage(id, name, contentUri))
                }

            }

        }
        return images
    }

    override fun fetchPagedGalleryImages(): Flow<PagingData<GalleryImage>> {
        return Pager(
            config = PagingConfig(pageSize = 10),
            pagingSourceFactory = { GalleryImagePagingSource(this) }
        ).flow
    }
}

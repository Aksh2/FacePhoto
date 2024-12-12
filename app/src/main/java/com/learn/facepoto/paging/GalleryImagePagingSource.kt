package com.learn.facepoto.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.learn.facepoto.data.GalleryImage
import com.learn.facepoto.repository.ImageRepository

class GalleryImagePagingSource(
    private val imageRepository: ImageRepository
) :
    PagingSource<Int, GalleryImage>() {
    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, GalleryImage> {
        return try {
            val page = params.key ?: 0 // Start with page 0
            val images = imageRepository.fetchGalleryImages()//images
            LoadResult.Page(
                data = images,
                prevKey = if (page == 0) null else page - 1,
                nextKey = if (images.isEmpty()) null else page + 1
            )
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, GalleryImage>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            state.closestPageToPosition(anchorPosition)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(anchorPosition)?.nextKey?.minus(1)
        }
    }

}
package com.learn.facepoto.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.learn.facepoto.data.GalleryImage

class GalleryImagePagingSource(
    private val humanImagesList: List<GalleryImage>
) :
    PagingSource<Int, GalleryImage>() {
    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, GalleryImage> {
        return try {
            val page = params.key ?: 0 // Start with page 0

            LoadResult.Page(
                data = humanImagesList,
                prevKey = if (page == 0) null else page - 1,
                nextKey = if (humanImagesList.isEmpty()) null else page + 1
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
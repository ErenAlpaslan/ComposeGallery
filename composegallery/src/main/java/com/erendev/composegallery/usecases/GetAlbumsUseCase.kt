package com.erendev.composegallery.usecases

import android.annotation.SuppressLint
import android.content.ContentResolver
import android.provider.MediaStore
import com.erendev.composegallery.base.BaseUseCase
import com.erendev.composegallery.base.GalleryResult
import com.erendev.composegallery.common.GalleryDefaults
import com.erendev.composegallery.data.model.AlbumItem
import com.erendev.composegallery.utils.coroutine.GalleryDispatchers
import com.erendev.composegallery.utils.extensions.doWhile
import com.erendev.composegallery.utils.extensions.getCursorUri
import kotlinx.coroutines.flow.*

class GetAlbumsUseCase: BaseUseCase<ArrayList<AlbumItem>, GetAlbumsUseCase.Params>() {

    data class Params(
        val contentResolver: ContentResolver
    )

    @SuppressLint("Range")
    override suspend fun execute(params: Params?) = flow {
        params?.contentResolver?.query(
            getCursorUri(),
            arrayOf(GalleryDefaults.DISPLAY_NAME_COLUMN, MediaStore.Images.ImageColumns.BUCKET_ID),
            null,
            null,
            GalleryDefaults.ORDER_BY
        ).let { albumCursor ->
            val list = arrayListOf<AlbumItem>()
            try {
                list.add(AlbumItem("All", true, "0"))
                if (albumCursor == null) {
                    emit(GalleryResult.Success(data = list))
                }
                albumCursor?.doWhile {
                    val bucketId =
                        albumCursor.getLong(albumCursor.getColumnIndexOrThrow(MediaStore.Images.ImageColumns.BUCKET_ID))
                            .toString()
                    val name =
                        albumCursor.getString(albumCursor.getColumnIndex(GalleryDefaults.DISPLAY_NAME_COLUMN))
                            ?: bucketId
                    var albumItem = AlbumItem(name, false, bucketId)
                    if (!list.contains(albumItem)) {
                        list.add(albumItem)
                    }
                }
            } catch (e: Exception) {
                emit(GalleryResult.Error(e.message.toString()))
            } finally {
                if (albumCursor != null && !albumCursor.isClosed) {
                    albumCursor.close()
                }
            }

            emit(GalleryResult.Success(data = list))
        }
    }.flowOn(GalleryDispatchers.io)

}
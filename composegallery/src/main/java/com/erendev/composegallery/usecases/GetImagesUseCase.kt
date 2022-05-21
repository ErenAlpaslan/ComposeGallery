package com.erendev.composegallery.usecases

import android.annotation.SuppressLint
import android.content.ContentResolver
import android.content.ContentUris
import android.database.Cursor
import android.media.Image
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import com.erendev.composegallery.base.BaseUseCase
import com.erendev.composegallery.base.GalleryResult
import com.erendev.composegallery.common.GalleryDefaults
import com.erendev.composegallery.common.enum.ImageSource
import com.erendev.composegallery.data.model.AlbumItem
import com.erendev.composegallery.data.model.ImageItem
import com.erendev.composegallery.utils.coroutine.GalleryDispatchers
import com.erendev.composegallery.utils.extensions.getCursorUri
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn

class GetImagesUseCase : BaseUseCase<ArrayList<ImageItem>, GetImagesUseCase.Params>() {

    data class Params(
        val contentResolver: ContentResolver,
        val albumItem: AlbumItem?,
        val page: Int,
        val preSelectedImages: List<String>?,
        val supportedImages: String?
    )

    @SuppressLint("Range")
    override suspend fun execute(params: Params?) = flow {
        val offset = (params?.page ?: 1) * GalleryDefaults.PAGE_SIZE
        val images: ArrayList<ImageItem> = arrayListOf()
        var photoCursor: Cursor? = null
        try {
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.Q) {

                val bundle = Bundle().apply {
                    putInt(ContentResolver.QUERY_ARG_LIMIT, GalleryDefaults.PAGE_SIZE)
                    putInt(ContentResolver.QUERY_ARG_OFFSET, offset)
                    putString(
                        ContentResolver.QUERY_ARG_SQL_SORT_ORDER,
                        "${MediaStore.MediaColumns.DATE_MODIFIED} DESC"
                    )
                }

                photoCursor = params?.contentResolver?.query(
                    getCursorUri(),
                    arrayOf(
                        GalleryDefaults.ID_COLUMN,
                        GalleryDefaults.PATH_COLUMN
                    ),
                    bundle,
                    null
                )
            } else {
                if (params?.albumItem == null || params.albumItem.isAll) {
                    photoCursor = params?.contentResolver?.query(
                        getCursorUri(),
                        arrayOf(
                            GalleryDefaults.ID_COLUMN,
                            GalleryDefaults.PATH_COLUMN
                        ),
                        null,
                        null,
                        "${GalleryDefaults.ORDER_BY} LIMIT ${GalleryDefaults.PAGE_SIZE} OFFSET $offset"
                    )
                } else {
                    photoCursor = params.contentResolver.query(
                        getCursorUri(),
                        arrayOf(
                            GalleryDefaults.ID_COLUMN,
                            GalleryDefaults.PATH_COLUMN
                        ),
                        "${MediaStore.Images.ImageColumns.BUCKET_ID} =?",
                        arrayOf(params.albumItem.bucketId),
                        "${GalleryDefaults.ORDER_BY} LIMIT ${GalleryDefaults.PAGE_SIZE} OFFSET $offset"
                    )
                }
            }


            photoCursor?.isAfterLast ?: emit(GalleryResult.Success(data = images))

            photoCursor?.let {
                while (it.moveToNext()) {
                    val image =
                        it.getString((it.getColumnIndex(GalleryDefaults.PATH_COLUMN)))
                    val id =
                        it.getLong(it.getColumnIndexOrThrow(GalleryDefaults.ID_COLUMN))
                    val uri =
                        ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id)
                    if (params?.preSelectedImages == null) {
                        images.add(ImageItem(image, ImageSource.GALLERY, uri, false))
                    } else {
                        images.add(addSelectedImageToList(params.preSelectedImages, image, uri))
                    }
                }
            }

        }catch (e: Exception) {
            emit(GalleryResult.Error(e.message.toString()))
        } finally {
            if (photoCursor != null && !photoCursor.isClosed) {
                photoCursor.close()
            }
        }

        emit(GalleryResult.Success(data = images))
    }.flowOn(GalleryDispatchers.io)

    private fun addSelectedImageToList(
        preSelectedImages: List<String>,
        image: String,
        uri: Uri
    ): ImageItem {
        var isSelected = false

        if (preSelectedImages.contains(image)) {
            isSelected = true
        }

        return ImageItem(image, ImageSource.GALLERY, uri, isSelected)
    }
}
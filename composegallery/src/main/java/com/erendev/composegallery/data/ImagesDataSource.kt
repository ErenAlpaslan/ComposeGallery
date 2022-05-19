package com.erendev.composegallery.data

import android.annotation.SuppressLint
import android.content.ContentResolver
import android.content.ContentUris
import android.database.Cursor
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import com.erendev.composegallery.common.GalleryDefaults.CURSOR_URI
import com.erendev.composegallery.common.GalleryDefaults.DISPLAY_NAME_COLUMN
import com.erendev.composegallery.common.GalleryDefaults.ID_COLUMN
import com.erendev.composegallery.common.GalleryDefaults.NEW_CURSOR_URI
import com.erendev.composegallery.common.GalleryDefaults.ORDER_BY
import com.erendev.composegallery.common.GalleryDefaults.PAGE_SIZE
import com.erendev.composegallery.common.GalleryDefaults.PATH_COLUMN
import com.erendev.composegallery.common.enum.ImageSource
import com.erendev.composegallery.data.model.AlbumItem
import com.erendev.composegallery.data.model.ImageItem
import com.erendev.composegallery.utils.extensions.doWhile

internal class ImagesDataSource(private val contentResolver: ContentResolver){

    internal var selectedPosition = 0
    internal var isSelected = false

    fun getCursorUri(): Uri {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            NEW_CURSOR_URI
        } else {
            CURSOR_URI
        }
    }

    @SuppressLint("Range")
    fun loadAlbums(): ArrayList<AlbumItem> {
        val albumCursor = contentResolver.query(
            getCursorUri(),
            arrayOf(DISPLAY_NAME_COLUMN, MediaStore.Images.ImageColumns.BUCKET_ID),
            null,
            null,
            ORDER_BY
        )
        val list = arrayListOf<AlbumItem>()
        try {
            list.add(AlbumItem("All", true,"0"))
            if (albumCursor == null) {
                return list
            }
            albumCursor.doWhile {
                val bucketId = albumCursor.getString(albumCursor.getColumnIndex(MediaStore.Images.ImageColumns.BUCKET_ID))
                val name = albumCursor.getString(albumCursor.getColumnIndex(DISPLAY_NAME_COLUMN)) ?: bucketId
                var albumItem = AlbumItem(name, false, bucketId)
                if (!list.contains(albumItem)) {
                    list.add(albumItem)
                }
            }
        }catch (e: Exception) {

        }finally {
            if (albumCursor != null && !albumCursor.isClosed) {
                albumCursor.close()
            }
        }
        return list
    }

    @SuppressLint("Range")
    fun loadAlbumImages(
        albumItem: AlbumItem?,
        page: Int,
        supportedImages: String? = null,
        preSelectedImages: Array<out String?>? = null
    ): ArrayList<ImageItem> {
        val offset = page * PAGE_SIZE
        val list: ArrayList<ImageItem> = arrayListOf()
        var photoCursor: Cursor? = null
        try {
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.Q) {

                val bundle = Bundle().apply {
                    putInt(ContentResolver.QUERY_ARG_LIMIT, PAGE_SIZE)
                    putInt(ContentResolver.QUERY_ARG_OFFSET, offset)
                    putString(ContentResolver.QUERY_ARG_SQL_SORT_ORDER, "${MediaStore.MediaColumns.DATE_MODIFIED} DESC")
                }

                photoCursor = contentResolver.query(
                    getCursorUri(),
                    arrayOf(
                        ID_COLUMN,
                        PATH_COLUMN
                    ),
                    bundle,
                    null)
            }else {
                if (albumItem == null || albumItem.isAll) {
                    photoCursor = contentResolver.query(
                        getCursorUri(),
                        arrayOf(
                            ID_COLUMN,
                            PATH_COLUMN
                        ),
                        null,
                        null,
                        "$ORDER_BY LIMIT $PAGE_SIZE OFFSET $offset"
                    )
                } else {
                    photoCursor = contentResolver.query(
                        getCursorUri(),
                        arrayOf(
                            ID_COLUMN,
                            PATH_COLUMN
                        ),
                        "${MediaStore.Images.ImageColumns.BUCKET_ID} =?",
                        arrayOf(albumItem.bucketId),
                        "$ORDER_BY LIMIT $PAGE_SIZE OFFSET $offset")
                }
            }



            photoCursor?.isAfterLast ?: return list

            while(photoCursor.moveToNext()) {
                val image = photoCursor.getString((photoCursor.getColumnIndex(PATH_COLUMN)))
                val id = photoCursor.getLong(photoCursor.getColumnIndexOrThrow(ID_COLUMN))
                val uri = ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id)
                Log.d("ImageSourceControl", "image ==>$image // $id // $uri")
                if (supportedImages != null) {
                    val imageType = image.substring(image.lastIndexOf(".") + 1)
                    if (supportedImages.contains(imageType)) {
                        if (preSelectedImages == null) {
                            list.add(ImageItem(image, ImageSource.GALLERY, uri, false))
                        } else {
                            addSelectedImageToList(preSelectedImages, image, uri, list)
                        }
                    }
                } else {
                    if (preSelectedImages == null) {
                        list.add(ImageItem(image, ImageSource.GALLERY, uri, false))
                    } else {
                        addSelectedImageToList(preSelectedImages, image, uri, list)
                    }
                }
            }
        } finally {
            if (photoCursor != null && !photoCursor.isClosed) {
                photoCursor.close()
            }
        }
        Log.d("ImageSourceControl", "Images ==> $list")
        return list
    }

    private fun addSelectedImageToList(preSelectedImages: Array<out String?>, image: String, uri: Uri, list: ArrayList<ImageItem>) {
        if (preSelectedImages.contains(image)) {
            isSelected = true
        }

        if (isSelected) {
            selectedPosition += 1
        }
        list.add(ImageItem(image, ImageSource.GALLERY, uri, isSelected))
        isSelected = false
    }

}
package com.erendev.composegallery.common

import android.net.Uri
import android.provider.MediaStore
import androidx.compose.ui.unit.dp
import com.erendev.composegallery.ui.theme.Green

internal object GalleryDefaults {
    const val DEFAULT_MAX_SELECTION_LIMIT = 1
    const val DEFAULT_COLUMN_COUNT = 3
    val DEFAULT_GALLERY_ITEM_PADDING = 8.dp
    const val DEFAULT_TOOLBAR_ENABLED = true
    val DEFAULT_TOOLBAR_BACKGROUND_COLOR = Green

    val CURSOR_URI: Uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
    val NEW_CURSOR_URI: Uri = MediaStore.Images.Media.getContentUri(MediaStore.VOLUME_EXTERNAL)
    const val ORDER_BY = MediaStore.Images.Media.DATE_TAKEN + " DESC"
    const val DISPLAY_NAME_COLUMN = MediaStore.Images.Media.BUCKET_DISPLAY_NAME
    const val ID_COLUMN = MediaStore.Images.Media._ID
    const val PATH_COLUMN = MediaStore.Images.Media.DATA
    const val PAGE_SIZE = 20

    const val IMAGES = "images"
    const val ALBUMS = "albums"
    const val PHOTO_PATH = "photo_path"
    const val ALBUM_POS = "album_pos"
    const val PAGE = "page"
    const val SELECTED_ALBUM = "selected_album"
    const val SELECTED_IMAGES = "selected_images"
    const val CURRENT_SELECTION = "curren_selection"
    const val LIMIT_NUMBER = "limit"
    const val DISABLE_CAMERA = "limit"
    const val ALL_TYPES = "all"
}
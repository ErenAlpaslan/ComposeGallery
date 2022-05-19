package com.erendev.composegallery.data.model

import android.graphics.Bitmap
import android.net.Uri
import com.erendev.composegallery.common.enum.ImageSource

data class ImageItem(
    var imagePath: String,
    var source: ImageSource,
    var uri: Uri?,
    var selected: Boolean
)

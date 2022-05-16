package com.erendev.composegallery.data.model

import android.net.Uri
import com.erendev.composegallery.common.enum.ImageSource

data class ImageItem(
    var imagePath: String,
    var source: ImageSource,
    var uri: Uri,
    var selected: Int,
    var selectedPosition: Int? = null
) {

    companion object {
        const val SELECTED = 1
        const val NOT_SELECTED = 0
    }

    fun isCustomPositionActivated(): Boolean {
        return selected == SELECTED
    }

}

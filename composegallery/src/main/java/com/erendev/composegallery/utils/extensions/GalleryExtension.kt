package com.erendev.composegallery.utils.extensions

import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.os.Environment.DIRECTORY_PICTURES
import com.erendev.composegallery.common.GalleryDefaults.ALL_TYPES
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

internal fun Cursor.doWhile(action: () -> Unit) {
    this.use {
        if (this.moveToFirst()) {
            do {
                action()
            } while (this.moveToNext())
        }
    }
}

internal fun getSupportedImagesExt(supportedExt: List<String>): String {
    var result = ""
    return if (supportedExt.isEmpty()) {
        result = ALL_TYPES
        result
    } else {
        supportedExt.forEach {
            result += "$it/"
        }
        result
    }
}

@Throws(IOException::class)
internal fun createTempImageFile(context : Context): File {
    val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.ENGLISH).format(Date())
    val imageFileName = "JPEG_" + timeStamp + "_"
    val storageDir = context.getExternalFilesDir(DIRECTORY_PICTURES)
    val image = File.createTempFile(
        imageFileName,
        ".jpg",
        storageDir
    )
    return image
}

internal fun String.getUriFromImagePath(): Uri? {
    return Uri.fromFile(File(this))
}
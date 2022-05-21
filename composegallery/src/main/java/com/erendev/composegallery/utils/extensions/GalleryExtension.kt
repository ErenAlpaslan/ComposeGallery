package com.erendev.composegallery.utils.extensions

import android.content.ContentUris
import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.os.Environment.DIRECTORY_PICTURES
import android.provider.MediaStore
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.erendev.composegallery.common.GalleryDefaults
import com.erendev.composegallery.common.GalleryDefaults.ALL_TYPES
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.OutputStream
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

fun saveToGallery(context: Context, bitmap: Bitmap, albumName: String): Uri? {
    val filename = "${System.currentTimeMillis()}.png"
    val write: (OutputStream) -> Boolean = {
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, it)
    }

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
        val contentValues = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, filename)
            put(MediaStore.MediaColumns.MIME_TYPE, "image/png")
            put(MediaStore.MediaColumns.RELATIVE_PATH, "${Environment.DIRECTORY_DCIM}/$albumName")
        }

        context.contentResolver.let {
            it.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)?.let { uri ->
                it.openOutputStream(uri)?.let(write)
                return uri
            }
        }
    } else {
        val imagesDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).toString() + File.separator + albumName
        val file = File(imagesDir)
        if (!file.exists()) {
            file.mkdir()
        }
        val image = File(imagesDir, filename)
        write(FileOutputStream(image))
    }

    return null
}

fun getCursorUri(): Uri {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
        GalleryDefaults.NEW_CURSOR_URI
    } else {
        GalleryDefaults.CURSOR_URI
    }
}

inline fun <VM : ViewModel> viewModelFactory(crossinline f: () -> VM) =
    object : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(aClass: Class<T>):T = f() as T
    }
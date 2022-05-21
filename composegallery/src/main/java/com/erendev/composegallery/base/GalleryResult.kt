package com.erendev.composegallery.base

sealed class GalleryResult <T> {
    data class Success <T> (val data: T?): GalleryResult<T>()
    data class Error <T> (val message: String) : GalleryResult<T>()
}

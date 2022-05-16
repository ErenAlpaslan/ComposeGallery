package com.erendev.composegallery.common.enum

sealed class GalleryType() {
    class Standard(val columnCount: Int): GalleryType()
    object Quilted: GalleryType()
    class Masonry(val columnCount: Int): GalleryType()
}
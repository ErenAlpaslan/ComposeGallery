package com.erendev.composegallery.utils.coroutine

import kotlinx.coroutines.CoroutineDispatcher

abstract class IGalleryDispatchers {

    abstract val main: CoroutineDispatcher

    abstract val io: CoroutineDispatcher

    abstract val default: CoroutineDispatcher
}
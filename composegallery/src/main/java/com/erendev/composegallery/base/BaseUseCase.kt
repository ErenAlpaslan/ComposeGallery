package com.erendev.composegallery.base

import kotlinx.coroutines.flow.Flow

abstract class BaseUseCase<T : Any, in Params> {
    abstract suspend fun execute(params: Params?): Flow<GalleryResult<T>>
}
package com.erendev.composegallery

import android.content.ContentResolver
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.erendev.composegallery.base.BaseViewModel
import com.erendev.composegallery.base.GalleryResult
import com.erendev.composegallery.common.GalleryDefaults.ALL_TYPES
import com.erendev.composegallery.data.ImagesDataSource
import com.erendev.composegallery.data.model.AlbumItem
import com.erendev.composegallery.data.model.ImageItem
import com.erendev.composegallery.usecases.GetAlbumsUseCase
import com.erendev.composegallery.usecases.GetImagesUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

internal class ComposeGalleryViewModel(
    private val getAlbumsUseCase: GetAlbumsUseCase,
    private val getImagesUseCase: GetImagesUseCase
) : BaseViewModel() {

    private lateinit var contentResolver: ContentResolver

    internal var mDoneEnabled = MutableLiveData<Boolean>()

    private val _albums = MutableLiveData<ArrayList<AlbumItem>>()
    internal val albums: LiveData<ArrayList<AlbumItem>> = _albums

    private val _filteredImages: MutableLiveData<List<ImageItem>> = MutableLiveData()
    val filteredImages: LiveData<List<ImageItem>> = _filteredImages

    private val images: MutableLiveData<List<ImageItem>> = MutableLiveData()

    private val _isDoneEnabled: MutableLiveData<Boolean> = MutableLiveData(false)
    val isDoneEnabled: LiveData<Boolean> = _isDoneEnabled

    internal var mPage = 0
    internal var mLoadedIndex = 0

    private var mSelectedAlbum: AlbumItem? = null
    private var mSelectedList = hashMapOf<String, ImageItem>()
    private var preSelectedImages: List<String>? = null

    internal fun init(contentResolver: ContentResolver) {
        this.contentResolver = contentResolver
        loadAlbums()
    }

    internal fun loadAlbums() {
        if (_albums.value.isNullOrEmpty()) {
            viewModelScope.launch {
                getAlbumsUseCase.execute(
                    GetAlbumsUseCase.Params(
                        contentResolver = contentResolver
                    )
                ).collect {
                    when (it) {
                        is GalleryResult.Error -> errorMessage.postValue(it.message)
                        is GalleryResult.Success -> {
                            _albums.postValue(it.data)
                            mSelectedAlbum = it.data?.get(0)
                            loadAllImages()
                        }
                    }
                }
            }
        }
    }

    private fun loadAllImages() {
        if (images.value.isNullOrEmpty()) {
            viewModelScope.launch {
                getImagesUseCase.execute(
                    GetImagesUseCase.Params(
                        contentResolver = contentResolver,
                        albumItem = null,
                        page = mPage,
                        preSelectedImages = preSelectedImages,
                        supportedImages = ALL_TYPES
                    )
                ).collect {
                    when (it) {
                        is GalleryResult.Error -> errorMessage.postValue(it.message)
                        is GalleryResult.Success -> {
                            images.postValue(it.data)
                            _filteredImages.postValue(it.data)
                        }
                    }
                }
            }
        }
    }

    fun loadMoreImages(index: Int) {
        if (mSelectedAlbum != null && index > mLoadedIndex) {
            mPage += 1
            viewModelScope.launch {
                getImagesUseCase.execute(
                    GetImagesUseCase.Params(
                        contentResolver = contentResolver,
                        albumItem = mSelectedAlbum,
                        page = mPage,
                        preSelectedImages = preSelectedImages,
                        supportedImages = ALL_TYPES
                    )
                ).collect {
                    when (it) {
                        is GalleryResult.Error -> errorMessage.postValue(it.message)
                        is GalleryResult.Success -> {
                            val result = it.data?.toList() ?: emptyList()
                            val mergedList = images.value?.plus(result)
                            mLoadedIndex = index
                            images.postValue(mergedList)
                            _filteredImages.postValue(mergedList)
                        }
                    }
                }
            }
        }
    }

    fun onAlbumChanged(album: AlbumItem) {
        mSelectedAlbum = album
        mPage = 0
        if (album.isAll) {
            _filteredImages.postValue(images.value)
        } else {
            viewModelScope.launch {
                getImagesUseCase.execute(
                    GetImagesUseCase.Params(
                        contentResolver = contentResolver,
                        albumItem = album,
                        page = mPage,
                        preSelectedImages = preSelectedImages,
                        supportedImages = ALL_TYPES
                    )
                ).collect {
                    when (it) {
                        is GalleryResult.Error -> errorMessage.postValue(it.message)
                        is GalleryResult.Success -> {
                            _filteredImages.postValue(
                                images.value?.filter { item ->
                                    item.imagePath.contains(album.name)
                                }
                            )
                        }
                    }
                }
            }
        }
    }

    internal fun addCameraItem(item: ImageItem) {
        viewModelScope.launch {
            mSelectedList[item.imagePath] = item
            val list: ArrayList<ImageItem> = arrayListOf()
            images.value?.let {
                list.addAll(it)
            }
            list.add(0, item)
            images.postValue(list)
            _filteredImages.postValue(list)
        }
    }
}
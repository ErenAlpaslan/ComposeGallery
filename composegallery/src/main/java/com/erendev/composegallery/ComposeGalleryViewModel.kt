package com.erendev.composegallery

import android.content.ContentResolver
import android.text.TextUtils
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.erendev.composegallery.common.GalleryDefaults.ALL_TYPES
import com.erendev.composegallery.data.ImagesDataSource
import com.erendev.composegallery.data.model.AlbumItem
import com.erendev.composegallery.data.model.ImageItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

internal class ComposeGalleryViewModel: ViewModel() {

    private lateinit var mImageDataSource: ImagesDataSource
    private lateinit var contentResolver: ContentResolver

    internal var mDoneEnabled = MutableLiveData<Boolean>()
    internal var mDirectCamera = MutableLiveData<Boolean>()
    internal var showOverLimit = MutableLiveData<Boolean>()
    internal var mNotifyPosition = MutableLiveData<Int>()
    internal var mNotifyInsert = MutableLiveData<Int>()

    private val _albums = MutableLiveData<ArrayList<AlbumItem>>()
    internal val albums: LiveData<ArrayList<AlbumItem>> = _albums

    private var _lastAddedImages = MutableLiveData<ArrayList<ImageItem>>()
    internal val lastAddedImages = _lastAddedImages

    internal var saveStateImages = arrayListOf<ImageItem>()
    internal var mCurrentPhotoPath: String? = null
    internal var mCurrentSelectedAlbum = 0
    internal var mPage = 0

    private var mSelectedAlbum: AlbumItem? = null
    private var mSelectedList = hashMapOf<String, ImageItem>()
    private var mCurrentSelection: Int = 0
    private var mLimit = 0
    private var isSingleSelectionEnabled = false
    private var mCameraCisabled: Boolean = true
    private var supportedImages: String = ALL_TYPES
    private var preSelectedImages: Array<out String?>? = null

    internal fun init(contentResolver: ContentResolver) {
        this.contentResolver = contentResolver
        this.mImageDataSource = ImagesDataSource(this.contentResolver)
    }

    private fun getCurrentSelection() = mCurrentSelection

    internal fun isOverLimit() = mCurrentSelection >= mLimit

    internal fun loadAlbums() {
        if (!_albums.value.isNullOrEmpty()) {
            return
        }
        viewModelScope.launch {
            val albums = getAlbums()
            _albums.value = albums
            loadImages()
        }
    }

    internal fun loadMoreImages() {
        loadImages(true)
    }

    private fun loadImages(isLoadMore: Boolean = false) {
        if (isLoadMore) {
            mPage += 1
        } else {
            mPage = 0
        }
        viewModelScope.launch() {
            val images = getImages()
            mCurrentSelection = mImageDataSource.selectedPosition
            addSelectedImages(images)
            if (mCurrentSelection > 0) {
                mDoneEnabled.postValue(true)
            }
            _lastAddedImages.value = images
        }
    }

    private suspend fun getImages() = withContext(Dispatchers.Default) {
        if (!TextUtils.equals(supportedImages, ALL_TYPES)) {
            mImageDataSource.loadAlbumImages(mSelectedAlbum, mPage, supportedImages, preSelectedImages)
        } else {
            mImageDataSource.loadAlbumImages(mSelectedAlbum, mPage, null, preSelectedImages)
        }
    }


    private suspend fun getAlbums() = withContext(Dispatchers.Default) {
        mImageDataSource.loadAlbums()
    }

    private fun addSelectedImages(images: ArrayList<ImageItem>) {
        if (preSelectedImages != null) {
            mSelectedList.clear()
            images.forEach {
                mSelectedList[it.imagePath] = it
            }
        }
    }

    fun onAlbumChanged(item: AlbumItem) {
        mSelectedAlbum = item
        mSelectedList.clear()
        mCurrentSelection = 0
        loadImages()
    }



}
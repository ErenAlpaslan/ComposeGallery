package com.erendev.composegallery.ui.views.image

import android.util.Log
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import com.erendev.composegallery.common.GalleryDefaults.DEFAULT_COLUMN_COUNT
import com.erendev.composegallery.common.GalleryDefaults.PAGE_SIZE
import com.erendev.composegallery.common.enum.ImageSource
import com.erendev.composegallery.data.model.ImageItem
import com.erendev.composegallery.ui.views.camera.CameraButton

@Composable
fun GalleryImageListStandard(
    list: List<ImageItem>?,
    updatedList: (List<ImageItem>) -> Unit,
    onImageCaptured: (ImageItem) -> Unit,
    onLoadMore: (Int) -> Unit,
) {
    val selectedItems = remember {
        mutableListOf<ImageItem>()
    }

    val state by remember {
        mutableStateOf(LazyGridState())
    }



    if (!list.isNullOrEmpty()) {
        LazyVerticalGrid(
            columns = GridCells.Fixed(DEFAULT_COLUMN_COUNT),
            state = state
        ) {
            item(0) {
                CameraButton { imageItem ->
                    if (imageItem !in selectedItems) {
                        selectedItems.add(imageItem)
                    }

                    onImageCaptured(imageItem)
                }
            }
            itemsIndexed(list) { index, imageItem ->
                Log.d("StateControl", "=> $index")
                Log.d("StateControl", "=> ${index + 1}")
                Log.d("StateControl", "=> $PAGE_SIZE")

                if ((index + 1) % PAGE_SIZE == 0) {
                    onLoadMore(index)
                }

                key((index + 1)) {
                    GalleryImageListItem(
                        item = imageItem,
                        onImageSelected = {
                            if (it !in selectedItems) {
                                selectedItems.add(it)
                            }

                            updatedList(selectedItems)

                        },

                        onImageRemoved = {
                            if (it in selectedItems) {
                                selectedItems.remove(it)
                            }

                            updatedList(selectedItems)
                        }
                    )
                }
            }
        }
    } else {
        CameraButton { imageItem ->
            if (imageItem !in selectedItems) {
                selectedItems.add(imageItem)
            }

            onImageCaptured(imageItem)
        }
    }
}
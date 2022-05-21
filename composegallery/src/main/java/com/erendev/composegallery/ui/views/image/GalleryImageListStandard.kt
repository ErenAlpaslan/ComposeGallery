package com.erendev.composegallery.ui.views.image

import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.runtime.Composable
import androidx.compose.runtime.key
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import com.erendev.composegallery.common.GalleryDefaults.DEFAULT_COLUMN_COUNT
import com.erendev.composegallery.common.enum.ImageSource
import com.erendev.composegallery.data.model.ImageItem
import com.erendev.composegallery.ui.views.camera.CameraButton

@Composable
fun GalleryImageListStandard(
    list: List<ImageItem>?,
    limit: Int,
    updatedList: (List<ImageItem>) -> Unit,
    onImageCaptured: (ImageItem) -> Unit
) {
    val selectedItems = remember {
        mutableListOf<ImageItem>()
    }

    if (!list.isNullOrEmpty()) {
        LazyVerticalGrid(
            columns = GridCells.Fixed(DEFAULT_COLUMN_COUNT)
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
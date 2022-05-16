package com.erendev.composegallery.ui.views.image

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.erendev.composegallery.R
import com.erendev.composegallery.common.enum.GalleryType
import com.erendev.composegallery.data.model.ImageItem

@Composable
fun GalleryImageListStandard(
    items: List<ImageItem>?,
    galleryType: GalleryType.Standard,
    limit: Int,
    onItemSelected: (ImageItem) -> Unit
) {
    val selectedItems = remember {
        mutableListOf<Map<Int, ImageItem>>()
    }

    if (!items.isNullOrEmpty()) {
        LazyVerticalGrid(
            columns = GridCells.Fixed(galleryType.columnCount)
        ) {
            items(1) {
                CameraButton()
            }
            itemsIndexed(items ?: emptyList()) { index, imageItem ->

                GalleryImageListItem(
                    item = imageItem
                ) {
                    onItemSelected(imageItem)
                }
            }
        }
    } else {
        CameraButton()
    }


}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CameraButton() {
    Card(
        modifier = Modifier.fillMaxSize()
            .defaultMinSize(150.dp)
            .padding(2.dp),
        onClick = {
            /* TODO:Open camera */
        },
        shape = MaterialTheme.shapes.medium
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_photo_camera_black_24dp),
                contentDescription = "Camera icon")
            Text(text = stringResource(id = R.string.gallery_take_picture_title))
        }
    }
}
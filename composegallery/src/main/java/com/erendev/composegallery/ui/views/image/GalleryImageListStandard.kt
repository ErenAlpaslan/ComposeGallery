package com.erendev.composegallery.ui.views.image

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.erendev.composegallery.R
import com.erendev.composegallery.common.GalleryDefaults.DEFAULT_COLUMN_COUNT
import com.erendev.composegallery.common.enum.GalleryType
import com.erendev.composegallery.data.model.ImageItem
import com.erendev.composegallery.ui.theme.Green
import com.erendev.composegallery.ui.theme.White
import com.skydoves.landscapist.glide.GlideImage

@Composable
fun GalleryImageListStandard(
    items: List<ImageItem>?,
    limit: Int,
    updatedList: (List<ImageItem>) -> Unit
) {
    val selectedItems = remember {
        mutableListOf<ImageItem>()
    }

    if (!items.isNullOrEmpty()) {
        LazyVerticalGrid(
            columns = GridCells.Fixed(DEFAULT_COLUMN_COUNT)
        ) {
            items(1) {
                CameraButton()
            }
            itemsIndexed(items ?: emptyList()) { index, imageItem ->

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
    } else {
        CameraButton()
    }


}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CameraButton() {
    Box(
        modifier = Modifier.padding(vertical = 3.dp, horizontal = 2.dp),
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .defaultMinSize(minHeight = 150.dp)
                .clip(RoundedCornerShape(10.dp))
                .clickable {

                },
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_photo_camera_black_24dp),
                contentDescription = "Camera icon"
            )
            Text(text = stringResource(id = R.string.gallery_take_picture_title))
        }
    }
}
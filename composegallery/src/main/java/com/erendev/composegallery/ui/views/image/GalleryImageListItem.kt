package com.erendev.composegallery.ui.views.image

import android.net.Uri
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import com.erendev.composegallery.data.model.ImageItem
import com.skydoves.landscapist.glide.GlideImage

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GalleryImageListItem(
    item: ImageItem,
    selected: Boolean = false,
    onImageSelected: (ImageItem) -> Unit
) {
    var imageUri by remember {
        mutableStateOf<Uri?>(
            item.uri
        )
    }
    Row(modifier = Modifier.padding(vertical = 3.dp, horizontal = 2.dp)) {
        GlideImage(
            imageModel = imageUri,
            contentScale = ContentScale.Fit,
            modifier = Modifier
                .fillMaxSize()
                .defaultMinSize(minHeight = 150.dp)
                .clip(RoundedCornerShape(10.dp))
                .clickable {
                    onImageSelected(item)
                },
        )
    }
}
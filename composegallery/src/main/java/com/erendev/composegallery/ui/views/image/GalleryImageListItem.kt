package com.erendev.composegallery.ui.views.image

import android.graphics.Bitmap
import android.net.Uri
import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import com.erendev.composegallery.data.model.ImageItem
import com.erendev.composegallery.ui.theme.Green
import com.erendev.composegallery.ui.theme.White
import com.skydoves.landscapist.glide.GlideImage

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GalleryImageListItem(
    item: ImageItem,
    onImageSelected: (ImageItem) -> Unit,
    onImageRemoved: (ImageItem) -> Unit
) {
    var isSelected by remember {
        mutableStateOf(false)
    }

    isSelected = item.selected

    val state = remember {
        MutableTransitionState(false).apply {
            targetState = true
        }
    }

    Box(modifier = Modifier.padding(vertical = 3.dp, horizontal = 2.dp)) {
        AnimatedVisibility(
            visibleState = state,
            enter = fadeIn(animationSpec = tween(1000)),
            exit = fadeOut(animationSpec = tween(1000))
        ) {
            GlideImage(
                imageModel = item.uri,
                contentScale = ContentScale.FillBounds,
                modifier = Modifier
                    .fillMaxSize()
                    .height(150.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .clickable {
                        isSelected = isSelected.not()
                        item.selected = isSelected
                        if (isSelected) onImageSelected(item) else onImageRemoved(item)
                    },
            )
        }

        if (isSelected) {
            Box(modifier = Modifier.padding(6.dp)) {
                Row(
                    modifier = Modifier
                        .clip(CircleShape)
                        .height(20.dp)
                        .width(20.dp)
                        .align(Alignment.Center)
                        .background(Green),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = "",
                        tint = White,
                        modifier = Modifier
                            .height(16.dp)
                            .width(16.dp)
                    )
                }
            }
        }
    }

}
package com.erendev.composegallery.ui.views.album

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.erendev.composegallery.R
import com.erendev.composegallery.data.model.AlbumItem
import com.erendev.composegallery.ui.theme.Gray
import com.erendev.composegallery.ui.views.album.AlbumDropDown

@Composable
fun AlbumSelection(
    items: List<AlbumItem>?,
    onAlbumSelected: (AlbumItem) -> Unit
) {
    Column(modifier = Modifier.wrapContentHeight()
        .padding(start = 8.dp, end = 8.dp)) {
        AlbumDropDown(
            items = items
        ) {
            onAlbumSelected(it)
        }
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = stringResource(id = R.string.gallery_change_album_title),
            color = Gray
        )
    }
}
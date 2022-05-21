package com.erendev.composegallery.ui.views.album

import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import com.erendev.composegallery.data.model.AlbumItem
import com.erendev.composegallery.ui.theme.Black

@Composable
fun AlbumDropDown(
    items: List<AlbumItem>?,
    onAlbumSelected: (AlbumItem) -> Unit
) {

    val expanded = remember {
        mutableStateOf(false)
    }
    var selectedIndex by remember {
        mutableStateOf(0)
    }

    Column {
        Row(modifier = Modifier.clickable {
            expanded.value = true
        }) {
            Text(text = items?.get(selectedIndex)?.name ?: "", color = Black)
            Icon(
                imageVector = Icons.Default.ArrowDropDown,
                contentDescription = "Drop down arrow",
                tint = Black
            )
        }
        DropdownMenu(
            expanded = expanded.value,
            onDismissRequest = {
                expanded.value = false
            }
        ) {
            items?.forEachIndexed { index, item ->
                DropdownMenuItem(
                    text = {
                        Text(text = item.name)
                    },
                    onClick = {
                        expanded.value = false
                        selectedIndex = index
                        onAlbumSelected(item)
                    }
                )
            }

        }
    }


}
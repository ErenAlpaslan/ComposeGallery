package com.erendev.composegallery

import android.annotation.SuppressLint
import android.net.Uri
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.erendev.composegallery.common.GalleryDefaults.DEFAULT_COLUMN_COUNT
import com.erendev.composegallery.common.GalleryDefaults.DEFAULT_MAX_SELECTION_LIMIT
import com.erendev.composegallery.common.GalleryDefaults.DEFAULT_TOOLBAR_BACKGROUND_COLOR
import com.erendev.composegallery.common.GalleryDefaults.DEFAULT_TOOLBAR_ENABLED
import com.erendev.composegallery.common.enum.GalleryType
import com.erendev.composegallery.ui.views.AlbumSelection
import com.erendev.composegallery.ui.views.image.GalleryImageListStandard
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.PermissionStatus
import com.google.accompanist.permissions.rememberPermissionState

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class,
    ExperimentalPermissionsApi::class
)
@Composable
fun ComposeGallery(
    modifier: Modifier = Modifier,
    limit: Int = DEFAULT_MAX_SELECTION_LIMIT,
    cameraOnly: Boolean = false,
    galleryType: GalleryType = GalleryType.Standard(
        columnCount = DEFAULT_COLUMN_COUNT
    ),
    toolbarEnabled: Boolean = DEFAULT_TOOLBAR_ENABLED,
    onItemsSelected: (List<Uri>) -> Unit,
) {
    val viewModel: ComposeGalleryViewModel = viewModel()

    viewModel.init(LocalContext.current.contentResolver)

    val albums by viewModel.albums.observeAsState()
    val images by viewModel.lastAddedImages.observeAsState()

    val storagePermissionState = rememberPermissionState(
        android.Manifest.permission.READ_EXTERNAL_STORAGE
    )

    Scaffold(
        modifier = modifier,
        topBar = {
            CenterAlignedTopAppBar(
                title = {},
                navigationIcon = {
                    AlbumSelection(
                        items = albums
                    ) { selectedAlbum ->
                        viewModel.onAlbumChanged(selectedAlbum)
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = DEFAULT_TOOLBAR_BACKGROUND_COLOR
                )
            )
        },
        content = {
            when (storagePermissionState.status) {
                // If the camera permission is granted, then show screen with the feature enabled
                PermissionStatus.Granted -> {
                    viewModel.loadAlbums()
                    Box (
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(top = 80.dp)
                    ){
                        when(galleryType) {
                            is GalleryType.Masonry -> {}
                            is GalleryType.Quilted -> {}
                            is GalleryType.Standard -> {
                                GalleryImageListStandard(
                                    items = images,
                                    galleryType = galleryType,
                                    limit = limit
                                ){

                                }
                            }
                        }
                    }
                }
                is PermissionStatus.Denied -> {
                    LaunchedEffect(key1 = "") {
                        storagePermissionState.launchPermissionRequest()
                    }
                    Column {
                        val textToShow = if ((storagePermissionState.status as PermissionStatus.Denied).shouldShowRationale) {
                            // If the user has denied the permission but the rationale can be shown,
                            // then gently explain why the app requires this permission
                            "The storage is important for the showing gallery. Please grant the permission."
                        } else {
                            // If it's the first time the user lands on this feature, or the user
                            // doesn't want to be asked again for this permission, explain that the
                            // permission is required
                            "Storage permission required for this feature to be available. " +
                                    "Please grant the permission"
                        }
                    }
                }
            }
        }
    )
}
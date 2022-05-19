package com.erendev.composegallery

import android.annotation.SuppressLint
import android.net.Uri
import android.util.Log
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.erendev.composegallery.common.GalleryDefaults.DEFAULT_MAX_SELECTION_LIMIT
import com.erendev.composegallery.common.GalleryDefaults.DEFAULT_TOOLBAR_ENABLED
import com.erendev.composegallery.common.enum.GalleryType
import com.erendev.composegallery.data.model.ImageItem
import com.erendev.composegallery.ui.theme.Black
import com.erendev.composegallery.ui.views.AlbumSelection
import com.erendev.composegallery.ui.views.image.GalleryImageListStandard
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.PermissionStatus
import com.google.accompanist.permissions.rememberPermissionState

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(
    ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class,
    ExperimentalPermissionsApi::class
)
@Composable
fun ComposeGallery(
    modifier: Modifier = Modifier,
    limit: Int = DEFAULT_MAX_SELECTION_LIMIT,
    cameraOnly: Boolean = false,
    galleryType: GalleryType = GalleryType.STANDARD,
    toolbarEnabled: Boolean = DEFAULT_TOOLBAR_ENABLED,
    onDone: (List<ImageItem>) -> Unit,
) {
    val viewModel: ComposeGalleryViewModel = viewModel()
    var selectedImages = remember {
        mutableStateOf<List<ImageItem>>(emptyList())
    }
    var isDone by remember {
        mutableStateOf(false)
    }

    viewModel.init(LocalContext.current.contentResolver)

    val albums by viewModel.albums.observeAsState()
    val images by viewModel.images.observeAsState()
    val isDoneEnabled by viewModel.isDoneEnabled.observeAsState()

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
                actions = {
                    if (isDone) {
                        IconButton(onClick = {
                            onDone(selectedImages.value)
                        }) {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = "",
                                tint = Black
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = colorResource(id = R.color.gallery_toolbar_background_color)
                )
            )

        },
        content = {
            when (storagePermissionState.status) {
                // If the camera permission is granted, then show screen with the feature enabled
                PermissionStatus.Granted -> {
                    viewModel.loadAlbums()
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(top = 80.dp)
                    ) {
                        images?.let { list ->
                            when (galleryType) {
                                GalleryType.STANDARD -> {
                                    GalleryImageListStandard(
                                        list = list,
                                        limit = limit,
                                        updatedList = {
                                            selectedImages.value = it
                                            isDone = selectedImages.value.isNotEmpty()
                                        },
                                        onImageCaptured = {
                                            viewModel.addCameraItem(it)
                                            isDone = selectedImages.value.isNotEmpty()
                                        }
                                    )
                                }
                                GalleryType.QUILTED -> {
                                }
                                GalleryType.MASONRY -> {
                                }
                            }
                        }
                    }
                }
                is PermissionStatus.Denied -> {
                    LaunchedEffect(key1 = "") {
                        storagePermissionState.launchPermissionRequest()
                    }
                }
            }
        }
    )
}

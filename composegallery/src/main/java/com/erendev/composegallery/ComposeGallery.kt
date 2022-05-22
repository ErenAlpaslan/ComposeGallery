package com.erendev.composegallery

import android.annotation.SuppressLint
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
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
import com.erendev.composegallery.ui.views.album.AlbumSelection
import com.erendev.composegallery.ui.views.image.BigImageView
import com.erendev.composegallery.ui.views.image.GalleryImageListStandard
import com.erendev.composegallery.usecases.GetAlbumsUseCase
import com.erendev.composegallery.usecases.GetImagesUseCase
import com.erendev.composegallery.utils.extensions.viewModelFactory
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
    val viewModel: ComposeGalleryViewModel = viewModel(factory = viewModelFactory {
        ComposeGalleryViewModel(
            GetAlbumsUseCase(),
            GetImagesUseCase()
        )
    })

    var selectedImages = remember {
        mutableStateOf<List<ImageItem>>(emptyList())
    }

    var isDone by remember {
        mutableStateOf(false)
    }

    viewModel.init(LocalContext.current.contentResolver)

    val albums by viewModel.albums.observeAsState()
    val images by viewModel.filteredImages.observeAsState()
    val isDoneEnabled by viewModel.isDoneEnabled.observeAsState()

    val storagePermissionState = rememberPermissionState(
        android.Manifest.permission.READ_EXTERNAL_STORAGE
    )

    var isLongPressed by remember {
        mutableStateOf(false)
    }

    var bigImageItem by remember {
        mutableStateOf<ImageItem?>(null)
    }

    Scaffold(
        modifier = modifier,
        topBar = {
            CenterAlignedTopAppBar(
                title = {},
                navigationIcon = {
                    if (isLongPressed) {
                        IconButton(
                            onClick = { isLongPressed = false }
                        ) {
                            Icon(
                                imageVector = Icons.Default.ArrowBack,
                                contentDescription = "Arrow Back",
                                tint = Black
                            )
                        }
                    }else {
                        AlbumSelection(
                            items = albums
                        ) { selectedAlbum ->
                            viewModel.onAlbumChanged(selectedAlbum)
                            isLongPressed = false
                        }
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
                                        updatedList = {
                                            selectedImages.value = it
                                            isDone = selectedImages.value.isNotEmpty()
                                        },
                                        onImageCaptured = {
                                            viewModel.addCameraItem(it)
                                            isDone = selectedImages.value.isNotEmpty()
                                        },
                                        onLoadMore = {
                                            viewModel.loadMoreImages(it)
                                        },
                                        onLongClicked = {
                                            isLongPressed = true
                                            bigImageItem = it
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
            if (isLongPressed) {
                BigImageView(imageItem = bigImageItem)
            }
        }
    )
}

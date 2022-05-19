package com.erendev.composegallery.ui.views.camera

import android.Manifest
import android.graphics.Bitmap
import android.net.Uri
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.result.launch
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.erendev.composegallery.R
import com.erendev.composegallery.common.enum.ImageSource
import com.erendev.composegallery.data.model.ImageItem
import com.erendev.composegallery.utils.extensions.saveToGallery
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.PermissionStatus
import com.google.accompanist.permissions.rememberPermissionState

@OptIn(ExperimentalMaterial3Api::class, ExperimentalPermissionsApi::class)
@Composable
fun CameraButton(
    onImageCaptured: (ImageItem) -> Unit
) {
    val cameraPermissionState = rememberPermissionState(
        Manifest.permission.CAMERA
    )

    val context = LocalContext.current

    val cameraLauncher = rememberLauncherForActivityResult(ActivityResultContracts.TakePicturePreview()) { bitmap ->
        if (bitmap != null) {
            val uri = saveToGallery(context, bitmap, "Camera")
            onImageCaptured(ImageItem(
                imagePath = "",
                source = ImageSource.CAMERA,
                uri = uri,
                selected = true
            ))
        }
    }

    Box(
        modifier = Modifier.padding(vertical = 3.dp, horizontal = 2.dp),
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .defaultMinSize(minHeight = 150.dp)
                .clip(RoundedCornerShape(10.dp))
                .clickable {
                    when (cameraPermissionState.status) {
                        PermissionStatus.Granted -> {
                            try {
                                cameraLauncher.launch()
                            } catch (e: Exception) {
                                Log.e("Picker", e.message, e)
                            }
                        }
                        is PermissionStatus.Denied -> {
                            if ((cameraPermissionState.status as PermissionStatus.Denied).shouldShowRationale) {

                            }else {
                                cameraPermissionState.launchPermissionRequest()
                            }
                        }
                    }
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
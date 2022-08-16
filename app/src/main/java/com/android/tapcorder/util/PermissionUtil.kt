package com.android.tapcorder.util

import android.Manifest
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.android.tapcorder.App

object PermissionUtil {

    const val PERMISSIONS_REQUEST_CODE = 100

    private val requiredPermissions = arrayOf(
        Manifest.permission.RECORD_AUDIO,
        Manifest.permission.READ_EXTERNAL_STORAGE,
        Manifest.permission.WRITE_EXTERNAL_STORAGE
    )

    fun checkPermissions(): Boolean {
        val rejectedPermissions = arrayListOf<String>().apply {
            for (permission in requiredPermissions) {
                if (ContextCompat.checkSelfPermission(App.getContext(), permission) != PackageManager.PERMISSION_GRANTED) {
                    add(permission)
                }
            }
        }

        if (rejectedPermissions.isNotEmpty()) {
            val rejectedPermissionArray = arrayOfNulls<String>(rejectedPermissions.size)
            ActivityCompat.requestPermissions(
                App.getCurrentActivity(),
                rejectedPermissions.toArray(rejectedPermissionArray),
                PERMISSIONS_REQUEST_CODE
            )
            return false
        }
        return true
    }
}
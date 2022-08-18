package com.android.tapcorder

import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.Settings
import androidx.appcompat.app.AppCompatActivity
import com.android.tapcorder.ui.main.MainFragment
import com.android.tapcorder.util.PermissionUtil
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        checkPermissions()
    }

    private fun checkPermissions() {
        if (!PermissionUtil.checkPermissions()) {
            return
        }

        showMainFragment()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            PermissionUtil.PERMISSIONS_REQUEST_CODE -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    checkFilePermission()
                    showMainFragment()
                } else {
                    finish()
                }
                return
            }
        }
    }

    private fun checkFilePermission() {
        if (!Environment.isExternalStorageManager()) {
            val intent = Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION)
            val uri = Uri.fromParts("package", "com.android.tapcorder", null)
            intent.data = uri
            App.showToast("Momember 권한을 설정해주세요")
            startActivity(intent)
        }
    }

    private fun showMainFragment() {
        supportFragmentManager.beginTransaction()
            .replace(R.id.container, MainFragment())
            .commitNow()
    }
}
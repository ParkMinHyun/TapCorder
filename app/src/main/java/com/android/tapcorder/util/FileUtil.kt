package com.android.tapcorder.util

import android.annotation.SuppressLint
import android.net.Uri
import android.util.Log
import com.android.tapcorder.App
import com.android.tapcorder.util.ExtensionUtil.TAG
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

class FileUtil {

    companion object {
        val TEMP_FILE_DIR = App.getContext().getExternalFilesDir("/")!!.absolutePath
        val SAVE_FILE_DIR = "$TEMP_FILE_DIR/result/"

        @SuppressLint("SimpleDateFormat")
        fun createFileName() = "$TEMP_FILE_DIR/${SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())}.mp4"

        fun getSavedAudioUris(): List<Uri> = mutableListOf<Uri>().apply {
            Log.d(TAG, "getSavedAudioUris")

            if (!File(SAVE_FILE_DIR).exists()) {
                File(SAVE_FILE_DIR).mkdir()
            }

            File(SAVE_FILE_DIR).listFiles()?.forEach {
                Log.v(TAG, "${it.name} - ${it.length()}")
                add(Uri.parse("$SAVE_FILE_DIR/${it.name}"))
            }
        }
    }
}
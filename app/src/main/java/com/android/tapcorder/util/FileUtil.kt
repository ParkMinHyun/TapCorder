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
        val FILE_DST = App.getContext().getExternalFilesDir("/")!!.absolutePath

        @SuppressLint("SimpleDateFormat")
        fun createFileName() = "$FILE_DST/${SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())}.mp4"

        fun getSavedAudioUris(): List<Uri> = mutableListOf<Uri>().apply {
            Log.d(TAG, "getSavedAudioUris")

            if (!File(FILE_DST).exists()) {
                File(FILE_DST).mkdir()
            }

            File(FILE_DST).listFiles()?.forEach {
                Log.v(TAG, "${it.name} - ${it.length()}")
                add(Uri.parse("$FILE_DST/${it.name}"))
            }
        }
    }
}
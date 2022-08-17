package com.android.tapcorder.util

import android.annotation.SuppressLint
import com.android.tapcorder.App
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

class FileUtil {

    companion object {
        private const val DEFAULT_FILE_NAME = "지나간 음성"
        val TEMP_FILE_DIR = App.getContext().getExternalFilesDir("/")!!.absolutePath
        val SAVE_FILE_DIR = "$TEMP_FILE_DIR/result/"

        @SuppressLint("SimpleDateFormat")
        fun createTempFilePath() = "$TEMP_FILE_DIR/${SimpleDateFormat("yyyy년 MM월 dd일 HH:mm:ss").format(Date())}.mp3"

        fun createSaveFilePath(): String {
            var index = 1
            while (true) {
                val fileName = "$SAVE_FILE_DIR/$DEFAULT_FILE_NAME$index.mp3"
                if (!File(fileName).exists()) {
                    return fileName
                }
                index++
            }
        }

//            val audioData = AudioDB.getInstance().audioDao().getAll()
//            if (!File(SAVE_FILE_DIR).exists()) {
//                File(SAVE_FILE_DIR).mkdir()
//            }
//
//            File(SAVE_FILE_DIR).listFiles()?.forEach {
//                Log.v(TAG, "${it.name} - ${it.length()}")
//                add(Uri.parse("$SAVE_FILE_DIR/${it.name}"))
//            }
//        }
    }
}
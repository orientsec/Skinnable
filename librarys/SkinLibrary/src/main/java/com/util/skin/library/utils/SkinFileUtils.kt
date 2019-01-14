package com.util.skin.library.utils

import android.content.Context
import android.os.Environment
import android.text.TextUtils

import java.io.File

object SkinFileUtils {
    fun getSkinDir(context: Context): String {
        val skinDir = File(getCacheDir(context), SkinConstants.SKIN_DEPLOY_PATH)
        if (!skinDir.exists()) {
            skinDir.mkdirs()
        }
        return skinDir.absolutePath
    }

    private fun getCacheDir(context: Context): String {
        if (Environment.getExternalStorageState() == Environment.MEDIA_MOUNTED) {
            val cacheDir = context.externalCacheDir
            if (cacheDir != null && (cacheDir.exists() || cacheDir.mkdirs())) {
                return cacheDir.absolutePath
            }
        }

        return context.cacheDir.absolutePath
    }


    fun isFileExists(path: String): Boolean {
        return !TextUtils.isEmpty(path) && File(path).exists()
    }
}

package com.util.skin.library.utils

import android.content.Context
import android.media.ExifInterface
import android.os.Environment
import android.text.TextUtils
import java.io.File
import java.io.IOException

/**
 * @PackageName com.util.skin.library.utils
 * @date 2019/1/18 15:51
 * @author zhanglei
 */

const val SKIN_DEPLOY_PATH = "skins"

internal fun getImageRotateAngle(filePath: String): Int {
    val exif: ExifInterface? = try {
        ExifInterface(filePath)
    } catch (e: IOException) {
        e.printStackTrace()
        null
    }

    var angle = 0
    if (exif != null) {
        val ori =
            exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_UNDEFINED)
        angle = when (ori) {
            ExifInterface.ORIENTATION_ROTATE_90 -> 90
            ExifInterface.ORIENTATION_ROTATE_180 -> 180
            ExifInterface.ORIENTATION_ROTATE_270 -> 270
            else -> 0
        }
    }
    return angle
}

fun getSkinDir(context: Context): String {
    val skinDir = File(getCacheDir(context), SKIN_DEPLOY_PATH)
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
package com.util.skin.library.utils

import android.media.ExifInterface
import java.io.IOException

internal object SkinImageUtils {
    fun getImageRotateAngle(filePath: String): Int {
        val exif: ExifInterface?
        exif = try {
            ExifInterface(filePath)
        } catch (e: IOException) {
            e.printStackTrace()
            null

        }

        var angle = 0
        if (exif != null) {
            val ori = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_UNDEFINED)
            angle = when (ori) {
                ExifInterface.ORIENTATION_ROTATE_90 -> 90
                ExifInterface.ORIENTATION_ROTATE_180 -> 180
                ExifInterface.ORIENTATION_ROTATE_270 -> 270
                else -> 0
            }
        }
        return angle
    }
}

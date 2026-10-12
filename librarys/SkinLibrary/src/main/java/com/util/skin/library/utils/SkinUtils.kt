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

/**
 * 刷新 RecyclerView 缓存池并通知数据发生改变，保证出队 Item 自动响应当前最新皮肤
 */
fun androidx.recyclerview.widget.RecyclerView.applySkinAndInvalidatePool() {
    try {
        recycledViewPool.clear()
        adapter?.notifyDataSetChanged()
    } catch (e: Exception) {
        Slog.e("SkinUtils", "Failed to refresh RecyclerView pool", e)
    }
}

/**
 * 递归遍历 View 树并尝试刷新每一个支持换肤的 View。
 * 可用于刷新 ViewPager、ViewPager2 或 Nested Fragment 中的动态 View 结构。
 */
fun applySkinToViewTree(view: android.view.View?) {
    if (view == null) return
    if (view is com.util.skin.library.widget.SkinSupportable && view.skinnable) {
        view.applySkin()
    }
    view.applySkinBinders()
    if (view is androidx.recyclerview.widget.RecyclerView) {
        view.applySkinAndInvalidatePool()
    }
    if (view is android.view.ViewGroup) {
        for (i in 0 until view.childCount) {
            applySkinToViewTree(view.getChildAt(i))
        }
    }
}
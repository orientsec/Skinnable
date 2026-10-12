package com.util.skinnable.support.compat.lottie

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import com.airbnb.lottie.ImageAssetDelegate
import com.airbnb.lottie.LottieImageAsset
import com.util.skin.library.res.SkinResourcesManager
import com.util.skin.library.utils.Slog

/**
 * Lottie 图层图片重定向代理实现 [ImageAssetDelegate]。
 * 当 Lottie 绘制图片图层时，优先从当前激活的外置/动态皮肤包中查找对应的图片资源；
 * 若皮肤包中不存在对应资源，则返回 null 以自动降级触发 Lottie 默认的资源加载逻辑。
 */
class SkinLottieImageAssetDelegate(
    private val context: Context
) : ImageAssetDelegate {

    override fun fetchBitmap(asset: LottieImageAsset): Bitmap? {
        val fileName = asset.fileName ?: asset.id ?: return null
        val resName = extractResourceName(fileName)

        try {
            // 1. 尝试通过 SkinResourcesManager 根据资源名称在当前皮肤包中检索 drawable 资源 ID
            val resId = context.resources.getIdentifier(resName, "drawable", context.packageName)
            if (resId != 0) {
                val drawable = SkinResourcesManager.getDrawable(context, resId)
                if (drawable != null) {
                    return drawableToBitmap(drawable, asset.width, asset.height)
                }
            }

            // 2. 尝试从 mipmap 资源类型查找
            val mipmapResId = context.resources.getIdentifier(resName, "mipmap", context.packageName)
            if (mipmapResId != 0) {
                val drawable = SkinResourcesManager.getDrawable(context, mipmapResId)
                if (drawable != null) {
                    return drawableToBitmap(drawable, asset.width, asset.height)
                }
            }
        } catch (e: Exception) {
            Slog.e("SkinLottieImageAssetDelegate", "Error fetching skin bitmap for asset: $fileName", e)
        }

        // 皮肤包中未找到匹配图片，返回 null 降级走 Lottie 内置默认 Asset 加载逻辑
        return null
    }

    private fun extractResourceName(fileName: String): String {
        val dotIndex = fileName.lastIndexOf('.')
        val name = if (dotIndex > 0) fileName.substring(0, dotIndex) else fileName
        return name.lowercase().replace("-", "_")
    }

    private fun drawableToBitmap(drawable: Drawable, reqWidth: Int, reqHeight: Int): Bitmap {
        if (drawable is BitmapDrawable && drawable.bitmap != null) {
            return drawable.bitmap
        }

        val width = if (drawable.intrinsicWidth > 0) drawable.intrinsicWidth else if (reqWidth > 0) reqWidth else 1
        val height = if (drawable.intrinsicHeight > 0) drawable.intrinsicHeight else if (reqHeight > 0) reqHeight else 1

        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        drawable.setBounds(0, 0, canvas.width, canvas.height)
        drawable.draw(canvas)
        return bitmap
    }
}

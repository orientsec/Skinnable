package com.util.skin.library.loader

import android.content.Context
import android.content.res.ColorStateList
import android.content.res.XmlResourceParser
import android.graphics.drawable.Drawable
import android.text.TextUtils
import android.util.TypedValue
import androidx.core.content.res.ResourcesCompat
import com.util.skin.library.helpers.SkinHelper
import com.util.skin.library.model.SkinResource

/**
 * @PackageName com.util.skin.library.loader
 * @date 2022/8/26 16:49
 * @author zhanglei
 */
internal class ResourceLoaderImpl constructor(
    private val strategy: SkinLoaderStrategy
) : ResourceLoader {
    private var resource: SkinResource? = null

    override fun initStrategy(context: Context, skinName: String): SkinResource? =
        strategy.initStrategy(context)?.apply { resource = this }

    override fun getColor(context: Context, resId: Int): Int {
        val targetResId = getTargetResId(context, resId)
        if (targetResId != SkinHelper.INVALID_ID) {
            resource?.apply {
                try {
                    return ResourcesCompat.getColor(resources, targetResId, context.theme)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
        return SkinHelper.INVALID_ID
    }

    override fun getColorStateList(context: Context, resId: Int): ColorStateList? {
        val targetResId = getTargetResId(context, resId)
        if (targetResId != SkinHelper.INVALID_ID) {
            resource?.apply {
                try {
                    return ResourcesCompat.getColorStateList(
                        resources,
                        targetResId,
                        context.theme
                    )
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
        return null
    }

    override fun getDrawable(context: Context, resId: Int): Drawable? {
        val targetResId = getTargetResId(context, resId)
        if (targetResId != SkinHelper.INVALID_ID) {
            resource?.apply {
                try {
                    return ResourcesCompat.getDrawable(
                        resources,
                        targetResId,
                        context.theme
                    )
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
        return null
    }

    override fun getXml(context: Context, resId: Int): XmlResourceParser? {
        val targetResId = getTargetResId(context, resId)
        if (targetResId != SkinHelper.INVALID_ID) {
            resource?.apply {
                try {
                    return resources.getXml(targetResId)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
        return null
    }

    override fun getValue(
        context: Context,
        resId: Int,
        outValue: TypedValue,
        resolveRefs: Boolean
    ) {
        val targetResId = getTargetResId(context, resId)
        if (targetResId != SkinHelper.INVALID_ID) {
            resource?.apply {
                try {
                    resources.getValue(targetResId, outValue, resolveRefs)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    }

    private fun getTargetResId(context: Context, resId: Int): Int {
        return try {
            resource?.let {
                var resName =
                    strategy.getTargetResourceEntryName(context, resId)
                if (TextUtils.isEmpty(resName)) {
                    resName = context.resources.getResourceEntryName(resId)
                }
                val type = context.resources.getResourceTypeName(resId)
                it.resources.getIdentifier(resName, type, it.pkgName)
            } ?: SkinHelper.INVALID_ID
        } catch (e: Exception) {
            // 换肤失败不至于应用崩溃.
            e.printStackTrace()
            SkinHelper.INVALID_ID
        }
    }
}
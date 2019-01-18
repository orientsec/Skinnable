package com.util.skin.library.loader

import android.content.Context
import android.content.res.ColorStateList
import android.content.res.XmlResourceParser
import android.graphics.drawable.Drawable
import android.text.TextUtils
import android.util.TypedValue
import androidx.core.content.res.ResourcesCompat
import com.util.skin.library.helpers.SkinHelper
import com.util.skin.library.model.SkinResModel

/**
 * @PackageName com.util.skin.library.loader
 * @date 2019/1/18 10:16
 * @author zhanglei
 */
abstract class BaseSkinLoaderStrategy : SkinLoaderStrategy {
    protected lateinit var resModel: SkinResModel

    override fun getColor(context: Context, resId: Int): Int {
        if (::resModel.isInitialized) {
            val targetResId = getTargetResId(context, resId)
            if (targetResId != SkinHelper.INVALID_ID) {
                try {
                    return ResourcesCompat.getColor(resModel.resources, targetResId, context.theme)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
        return SkinHelper.INVALID_ID
    }

    override fun getColorStateList(context: Context, resId: Int): ColorStateList? {
        if (::resModel.isInitialized) {
            val targetResId = getTargetResId(context, resId)
            if (targetResId != SkinHelper.INVALID_ID) {
                try {
                    return ResourcesCompat.getColorStateList(resModel.resources, targetResId, context.theme)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
        return null
    }

    override fun getDrawable(context: Context, resId: Int): Drawable? {
        if (::resModel.isInitialized) {
            val targetResId = getTargetResId(context, resId)
            if (targetResId != SkinHelper.INVALID_ID) {
                try {
                    return ResourcesCompat.getDrawable(resModel.resources, targetResId, context.theme)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
        return null
    }

    override fun getXml(context: Context, resId: Int): XmlResourceParser? {
        if (::resModel.isInitialized) {
            val targetResId = getTargetResId(context, resId)
            if (targetResId != SkinHelper.INVALID_ID) {
                try {
                    return resModel.resources.getXml(targetResId)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
        return null
    }

    override fun getValue(context: Context, resId: Int, outValue: TypedValue, resolveRefs: Boolean) {
        if (::resModel.isInitialized) {
            val targetResId = getTargetResId(context, resId)
            if (targetResId != SkinHelper.INVALID_ID) {
                try {
                    resModel.resources.getValue(targetResId, outValue, resolveRefs)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    }

    private fun getTargetResId(context: Context, resId: Int): Int {
        return try {
            var resName: String? = getTargetResourceEntryName(context, resModel.skinName, resId)
            if (TextUtils.isEmpty(resName)) {
                resName = context.resources.getResourceEntryName(resId)
            }
            val type = context.resources.getResourceTypeName(resId)
            resModel.resources.getIdentifier(resName, type, resModel.pkgName)
        } catch (e: Exception) {
            // 换肤失败不至于应用崩溃.
            e.printStackTrace()
            SkinHelper.INVALID_ID
        }
    }
}
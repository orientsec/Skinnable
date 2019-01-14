package com.util.skin.library.res

import android.content.Context
import android.content.res.ColorStateList
import android.content.res.Resources
import android.content.res.XmlResourceParser
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.text.TextUtils
import android.util.TypedValue

import com.util.skin.library.SkinManager

import java.util.ArrayList

import androidx.annotation.AnyRes
import androidx.core.content.res.ResourcesCompat
import com.util.skin.library.loader.SkinLoaderStrategy
import com.util.skin.library.loader.SkinLoaderStrategyType

object SkinResourcesManager {
    var skinResources: Resources? = null
        private set
    var skinPkgName = ""
        private set
    private var mSkinName = ""
    var strategy: SkinLoaderStrategy? = null
        private set
    var isDefaultSkin = true
        private set
    private val mSkinResources = ArrayList<SkinResources>()

    fun addSkinResources(resources: SkinResources) {
        mSkinResources.add(resources)
    }

    @JvmOverloads
    fun reset(strategy: SkinLoaderStrategy = SkinManager.strategies[SkinLoaderStrategyType.Default]!!) {
        skinResources = SkinManager.context.resources
        skinPkgName = ""
        mSkinName = ""
        SkinResourcesManager.strategy = strategy
        isDefaultSkin = true
        SkinUserThemeManager.clearCaches()
        for (skinResources in mSkinResources) {
            skinResources.clear()
        }
    }

    fun setupSkin(resources: Resources?, pkgName: String, skinName: String, strategy: SkinLoaderStrategy) {
        if (resources == null || TextUtils.isEmpty(pkgName) || TextUtils.isEmpty(skinName)) {
            reset(strategy)
            return
        }
        skinResources = resources
        skinPkgName = pkgName
        mSkinName = skinName
        SkinResourcesManager.strategy = strategy
        isDefaultSkin = false
        SkinUserThemeManager.clearCaches()
        for (skinResources in mSkinResources) {
            skinResources.clear()
        }
    }

    fun getTargetResId(context: Context, resId: Int): Int {
        return try {
            var resName: String? = null
            if (strategy != null) {
                resName = strategy!!.getTargetResourceEntryName(context,
                    mSkinName, resId)
            }
            if (TextUtils.isEmpty(resName)) {
                resName = context.resources.getResourceEntryName(resId)
            }
            val type = context.resources.getResourceTypeName(resId)
            skinResources!!.getIdentifier(resName, type,
                skinPkgName
            )
        } catch (e: Exception) {
            // 换肤失败不至于应用崩溃.
            e.printStackTrace()
            0
        }

    }

    private fun getSkinColor(context: Context, resId: Int): Int {
        if (!SkinUserThemeManager.isColorEmpty) {
            SkinUserThemeManager.getColorStateList(resId)?.apply {
                return defaultColor
            }
        }
        if (strategy != null) {
            val colorStateList = strategy!!.getColor(context,
                mSkinName, resId)
            return colorStateList!!.defaultColor
        }
        if (!isDefaultSkin) {
            val targetResId = getTargetResId(context, resId)
            if (targetResId != 0) {
                return ResourcesCompat.getColor(skinResources!!, targetResId, context.theme)
            }
        }
        return ResourcesCompat.getColor(context.resources, resId, context.theme)
    }

    private fun getSkinColorStateList(context: Context, resId: Int): ColorStateList? {
        if (!SkinUserThemeManager.isColorEmpty) {
            return SkinUserThemeManager.getColorStateList(resId)
        }
        if (strategy != null) {
            return strategy!!.getColorStateList(context,
                mSkinName, resId)!!
        }
        if (!isDefaultSkin) {
            val targetResId = getTargetResId(context, resId)
            if (targetResId != 0) {
                return ResourcesCompat.getColorStateList(skinResources!!, targetResId, context.theme)!!
            }
        }
        return ResourcesCompat.getColorStateList(context.resources, resId, context.theme)!!
    }

    private fun getSkinDrawable(context: Context, resId: Int): Drawable? {
        if (!SkinUserThemeManager.isColorEmpty) {
            SkinUserThemeManager.getColorStateList(resId)?.apply {
                return ColorDrawable(defaultColor)
            }
        }
        if (!SkinUserThemeManager.isDrawableEmpty) {
            return SkinUserThemeManager.getDrawable(resId)
        }
        if (strategy != null) {
            return strategy!!.getDrawable(context,
                mSkinName, resId)
        }
        if (!isDefaultSkin) {
            val targetResId = getTargetResId(context, resId)
            if (targetResId != 0) {
                return ResourcesCompat.getDrawable(skinResources!!, targetResId, context.theme)
            }
        }
        return ResourcesCompat.getDrawable(context.resources!!, resId, context.theme)
    }

    fun getStrategyDrawable(context: Context, resId: Int): Drawable? {
        return if (strategy != null) {
            strategy!!.getDrawable(context,
                mSkinName, resId)
        } else null
    }

    private fun getSkinXml(context: Context, resId: Int): XmlResourceParser {
        if (!isDefaultSkin) {
            val targetResId = getTargetResId(context, resId)
            if (targetResId != 0) {
                return skinResources!!.getXml(targetResId)
            }
        }
        return context.resources.getXml(resId)
    }

    private fun getSkinValue(context: Context, @AnyRes resId: Int, outValue: TypedValue, resolveRefs: Boolean) {
        if (!isDefaultSkin) {
            val targetResId = getTargetResId(context, resId)
            if (targetResId != 0) {
                skinResources!!.getValue(targetResId, outValue, resolveRefs)
                return
            }
        }
        context.resources.getValue(resId, outValue, resolveRefs)
    }

    fun getColor(context: Context, resId: Int): Int {
        return getSkinColor(context, resId)
    }

    fun getColorStateList(context: Context, resId: Int): ColorStateList? {
        return getSkinColorStateList(context, resId)
    }

    fun getDrawable(context: Context, resId: Int): Drawable? {
        return getSkinDrawable(context, resId)
    }

    fun getXml(context: Context, resId: Int): XmlResourceParser {
        return getSkinXml(context, resId)
    }

    fun getValue(context: Context, @AnyRes resId: Int, outValue: TypedValue, resolveRefs: Boolean) {
        getSkinValue(context, resId, outValue, resolveRefs)
    }
}

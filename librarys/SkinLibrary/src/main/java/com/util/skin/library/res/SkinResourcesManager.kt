package com.util.skin.library.res

import android.content.Context
import android.content.res.ColorStateList
import android.content.res.Resources
import android.content.res.XmlResourceParser
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.text.TextUtils
import android.util.AttributeSet
import android.util.TypedValue
import androidx.annotation.AnyRes
import androidx.core.content.res.ResourcesCompat
import com.util.skin.library.R
import com.util.skin.library.SkinManager
import com.util.skin.library.helpers.SkinHelper.Companion.INVALID_ID
import com.util.skin.library.loader.SkinLoaderStrategy
import com.util.skin.library.loader.SkinLoaderStrategyType
import com.util.skin.library.utils.SkinPreference
import java.util.*

object SkinResourcesManager {
    private var skinResources: Resources? = null
    private var skinPkgName = ""
    private var mSkinName = ""
    private var strategy: SkinLoaderStrategy? = null
    private val mSkinResources = ArrayList<SkinResources>()

    /**默认皮肤*/
    var isDefaultSkin = true
        private set

    fun addSkinResources(resources: SkinResources) {
        mSkinResources.add(resources)
    }

    internal fun reset() {
        SkinPreference.setSkinName("")
            .setSkinStrategy(SkinLoaderStrategyType.Default)
            .commitEditor()
        skinResources = SkinManager.context.resources
        skinPkgName = ""
        mSkinName = ""
        SkinResourcesManager.strategy = SkinManager.strategies[SkinLoaderStrategyType.Default]!!
        isDefaultSkin = true
        SkinUserThemeManager.clearCaches()
        for (skinResources in mSkinResources) {
            skinResources.clear()
        }
    }

    fun setupSkin(resources: Resources?, pkgName: String, skinName: String, strategy: SkinLoaderStrategy) {
        if (resources == null || TextUtils.isEmpty(pkgName) || TextUtils.isEmpty(skinName)) {
            reset()
            return
        }
        SkinPreference.setSkinName(skinName)
            .setSkinStrategy(strategy.type)
            .commitEditor()
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

    internal fun getTargetResId(context: Context, resId: Int): Int {
        return try {
            var resName: String? = strategy?.getTargetResourceEntryName(context, mSkinName, resId)
            if (TextUtils.isEmpty(resName)) {
                resName = context.resources.getResourceEntryName(resId)
            }
            val type = context.resources.getResourceTypeName(resId)
            skinResources!!.getIdentifier(resName, type, skinPkgName)
        } catch (e: Exception) {
            // 换肤失败不至于应用崩溃.
            e.printStackTrace()
            INVALID_ID
        }

    }

    private fun getSkinColor(context: Context, resId: Int): Int {
        if (!SkinUserThemeManager.isColorEmpty) {
            SkinUserThemeManager.getColorStateList(resId)
                ?.apply {
                    return defaultColor
                }
        }
        strategy?.getColor(context, mSkinName, resId)
            ?.apply {
                return defaultColor
            }
        if (!isDefaultSkin) {
            val targetResId = getTargetResId(context, resId)
            if (targetResId != INVALID_ID) {
                return ResourcesCompat.getColor(skinResources!!, targetResId, context.theme)
            }
        }
        return ResourcesCompat.getColor(context.resources, resId, context.theme)
    }

    private fun getSkinColorStateList(context: Context, resId: Int): ColorStateList? {
        if (!SkinUserThemeManager.isColorEmpty) {
            SkinUserThemeManager.getColorStateList(resId)
                ?.let { return it }
        }
        strategy?.getColorStateList(context, mSkinName, resId)
            ?.let { return it }
        if (!isDefaultSkin) {
            val targetResId = getTargetResId(context, resId)
            if (targetResId != INVALID_ID) {
                return ResourcesCompat.getColorStateList(skinResources!!, targetResId, context.theme)
                    ?: ResourcesCompat.getColorStateList(context.resources, resId, context.theme)
            }
        }
        return ResourcesCompat.getColorStateList(context.resources, resId, context.theme)
    }

    private fun getSkinDrawable(context: Context, resId: Int): Drawable? {
        if (!SkinUserThemeManager.isColorEmpty) {
            SkinUserThemeManager.getColorStateList(resId)
                ?.apply {
                    return ColorDrawable(defaultColor)
                }
        }
        if (!SkinUserThemeManager.isDrawableEmpty) {
            SkinUserThemeManager.getDrawable(resId)
                ?.let {
                    return it
                }
        }
        strategy?.apply {
            getDrawable(context, mSkinName, resId)?.let {
                return it
            }
        }
        if (!isDefaultSkin) {
            val targetResId = getTargetResId(context, resId)
            if (targetResId != INVALID_ID) {
                return skinResources?.run {
                    ResourcesCompat.getDrawable(this, targetResId, context.theme)
                } ?: ResourcesCompat.getDrawable(context.resources, resId, context.theme)
            }
        }
        return ResourcesCompat.getDrawable(context.resources!!, resId, context.theme)
    }

    fun getStrategyDrawable(context: Context, resId: Int): Drawable? {
        return strategy?.getDrawable(context, mSkinName, resId)
    }

    private fun getSkinXml(context: Context, resId: Int): XmlResourceParser {
        if (!isDefaultSkin) {
            val targetResId = getTargetResId(context, resId)
            if (targetResId != INVALID_ID) {
                return skinResources!!.getXml(targetResId)
                    ?: context.resources.getXml(resId)
            }
        }
        return context.resources.getXml(resId)
    }

    private fun getSkinValue(context: Context, @AnyRes resId: Int, outValue: TypedValue, resolveRefs: Boolean) {
        if (!isDefaultSkin) {
            val targetResId = getTargetResId(context, resId)
            if (targetResId != INVALID_ID) {
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

    fun parseSkinnable(
        context: Context,
        attrs: AttributeSet? = null,
        defStyleAttr: Int = 0
    ): Boolean {
        val a = context.obtainStyledAttributes(attrs, R.styleable.SkinSupportable, defStyleAttr, 0)
        return try {
            if (a.hasValue(R.styleable.SkinSupportable_skinnable)) {
                a.getBoolean(R.styleable.SkinSupportable_skinnable, false)
            } else false
        } finally {
            a.recycle()
        }
    }
}

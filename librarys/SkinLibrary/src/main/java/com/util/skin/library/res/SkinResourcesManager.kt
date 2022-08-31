package com.util.skin.library.res

import android.content.Context
import android.content.res.ColorStateList
import android.content.res.XmlResourceParser
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.util.TypedValue
import androidx.annotation.AnyRes
import androidx.core.content.res.ResourcesCompat
import com.util.skin.library.R
import com.util.skin.library.helpers.SkinHelper.Companion.INVALID_ID
import com.util.skin.library.loader.ResourceLoader
import com.util.skin.library.loader.ResourceLoaderImpl
import com.util.skin.library.loader.SkinStrategy
import com.util.skin.library.utils.SkinPreference
import java.util.concurrent.atomic.AtomicReference

/**
 * 换肤资源管理
 */
object SkinResourcesManager {
    private val mSkinResources = ArrayList<SkinResources>()
    private val resource = AtomicReference<ResourceLoader>()

    /**默认皮肤*/
    val isDefaultSkin: Boolean
        get() = resource.get() != null

    fun addSkinResources(resources: SkinResources) {
        mSkinResources.add(resources)
    }

    /**
     * 初始化Resource
     */
    fun setupResource(strategy: SkinStrategy) {
        resource.set(ResourceLoaderImpl(strategy.loaderStrategy))
        SkinPreference.addResources(strategy.key).commitEditor()
    }

    /**
     * 缓存的策略
     */
    fun defaultStrategy(): SkinStrategy? {
        return SkinPreference.strategies().firstOrNull()
    }

    /**
     * 恢复默认皮肤
     */
    fun resetDefault() {
        resource.set(null)
        SkinPreference.resetResources().commitEditor()
        SkinUserThemeManager.clearCaches()
        for (skinResources in mSkinResources) {
            skinResources.clear()
        }
    }

    /**
     * 获取颜色
     */
    private fun getSkinColor(context: Context, resId: Int): Int {
        if (!SkinUserThemeManager.isColorEmpty) {
            SkinUserThemeManager.getColorStateList(resId)
                ?.apply {
                    return defaultColor
                }
        }
        resource.get()
            ?.getColor(context, resId)
            ?.let {
                if (it != INVALID_ID) {
                    return it
                }
            }
        return ResourcesCompat.getColor(context.resources, resId, context.theme)
    }

    /**
     * 获取ColorStateList
     */
    private fun getSkinColorStateList(context: Context, resId: Int): ColorStateList? {
        if (!SkinUserThemeManager.isColorEmpty) {
            SkinUserThemeManager.getColorStateList(resId)
                ?.let { return it }
        }
        return resource.get()?.getColorStateList(context, resId)
            ?: ResourcesCompat.getColorStateList(context.resources, resId, context.theme)
    }

    /**
     * 获取Drawable
     */
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
        // 使用每种策略中的resource加载资源
        return resource.get()?.getDrawable(context, resId)
            ?: ResourcesCompat.getDrawable(context.resources!!, resId, context.theme)
    }

    private fun getSkinXml(context: Context, resId: Int): XmlResourceParser {
        return resource.get()?.getXml(context, resId) ?: context.resources.getXml(resId)
    }

    private fun getSkinValue(
        context: Context,
        @AnyRes resId: Int,
        outValue: TypedValue,
        resolveRefs: Boolean
    ) {
        resource.get()?.getValue(context, resId, outValue, resolveRefs)
            ?: context.resources.getValue(resId, outValue, resolveRefs)
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

    private val SkinStrategy.key: String
        get() = "$skinName:${this::class.java.simpleName}"
}

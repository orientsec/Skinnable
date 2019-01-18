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
import com.util.skin.library.loader.SkinLoaderStrategy
import com.util.skin.library.loader.SkinLoaderStrategyType
import com.util.skin.library.utils.SkinPreference
import java.util.*

object SkinResourcesManager {
    private val mSkinResources = ArrayList<SkinResources>()
    private val strategyMap = hashMapOf<String, SkinLoaderStrategy>()

    /**默认皮肤*/
    var isDefaultSkin = true
        private set
        get() = strategyMap.isEmpty()

    fun addSkinResources(resources: SkinResources) {
        mSkinResources.add(resources)
    }

    /**
     * 增加策略
     */
    fun addStrategy(skinName: String, strategy: SkinLoaderStrategy) {
        strategyMap[getSkinResourcesKey(skinName, strategy.type)] = strategy
        val value = getSkinResourcesKey(skinName, strategy.type)
        SkinPreference.addResources(value).commitEditor()
    }

    /**
     * 删除策略
     */
    fun removeStrategy(skinName: String, strategyType: SkinLoaderStrategyType) {
        strategyMap.remove(getSkinResourcesKey(skinName, strategyType))
        val value = getSkinResourcesKey(skinName, strategyType)
        SkinPreference.removeResources(value).commitEditor()
    }

    /**
     * 恢复默认皮肤
     */
    fun resetDefault() {
        strategyMap.clear()
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
        strategyMap.values
            .sortedBy { strategy -> strategy.type.type }
            .forEach { strategy ->
                strategy.getColor(context, resId)
                    .let {
                        if (it != INVALID_ID) {
                            return it
                        }
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
        strategyMap.values
            .sortedBy { strategy -> strategy.type.type }
            .forEach { strategy ->
                strategy.getColorStateList(context, resId)
                    ?.let { return it }
            }
        return ResourcesCompat.getColorStateList(context.resources, resId, context.theme)
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
        strategyMap.values
            .sortedBy { strategy -> strategy.type.type }
            .forEach { strategy ->
                strategy.getDrawable(context, resId)
                    ?.let { return it }
            }
        return ResourcesCompat.getDrawable(context.resources!!, resId, context.theme)
    }

    private fun getSkinXml(context: Context, resId: Int): XmlResourceParser {
        strategyMap.values
            .sortedBy { strategy -> strategy.type.type }
            .forEach { strategy ->
                strategy.getXml(context, resId)
                    ?.let { return it }
            }
        return context.resources.getXml(resId)
    }

    private fun getSkinValue(context: Context, @AnyRes resId: Int, outValue: TypedValue, resolveRefs: Boolean) {
        strategyMap.values
            .sortedBy { strategy -> strategy.type.type }
            .forEach { strategy ->
                strategy.getValue(context, resId, outValue, resolveRefs)
            }
        context.resources.getValue(resId, outValue, resolveRefs)
    }

    private fun getSkinResourcesKey(skinName: String, strategyType: SkinLoaderStrategyType): String =
        "$skinName:${strategyType.type}"

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

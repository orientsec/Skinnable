package com.util.skin.library.res

import android.content.Context
import android.content.res.ColorStateList
import android.content.res.XmlResourceParser
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.util.TypedValue
import androidx.annotation.AnyRes
import com.util.skin.library.R
import com.util.skin.library.fallback.ISkinErrorTracker
import com.util.skin.library.fallback.SafeSkinResources
import com.util.skin.library.fallback.SkinFallbackManager
import com.util.skin.library.loader.ResourceLoader
import com.util.skin.library.loader.ResourceLoaderImpl
import com.util.skin.library.loader.SkinStrategy
import com.util.skin.library.model.appContext
import com.util.skin.library.utils.SkinPreference
import java.util.concurrent.atomic.AtomicReference

/**
 * 换肤资源管理，集成 SafeSkinResources 三级代理与 SkinFallbackManager 事务熔断器。
 */
object SkinResourcesManager {
    private val mSkinResources = ArrayList<SkinResources>()
    private val resource = AtomicReference<ResourceLoader>()

    val fallbackManager = SkinFallbackManager()
    val safeResources = SafeSkinResources(fallbackManager = fallbackManager)

    var currentStrategyKey: String = ""
        private set

    /**默认皮肤*/
    val isDefaultSkin: Boolean
        get() = resource.get() == null

    fun addSkinResources(resources: SkinResources) {
        mSkinResources.add(resources)
    }

    fun setErrorTracker(tracker: ISkinErrorTracker?) {
        safeResources.setTracker(tracker)
    }

    /**
     * 初始化 Resource
     */
    fun setupResource(strategy: SkinStrategy) {
        currentStrategyKey = strategy.key
        val resourceLoader = ResourceLoaderImpl(strategy.loaderStrategy)
        val skinRes = resourceLoader.initStrategy(appContext)
        checkNotNull(skinRes) { "SkinResource initialization returned null for strategy: ${strategy.key}" }
        resource.set(resourceLoader)
        SkinPreference.addResources(strategy.key).commitEditor()
        SkinUserThemeManager.clearCaches()
        for (skinResources in mSkinResources) {
            skinResources.clear()
        }
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
        currentStrategyKey = ""
        resource.set(null)
        SkinPreference.resetResources().commitEditor()
        SkinUserThemeManager.clearCaches()
        for (skinResources in mSkinResources) {
            skinResources.clear()
        }
    }

    fun getColor(context: Context, resId: Int): Int {
        return safeResources.getColor(context, resId, resource.get(), currentStrategyKey)
    }

    fun getColorStateList(context: Context, resId: Int): ColorStateList? {
        return safeResources.getColorStateList(context, resId, resource.get(), currentStrategyKey)
    }

    fun getDrawable(context: Context, resId: Int): Drawable? {
        return safeResources.getDrawable(context, resId, resource.get(), currentStrategyKey)
    }

    fun getXml(context: Context, resId: Int): XmlResourceParser? {
        return safeResources.getXml(context, resId, resource.get(), currentStrategyKey)
    }

    fun getValue(context: Context, @AnyRes resId: Int, outValue: TypedValue, resolveRefs: Boolean) {
        safeResources.getValue(context, resId, outValue, resolveRefs, resource.get(), currentStrategyKey)
    }

    fun parseSkinnable(
        context: Context,
        attrs: AttributeSet? = null,
        defStyleAttr: Int = 0
    ): Boolean {
        val a = context.obtainStyledAttributes(attrs, R.styleable.SkinSupportable, defStyleAttr, 0)
        return try {
            a.hasValue(R.styleable.SkinSupportable_skinnable) && a.getBoolean(R.styleable.SkinSupportable_skinnable, false)
        } finally {
            a.recycle()
        }
    }

    private val SkinStrategy.key: String
        get() = "$skinName:${this::class.java.simpleName}"
}

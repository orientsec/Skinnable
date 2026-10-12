package com.util.skin.library.fallback

import android.content.Context
import android.content.res.ColorStateList
import android.content.res.XmlResourceParser
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.util.TypedValue
import androidx.annotation.AnyRes
import androidx.core.content.res.ResourcesCompat
import com.util.skin.library.helpers.SkinHelper.Companion.INVALID_ID
import com.util.skin.library.loader.ResourceLoader
import com.util.skin.library.res.SkinUserThemeManager
import com.util.skin.library.utils.Slog

/**
 * 【三级安全资源代理 (SafeSkinResources)】
 * 1. 代理 Layer 1（用户调色盘）-> Layer 2（外置/内置皮肤包插件资源）-> Layer 3（宿主原生默认资源）。
 * 2. 系统框架资源 (0x01xxxxxx) 与皮肤包未覆盖资源属于正常 Fallback，平滑降级到 Layer 3，不触发展示警告或错误计数；
 * 3. 仅当外置皮肤包抛出真实异常（如损坏文件解析失败、OOM）时触发错误记录与熔断计数，保证 View 渲染平滑且系统高可用。
 */
class SafeSkinResources(
    private var tracker: ISkinErrorTracker? = null,
    private var fallbackManager: SkinFallbackManager? = null
) {
    fun setTracker(tracker: ISkinErrorTracker?) {
        this.tracker = tracker
    }

    fun setFallbackManager(manager: SkinFallbackManager?) {
        this.fallbackManager = manager
    }

    private fun isSystemFrameworkResource(resId: Int): Boolean {
        // 系统 Framework 资源 package ID 为 0x01 (如 17171030 = 0x0106000d)，无需经过皮肤包查找
        return (resId ushr 24) == 0x01
    }

    /**
     * 一级代理：获取 Color 资源。
     */
    fun getColor(
        context: Context,
        @AnyRes resId: Int,
        loader: ResourceLoader?,
        strategyKey: String = ""
    ): Int {
        if (resId == INVALID_ID) return INVALID_ID

        // Layer 1: 用户自定义调色盘
        try {
            if (!SkinUserThemeManager.isColorEmpty) {
                SkinUserThemeManager.getColorStateList(resId)?.let {
                    return it.defaultColor
                }
            }
        } catch (t: Throwable) {
            logAndTrack(
                SkinErrorCategory.MISSING_RESOURCE,
                "Layer 1 UserTheme getColor failed for resId=$resId",
                resId, context, strategyKey, t
            )
        }

        // Layer 2: 外置/内置皮肤包插件资源 (非系统 0x01 框架资源)
        if (loader != null && !isSystemFrameworkResource(resId)) {
            try {
                val skinColor = loader.getColor(context, resId)
                if (skinColor != INVALID_ID) {
                    fallbackManager?.recordResourceSuccess()
                    return skinColor
                }
                // 返回 INVALID_ID 属于正常未覆盖资源，静默平滑降级至 Layer 3 宿主默认资源
            } catch (t: Throwable) {
                val category = if (t is OutOfMemoryError) SkinErrorCategory.OOM_DECODE else SkinErrorCategory.MISSING_RESOURCE
                logAndTrack(
                    category,
                    "Layer 2 Skin package getColor exception for resId=$resId in skin $strategyKey",
                    resId, context, strategyKey, t
                )
                fallbackManager?.recordResourceFailure("getColor exception for resId=$resId: $t")
            }
        }

        // Layer 3: 宿主默认 App 资源降级兜底
        return try {
            ResourcesCompat.getColor(context.resources, resId, context.theme)
        } catch (_: Throwable) {
            INVALID_ID
        }
    }

    /**
     * 一级代理：获取 ColorStateList 资源。
     */
    fun getColorStateList(
        context: Context,
        @AnyRes resId: Int,
        loader: ResourceLoader?,
        strategyKey: String = ""
    ): ColorStateList? {
        if (resId == INVALID_ID) return null

        // Layer 1
        try {
            if (!SkinUserThemeManager.isColorEmpty) {
                SkinUserThemeManager.getColorStateList(resId)?.let { return it }
            }
        } catch (t: Throwable) {
            logAndTrack(
                SkinErrorCategory.MISSING_RESOURCE,
                "Layer 1 UserTheme getColorStateList failed for resId=$resId",
                resId, context, strategyKey, t
            )
        }

        // Layer 2
        if (loader != null && !isSystemFrameworkResource(resId)) {
            try {
                val csl = loader.getColorStateList(context, resId)
                if (csl != null) {
                    fallbackManager?.recordResourceSuccess()
                    return csl
                }
                // 返回 null 属于正常未覆盖资源，静默平滑降级至 Layer 3 宿主默认资源
            } catch (t: Throwable) {
                val category = if (t is OutOfMemoryError) SkinErrorCategory.OOM_DECODE else SkinErrorCategory.MISSING_RESOURCE
                logAndTrack(
                    category,
                    "Layer 2 Skin package getColorStateList exception for resId=$resId in skin $strategyKey",
                    resId, context, strategyKey, t
                )
                fallbackManager?.recordResourceFailure("getColorStateList exception for resId=$resId: $t")
            }
        }

        // Layer 3
        return try {
            ResourcesCompat.getColorStateList(context.resources, resId, context.theme)
        } catch (_: Throwable) {
            null
        }
    }

    /**
     * 一级代理：获取 Drawable 资源。
     */
    fun getDrawable(
        context: Context,
        @AnyRes resId: Int,
        loader: ResourceLoader?,
        strategyKey: String = ""
    ): Drawable? {
        if (resId == INVALID_ID) return null

        // Layer 1
        try {
            if (!SkinUserThemeManager.isColorEmpty) {
                SkinUserThemeManager.getColorStateList(resId)?.let {
                    return ColorDrawable(it.defaultColor)
                }
            }
            if (!SkinUserThemeManager.isDrawableEmpty) {
                SkinUserThemeManager.getDrawable(resId)?.let { return it }
            }
        } catch (t: Throwable) {
            logAndTrack(
                SkinErrorCategory.MISSING_RESOURCE,
                "Layer 1 UserTheme getDrawable failed for resId=$resId",
                resId, context, strategyKey, t
            )
        }

        // Layer 2
        if (loader != null && !isSystemFrameworkResource(resId)) {
            try {
                val drawable = loader.getDrawable(context, resId)
                if (drawable != null) {
                    fallbackManager?.recordResourceSuccess()
                    return drawable
                }
                // 返回 null 属于正常未覆盖资源，静默平滑降级至 Layer 3 宿主默认资源
            } catch (t: Throwable) {
                val category = if (t is OutOfMemoryError) SkinErrorCategory.OOM_DECODE else SkinErrorCategory.MISSING_RESOURCE
                logAndTrack(
                    category,
                    "Layer 2 Skin package getDrawable exception for resId=$resId in skin $strategyKey",
                    resId, context, strategyKey, t
                )
                fallbackManager?.recordResourceFailure("getDrawable exception for resId=$resId: $t")
            }
        }

        // Layer 3
        return try {
            ResourcesCompat.getDrawable(context.resources, resId, context.theme)
        } catch (_: Throwable) {
            null
        }
    }

    /**
     * 一级代理：获取 XmlResourceParser 资源。
     */
    fun getXml(
        context: Context,
        @AnyRes resId: Int,
        loader: ResourceLoader?,
        strategyKey: String = ""
    ): XmlResourceParser? {
        if (resId == INVALID_ID) return null

        if (loader != null && !isSystemFrameworkResource(resId)) {
            try {
                val xml = loader.getXml(context, resId)
                if (xml != null) {
                    fallbackManager?.recordResourceSuccess()
                    return xml
                }
            } catch (t: Throwable) {
                logAndTrack(
                    SkinErrorCategory.MISSING_RESOURCE,
                    "Layer 2 Skin package getXml exception for resId=$resId in skin $strategyKey",
                    resId, context, strategyKey, t
                )
            }
        }

        return try {
            context.resources.getXml(resId)
        } catch (_: Throwable) {
            null
        }
    }

    /**
     * 一级代理：获取 TypedValue 资源。
     */
    fun getValue(
        context: Context,
        @AnyRes resId: Int,
        outValue: TypedValue,
        resolveRefs: Boolean,
        loader: ResourceLoader?,
        strategyKey: String = ""
    ) {
        if (resId == INVALID_ID) return

        if (loader != null && !isSystemFrameworkResource(resId)) {
            try {
                loader.getValue(context, resId, outValue, resolveRefs)
                fallbackManager?.recordResourceSuccess()
                return
            } catch (t: Throwable) {
                logAndTrack(
                    SkinErrorCategory.MISSING_RESOURCE,
                    "Layer 2 Skin package getValue exception for resId=$resId in skin $strategyKey",
                    resId, context, strategyKey, t
                )
            }
        }

        try {
            context.resources.getValue(resId, outValue, resolveRefs)
        } catch (_: Throwable) {
            // ignore
        }
    }

    private fun logAndTrack(
        category: SkinErrorCategory,
        message: String,
        resId: Int,
        context: Context,
        strategyKey: String,
        throwable: Throwable?
    ) {
        Slog.w("SafeSkinResources", message, throwable)
        val resName = try {
            if (resId != INVALID_ID) context.resources.getResourceEntryName(resId) else ""
        } catch (_: Throwable) {
            ""
        }
        tracker?.trackError(
            SkinErrorEvent(
                category = category,
                message = message,
                resId = resId,
                resName = resName,
                strategyKey = strategyKey,
                throwable = throwable
            )
        )
    }
}

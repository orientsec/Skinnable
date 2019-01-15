package com.util.skinnable.support.compat.res

import android.content.Context
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.content.res.ResourcesCompat
import com.util.skin.library.res.SkinResources
import com.util.skin.library.res.SkinResourcesManager
import com.util.skin.library.res.SkinUserThemeManager
import com.util.skinnable.support.compat.util.SkinCompatDrawableManager

object SkinCompatVectorResources : SkinResources {

    init {
        SkinResourcesManager.addSkinResources(this)
    }

    override fun clear() {
        SkinCompatDrawableManager.clearCaches()
    }

    private fun getSkinDrawableCompat(context: Context, resId: Int): Drawable? {
        if (AppCompatDelegate.isCompatVectorFromResourcesEnabled()) {
            if (!SkinResourcesManager.isDefaultSkin) {
                try {
                    return SkinCompatDrawableManager.getDrawable(context, resId)
                } catch (e: Exception) {
                    e.printStackTrace()
                }

            }
            // SkinCompatDrawableManager.get().getDrawable(context, resId) 中会调用getSkinDrawable等方法。
            // 这里只需要拦截使用默认皮肤的情况。
            if (!SkinUserThemeManager.isColorEmpty) {
                SkinUserThemeManager.getColorStateList(resId)
                    ?.apply {
                        return ColorDrawable(defaultColor)
                    }
            }
            if (!SkinUserThemeManager.isDrawableEmpty) {
                SkinUserThemeManager.getDrawable(resId)?.let {
                    return it
                }
            }
            return SkinResourcesManager.getStrategyDrawable(context, resId)
                ?: AppCompatResources.getDrawable(context, resId)
        } else {
            if (!SkinUserThemeManager.isColorEmpty) {
                SkinUserThemeManager.getColorStateList(resId)?.apply {
                    return ColorDrawable(defaultColor)
                }
            }
            if (!SkinUserThemeManager.isDrawableEmpty) {
                SkinUserThemeManager.getDrawable(resId)?.let {
                    return it
                }
            }
            SkinResourcesManager.getStrategyDrawable(context, resId)
                ?.let {
                    return it
                }
            if (!SkinResourcesManager.isDefaultSkin) {
                val targetResId = SkinResourcesManager.getTargetResId(context, resId)
                if (targetResId != 0) {
                    return SkinResourcesManager.skinResources?.run {
                        ResourcesCompat.getDrawable(this, targetResId, context.theme)
                    }
                }
            }
            return ResourcesCompat.getDrawable(context.resources, resId, context.theme)
        }
    }

    fun getDrawableCompat(context: Context, resId: Int): Drawable? {
        return getSkinDrawableCompat(context, resId)
    }
}

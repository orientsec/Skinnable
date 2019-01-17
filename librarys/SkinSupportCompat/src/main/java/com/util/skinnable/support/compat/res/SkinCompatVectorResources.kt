package com.util.skinnable.support.compat.res

import android.content.Context
import android.graphics.drawable.Drawable
import androidx.appcompat.app.AppCompatDelegate
import com.util.skin.library.res.SkinResources
import com.util.skin.library.res.SkinResourcesManager
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
        }
        return SkinResourcesManager.getDrawable(context, resId)
    }

    fun getDrawableCompat(context: Context, resId: Int): Drawable? {
        return getSkinDrawableCompat(context, resId)
    }
}

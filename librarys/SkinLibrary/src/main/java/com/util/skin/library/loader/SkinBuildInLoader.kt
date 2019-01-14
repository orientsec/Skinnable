package com.util.skin.library.loader

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.drawable.Drawable

import com.util.skin.library.res.SkinResourcesManager

class SkinBuildInLoader : SkinLoaderStrategy {

    override val type: SkinLoaderStrategyType
        get() = SkinLoaderStrategyType.BuildIn

    override fun loadSkinInBackground(context: Context, skinName: String): String? {
        SkinResourcesManager.setupSkin(
                context.resources,
                context.packageName,
                skinName,
                this)
        return skinName
    }

    override fun getTargetResourceEntryName(context: Context, skinName: String, resId: Int): String {
        return context.resources.getResourceEntryName(resId) + "_" + skinName
    }

    override fun getColor(context: Context, skinName: String, resId: Int): ColorStateList? {
        return null
    }

    override fun getColorStateList(context: Context, skinName: String, resId: Int): ColorStateList? {
        return null
    }

    override fun getDrawable(context: Context, skinName: String, resId: Int): Drawable? {
        return null
    }
}

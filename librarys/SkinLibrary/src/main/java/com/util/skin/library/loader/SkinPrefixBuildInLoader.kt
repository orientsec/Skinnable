package com.util.skin.library.loader

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.drawable.Drawable

import com.util.skin.library.res.SkinResourcesManager


class SkinPrefixBuildInLoader : SkinLoaderStrategy {

    override val type: SkinLoaderStrategyType
        get() = SkinLoaderStrategyType.PrefixBuildIn

    override fun loadSkinInBackground(context: Context, skinName: String): String? {
        SkinResourcesManager.setupSkin(
                context.resources,
                context.packageName,
                skinName,
                this)
        return skinName
    }

    override fun getTargetResourceEntryName(context: Context, skinName: String, resId: Int): String {
        return skinName + "_" + context.resources.getResourceEntryName(resId)
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

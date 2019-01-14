package com.util.skin.library.loader

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.drawable.Drawable

class SkinNoneLoader : SkinLoaderStrategy {

    override val type: SkinLoaderStrategyType
        get() = SkinLoaderStrategyType.Default

    override fun loadSkinInBackground(context: Context, skinName: String): String? {
        return ""
    }

    override fun getTargetResourceEntryName(context: Context, skinName: String, resId: Int): String {
        return ""
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

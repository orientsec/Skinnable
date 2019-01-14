package com.util.skin.library.loader

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.drawable.Drawable
import android.text.TextUtils

import com.util.skin.library.SkinManager
import com.util.skin.library.res.SkinResourcesManager
import com.util.skin.library.utils.SkinFileUtils

abstract class SkinSDCardLoader : SkinLoaderStrategy {
    override fun loadSkinInBackground(context: Context, skinName: String): String? {
        if (TextUtils.isEmpty(skinName)) {
            return skinName
        }
        val skinPkgPath = getSkinPath(context, skinName)
        if (SkinFileUtils.isFileExists(skinPkgPath)) {
            val pkgName = SkinManager.getSkinPackageName(skinPkgPath)
            val resources = SkinManager.getSkinResources(skinPkgPath)
            if (resources != null && !TextUtils.isEmpty(pkgName)) {
                SkinResourcesManager.setupSkin(
                        resources,
                        pkgName,
                        skinName,
                        this)
                return skinName
            }
        }
        return null
    }

    protected abstract fun getSkinPath(context: Context, skinName: String): String

    override fun getTargetResourceEntryName(context: Context, skinName: String, resId: Int): String? {
        return null
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

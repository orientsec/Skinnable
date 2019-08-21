package com.util.skin.library.loader

import android.content.Context
import android.text.TextUtils
import com.util.skin.library.SkinManager
import com.util.skin.library.model.SkinResModel
import com.util.skin.library.utils.isFileExists

/**
 * SD卡中加载器
 */
abstract class SkinSDCardLoader : BaseSkinLoaderStrategy() {
    override fun initLoader(context: Context, skinName: String): String? {
        if (TextUtils.isEmpty(skinName)) {
            return skinName
        }
        val skinPkgPath = getSkinPath(context, skinName)
        if (isFileExists(skinPkgPath)) {
            val pkgName = SkinManager.getSkinPackageName(skinPkgPath)
            val resources = SkinManager.getSkinResources(skinPkgPath)
            if (resources != null && !TextUtils.isEmpty(pkgName)) {
                resModel = SkinResModel(resources, pkgName, skinName)
                return skinName
            }
        }
        return null
    }

    protected abstract fun getSkinPath(context: Context, skinName: String): String

    override fun getTargetResourceEntryName(context: Context, skinName: String, resId: Int): String? {
        return null
    }

}

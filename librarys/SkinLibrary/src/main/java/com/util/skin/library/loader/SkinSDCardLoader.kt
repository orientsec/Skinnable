package com.util.skin.library.loader

import android.content.Context
import android.content.pm.PackageManager
import android.content.res.Resources
import android.text.TextUtils
import com.util.skin.library.model.SkinResource
import com.util.skin.library.model.appContext
import com.util.skin.library.utils.isFileExists

/**
 * SD卡中加载器
 */
abstract class SkinSDCardLoader(protected val path: String) : SkinLoaderStrategy {
    override fun initStrategy(context: Context): SkinResource? {
        if (TextUtils.isEmpty(path)) {
            return null
        }
        val skinPkgPath = getSkinPath(context, path)
        if (isFileExists(skinPkgPath)) {
            val pkgName = getSkinPackageName(skinPkgPath)
            val resources = getSkinResources(skinPkgPath)
            if (resources != null && !TextUtils.isEmpty(pkgName)) {
                return SkinResource(resources, pkgName, path)
            }
        }
        return null
    }

    protected abstract fun getSkinPath(context: Context, skinName: String): String

    override fun getTargetResourceEntryName(
        context: Context,
        resId: Int
    ): String? = null

    /**
     * 获取皮肤包包名.
     *
     * @param skinPkgPath sdcard中皮肤包路径.
     * @return
     */
    private fun getSkinPackageName(skinPkgPath: String): String {
        return appContext.packageManager
            .getPackageArchiveInfo(skinPkgPath, PackageManager.GET_ACTIVITIES)
            ?.packageName ?: ""
    }

    /**
     * 获取皮肤包资源[Resources].
     *
     * @param skinPkgPath sdcard中皮肤包路径.
     * @return
     */
    private fun getSkinResources(skinPkgPath: String): Resources? {
        try {
            val packageInfo =
                appContext.packageManager.getPackageArchiveInfo(skinPkgPath, 0) ?: return null
            packageInfo.applicationInfo.sourceDir = skinPkgPath
            packageInfo.applicationInfo.publicSourceDir = skinPkgPath
            val res =
                appContext.packageManager.getResourcesForApplication(packageInfo.applicationInfo)
            val superRes = appContext.resources
            return Resources(res.assets, superRes.displayMetrics, superRes.configuration)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return null
    }
}

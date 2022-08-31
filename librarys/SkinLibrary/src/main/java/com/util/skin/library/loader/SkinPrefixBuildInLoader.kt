package com.util.skin.library.loader

import android.content.Context
import com.util.skin.library.model.SkinResource


internal class SkinPrefixBuildInLoader(private val skinName: String) : SkinLoaderStrategy {

    override fun initStrategy(context: Context): SkinResource {
        return SkinResource(
            context.resources,
            context.packageName,
            skinName
        )
    }

    override fun getTargetResourceEntryName(
        context: Context,
        resId: Int
    ): String {
        return skinName + "_" + context.resources.getResourceEntryName(resId)
    }
}

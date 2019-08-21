package com.util.skin.library.loader

import android.content.Context
import com.util.skin.library.model.SkinResModel


class SkinPrefixBuildInLoader : BaseSkinLoaderStrategy() {

    override val type: SkinLoaderStrategyType
        get() = SkinLoaderStrategyType.PrefixBuildIn

    override fun initLoader(context: Context, skinName: String): String? {
        resModel = SkinResModel(
            context.resources,
            context.packageName,
            skinName
        )
        return skinName
    }

    override fun getTargetResourceEntryName(context: Context, skinName: String, resId: Int): String {
        return skinName + "_" + context.resources.getResourceEntryName(resId)
    }
}

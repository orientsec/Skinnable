package com.util.skin.library.model

import android.content.res.Resources
import com.util.skin.library.loader.SkinLoaderStrategyType

/**
 * @PackageName com.util.skin.library.model
 * @date 2019/1/18 9:10
 * @author zhanglei
 */
data class SkinResModel(
    val resources: Resources,
    val pkgName: String,
    val skinName: String
)

internal data class ResEntry(val skinName: String, val strategyType: SkinLoaderStrategyType)
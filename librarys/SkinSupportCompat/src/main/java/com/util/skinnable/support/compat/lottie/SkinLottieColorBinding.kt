package com.util.skinnable.support.compat.lottie

import com.airbnb.lottie.model.KeyPath
import com.util.skin.library.helpers.SkinHelper

/**
 * Lottie 动画 KeyPath 色值动态注入规则数据结构
 */
data class SkinLottieColorBinding(
    val keyPath: KeyPath,
    val colorResId: Int = SkinHelper.INVALID_ID,
    val colorAttrId: Int = SkinHelper.INVALID_ID
)

package com.util.skinnable.support.compat.lottie

import androidx.annotation.AttrRes
import androidx.annotation.ColorRes
import androidx.annotation.RawRes
import com.util.skin.library.helpers.SkinHelper

/**
 * 注解：用于在 View / Activity / Fragment / ViewHolder 中非侵入声明 [com.airbnb.lottie.LottieAnimationView] 的换肤规则。
 */
@Target(AnnotationTarget.FIELD, AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
annotation class LottieSkin(
    @RawRes val rawResId: Int = SkinHelper.INVALID_ID,
    val keyPath: String = "**",
    @ColorRes val colorResId: Int = SkinHelper.INVALID_ID,
    @AttrRes val colorAttrId: Int = SkinHelper.INVALID_ID,
    val autoColor: Boolean = false,
    val enableImageAssetDelegate: Boolean = true
)

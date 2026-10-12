package com.util.skinnable.support.compat.lottie

import android.content.Context
import android.util.AttributeSet
import androidx.annotation.RawRes
import com.airbnb.lottie.LottieAnimationView
import com.airbnb.lottie.model.KeyPath
import com.util.skin.library.helpers.SkinHelper
import com.util.skin.library.res.SkinResourcesManager
import com.util.skin.library.widget.SkinSupportable
import com.util.skinnable.support.compat.helpers.SkinBackgroundHelper

/**
 * 支持无缝换肤的 [LottieAnimationView] 控件。
 * 能够响应换肤事件，动态替换图层图片（ImageAssetDelegate）、按 KeyPath 注入主题色 ColorFilter 以及重新加载皮肤 Raw 动画。
 */
class SkinLottieAnimationView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : LottieAnimationView(context, attrs, defStyleAttr), SkinSupportable {

    private val mBackgroundTintHelper = SkinBackgroundHelper(this)
    private val mLottieHelper = SkinLottieHelper(this)

    override val skinnable: Boolean by lazy {
        SkinResourcesManager.parseSkinnable(context, attrs, defStyleAttr)
    }

    init {
        mBackgroundTintHelper.loadFromAttributes(attrs, defStyleAttr)
        mLottieHelper.loadFromAttributes(attrs, defStyleAttr)
    }

    override fun setBackgroundResource(resId: Int) {
        super.setBackgroundResource(resId)
        mBackgroundTintHelper.setSrcId(resId)
    }

    fun setLottieSkinRawRes(@RawRes rawResId: Int) {
        mLottieHelper.setRawResId(rawResId)
    }

    fun addKeyPathColorBinding(
        keyPath: KeyPath,
        colorResId: Int = SkinHelper.INVALID_ID,
        colorAttrId: Int = SkinHelper.INVALID_ID
    ) {
        mLottieHelper.addKeyPathColorBinding(keyPath, colorResId, colorAttrId)
    }

    fun addKeyPathColorBinding(
        keyPathStr: String,
        colorResId: Int = SkinHelper.INVALID_ID,
        colorAttrId: Int = SkinHelper.INVALID_ID
    ) {
        mLottieHelper.addKeyPathColorBinding(keyPathStr, colorResId, colorAttrId)
    }

    fun setImageAssetDelegateEnabled(enabled: Boolean) {
        mLottieHelper.setImageAssetDelegateEnabled(enabled)
    }

    override fun applySkin() {
        mBackgroundTintHelper.applySkin()
        mLottieHelper.applySkin()
    }
}

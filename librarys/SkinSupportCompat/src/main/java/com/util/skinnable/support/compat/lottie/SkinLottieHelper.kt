package com.util.skinnable.support.compat.lottie

import android.content.res.TypedArray
import android.util.AttributeSet
import com.airbnb.lottie.LottieAnimationView
import com.airbnb.lottie.LottieProperty
import com.airbnb.lottie.SimpleColorFilter
import com.airbnb.lottie.model.KeyPath
import com.airbnb.lottie.value.LottieValueCallback
import com.util.skin.library.helpers.SkinHelper
import com.util.skin.library.res.SkinResourcesManager
import com.util.skin.library.res.SkinThemeUtils
import com.util.skinnable.support.compat.R

class SkinLottieHelper(view: LottieAnimationView) : SkinHelper(view) {

    private var mRawResId = INVALID_ID
    private var mColorResId = INVALID_ID
    private var mColorAttrId = INVALID_ID
    private var mKeyPathStr: String = "**"
    private var mAutoColor: Boolean = false
    private var mEnableImageAssetDelegate: Boolean = true

    private val mColorBindings = mutableListOf<SkinLottieColorBinding>()

    override val mView: LottieAnimationView
        get() = super.mView as LottieAnimationView

    override fun loadFromAttributes(attrs: AttributeSet?, defStyleAttr: Int) {
        var a: TypedArray? = null
        try {
            a = mView.context.obtainStyledAttributes(
                attrs,
                R.styleable.SkinLottieAnimationView,
                defStyleAttr,
                0
            )
            mRawResId = a.getResourceId(
                R.styleable.SkinLottieAnimationView_skin_lottie_rawRes,
                INVALID_ID
            )
            mColorResId = a.getResourceId(
                R.styleable.SkinLottieAnimationView_skin_lottie_color_res,
                INVALID_ID
            )
            mColorAttrId = a.getResourceId(
                R.styleable.SkinLottieAnimationView_skin_lottie_color_attr,
                INVALID_ID
            )
            a.getString(R.styleable.SkinLottieAnimationView_skin_lottie_color_path)?.let { path ->
                if (path.isNotEmpty()) {
                    mKeyPathStr = path
                }
            }
            mAutoColor = a.getBoolean(
                R.styleable.SkinLottieAnimationView_skin_lottie_auto_color,
                false
            )
            mEnableImageAssetDelegate = a.getBoolean(
                R.styleable.SkinLottieAnimationView_skin_lottie_enable_image_delegate,
                true
            )
        } finally {
            a?.recycle()
        }

        if (checkResourceIdValid(mColorResId) || checkResourceIdValid(mColorAttrId)) {
            val keys = mKeyPathStr.split("/").filter { it.isNotEmpty() }.toTypedArray()
            addKeyPathColorBinding(KeyPath(*keys), mColorResId, mColorAttrId)
        }

        if (mAutoColor && mColorBindings.isEmpty()) {
            addKeyPathColorBinding(
                KeyPath("**"),
                colorResId = INVALID_ID,
                colorAttrId = androidx.appcompat.R.attr.colorPrimary
            )
        }

        applySkin()
    }

    fun setRawResId(rawResId: Int) {
        mRawResId = rawResId
        applySkin()
    }

    fun addKeyPathColorBinding(
        keyPath: KeyPath,
        colorResId: Int = INVALID_ID,
        colorAttrId: Int = INVALID_ID
    ) {
        mColorBindings.add(SkinLottieColorBinding(keyPath, colorResId, colorAttrId))
    }

    fun addKeyPathColorBinding(
        keyPathStr: String,
        colorResId: Int = INVALID_ID,
        colorAttrId: Int = INVALID_ID
    ) {
        val keys = keyPathStr.split("/").filter { it.isNotEmpty() }.toTypedArray()
        addKeyPathColorBinding(KeyPath(*keys), colorResId, colorAttrId)
    }

    fun setImageAssetDelegateEnabled(enabled: Boolean) {
        mEnableImageAssetDelegate = enabled
        applySkin()
    }

    override fun applySkin() {
        if (mEnableImageAssetDelegate) {
            mView.setImageAssetDelegate(SkinLottieImageAssetDelegate(mView.context))
        }

        if (checkResourceIdValid(mRawResId)) {
            try {
                mView.setAnimation(mRawResId)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        for (binding in mColorBindings) {
            var targetColor: Int? = null

            if (checkResourceIdValid(binding.colorResId)) {
                targetColor = SkinResourcesManager.getColor(mView.context, binding.colorResId)
            } else if (checkResourceIdValid(binding.colorAttrId)) {
                targetColor = SkinThemeUtils.getThemeAttrColor(mView.context, binding.colorAttrId)
            }

            if (targetColor != null) {
                mView.addValueCallback(
                    binding.keyPath,
                    LottieProperty.COLOR_FILTER,
                    LottieValueCallback(SimpleColorFilter(targetColor))
                )
            }
        }
    }
}

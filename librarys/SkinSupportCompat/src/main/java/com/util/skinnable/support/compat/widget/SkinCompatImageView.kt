package com.util.skinnable.support.compat.widget

import android.content.Context
import android.util.AttributeSet
import androidx.annotation.DrawableRes
import androidx.appcompat.widget.AppCompatImageView
import com.util.skin.library.res.SkinResourcesManager
import com.util.skinnable.support.compat.helpers.SkinBackgroundHelper
import com.util.skinnable.support.compat.helpers.SkinImageHelper
import com.util.skin.library.widget.SkinSupportable

class SkinCompatImageView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : AppCompatImageView(context, attrs, defStyleAttr), SkinSupportable {
    private val mBackgroundTintHelper= SkinBackgroundHelper(this)
    private val mImageHelper = SkinImageHelper(this)
    override val skinnable: Boolean by lazy {
        return@lazy SkinResourcesManager.parseSkinnable(
            context,
            attrs,
            defStyleAttr
        )
    }

    init {
        mBackgroundTintHelper.loadFromAttributes(attrs, defStyleAttr)
        mImageHelper.loadFromAttributes(attrs, defStyleAttr)
    }

    override fun setBackgroundResource(@DrawableRes resId: Int) {
        super.setBackgroundResource(resId)
        mBackgroundTintHelper.setSrcId(resId)
    }

    override fun setImageResource(@DrawableRes resId: Int) {
        // Intercept this call and instead retrieve the Drawable via the image helper
        mImageHelper.setSrcId(resId)
    }

    override fun applySkin() {
        mBackgroundTintHelper.applySkin()
        mImageHelper.applySkin()
    }

}

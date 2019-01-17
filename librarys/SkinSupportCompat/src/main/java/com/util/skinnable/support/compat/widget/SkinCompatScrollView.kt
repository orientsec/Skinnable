package com.util.skinnable.support.compat.widget

import android.content.Context
import android.util.AttributeSet
import android.widget.ScrollView
import androidx.annotation.DrawableRes
import com.util.skin.library.res.SkinResourcesManager
import com.util.skinnable.support.compat.helpers.SkinBackgroundHelper
import com.util.skin.library.widget.SkinSupportable

class SkinCompatScrollView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : ScrollView(context, attrs, defStyleAttr), SkinSupportable {
    private val mBackgroundTintHelper= SkinBackgroundHelper(this)
    override val skinnable: Boolean by lazy {
        return@lazy SkinResourcesManager.parseSkinnable(
            context,
            attrs,
            defStyleAttr
        )
    }

    init {
        mBackgroundTintHelper.loadFromAttributes(attrs, defStyleAttr)
    }

    override fun setBackgroundResource(@DrawableRes resId: Int) {
        super.setBackgroundResource(resId)
        mBackgroundTintHelper.setSrcId(resId)
    }

    override fun applySkin() {
        mBackgroundTintHelper.applySkin()
    }

}

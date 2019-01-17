package com.util.skinnable.support.design.widget

import android.content.Context
import android.util.AttributeSet
import com.google.android.material.appbar.AppBarLayout
import com.util.skin.library.res.SkinResourcesManager
import com.util.skin.library.widget.SkinSupportable
import com.util.skinnable.support.compat.helpers.SkinBackgroundHelper

class SkinMaterialAppBarLayout @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null) :
    AppBarLayout(context, attrs), SkinSupportable {
    private val mBackgroundTintHelper = SkinBackgroundHelper(this)
    override val skinnable: Boolean by lazy {
        return@lazy SkinResourcesManager.parseSkinnable(
            context,
            attrs
        )
    }

    init {
        mBackgroundTintHelper.loadFromAttributes(attrs, 0)
    }

    override fun applySkin() {
        mBackgroundTintHelper.applySkin()
    }

}

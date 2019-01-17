package com.util.skinnable.support.compat.widget

import android.content.Context
import android.util.AttributeSet
import android.widget.RadioGroup
import com.util.skin.library.res.SkinResourcesManager
import com.util.skin.library.widget.SkinSupportable
import com.util.skinnable.support.compat.helpers.SkinBackgroundHelper

class SkinCompatRadioGroup @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null) :
    RadioGroup(context, attrs), SkinSupportable {
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

    override fun setBackgroundResource(resId: Int) {
        super.setBackgroundResource(resId)
        mBackgroundTintHelper.setSrcId(resId)
    }

    override fun applySkin() {
        mBackgroundTintHelper.applySkin()
    }
}

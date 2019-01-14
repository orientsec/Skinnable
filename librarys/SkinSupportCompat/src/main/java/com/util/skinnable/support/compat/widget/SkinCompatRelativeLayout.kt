package com.util.skinnable.support.compat.widget

import android.content.Context
import android.util.AttributeSet
import android.widget.RelativeLayout
import com.util.skinnable.support.compat.helpers.SkinCompatBackgroundHelper
import com.util.skin.library.widget.SkinSupportable

class SkinCompatRelativeLayout @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : RelativeLayout(context, attrs, defStyleAttr), SkinSupportable {
    private val mBackgroundTintHelper= SkinCompatBackgroundHelper(this)

    init {
        mBackgroundTintHelper.loadFromAttributes(attrs, defStyleAttr)
    }

    override fun setBackgroundResource(resId: Int) {
        super.setBackgroundResource(resId)
        mBackgroundTintHelper.onSetBackgroundResource(resId)
    }

    override fun applySkin() {
        mBackgroundTintHelper.applySkin()
    }

}

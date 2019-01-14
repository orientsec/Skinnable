package com.util.skinnable.support.design.widget

import android.content.Context
import android.util.AttributeSet
import androidx.coordinatorlayout.widget.CoordinatorLayout
import com.util.skin.library.widget.SkinSupportable
import com.util.skinnable.support.compat.helpers.SkinCompatBackgroundHelper

class SkinMaterialCoordinatorLayout @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : CoordinatorLayout(context, attrs, defStyleAttr), SkinSupportable {

    private val mBackgroundTintHelper = SkinCompatBackgroundHelper(this)

    init {
        mBackgroundTintHelper.loadFromAttributes(attrs, 0)
    }

    override fun applySkin() {
        mBackgroundTintHelper.applySkin()
    }

}

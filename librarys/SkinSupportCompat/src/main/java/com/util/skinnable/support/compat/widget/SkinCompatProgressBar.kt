package com.util.skinnable.support.compat.widget

import android.content.Context
import android.util.AttributeSet
import android.widget.ProgressBar
import com.util.skin.library.res.SkinResourcesManager
import com.util.skinnable.support.compat.helpers.SkinProgressBarHelper
import com.util.skin.library.widget.SkinSupportable

class SkinCompatProgressBar @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = android.R.attr.progressBarStyle
) : ProgressBar(context, attrs, defStyleAttr), SkinSupportable {
    private val mSkinCompatProgressBarHelper = SkinProgressBarHelper(this)
    override val skinnable: Boolean by lazy {
        return@lazy SkinResourcesManager.parseSkinnable(
            context,
            attrs,
            defStyleAttr
        )
    }

    init {
        mSkinCompatProgressBarHelper.loadFromAttributes(attrs, defStyleAttr)
    }

    override fun applySkin() {
        mSkinCompatProgressBarHelper.applySkin()
    }

}

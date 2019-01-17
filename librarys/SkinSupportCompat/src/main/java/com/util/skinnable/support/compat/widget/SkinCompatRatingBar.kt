package com.util.skinnable.support.compat.widget

import android.content.Context
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatRatingBar
import com.util.skin.library.res.SkinResourcesManager
import com.util.skin.library.widget.SkinSupportable
import com.util.skinnable.support.compat.R
import com.util.skinnable.support.compat.helpers.SkinProgressBarHelper

class SkinCompatRatingBar @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = R.attr.ratingBarStyle
) : AppCompatRatingBar(context, attrs, defStyleAttr), SkinSupportable {
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

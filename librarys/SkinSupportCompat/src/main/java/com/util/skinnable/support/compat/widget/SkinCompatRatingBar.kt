package com.util.skinnable.support.compat.widget

import android.content.Context
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatRatingBar
import com.util.skin.library.R
import com.util.skinnable.support.compat.helpers.SkinCompatProgressBarHelper
import com.util.skin.library.widget.SkinSupportable

class SkinCompatRatingBar @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = R.attr.ratingBarStyle
) : AppCompatRatingBar(context, attrs, defStyleAttr), SkinSupportable {
    private val mSkinCompatProgressBarHelper = SkinCompatProgressBarHelper(this)

    init {
        mSkinCompatProgressBarHelper!!.loadFromAttributes(attrs, defStyleAttr)
    }

    override fun applySkin() {
        mSkinCompatProgressBarHelper?.applySkin()
    }

}

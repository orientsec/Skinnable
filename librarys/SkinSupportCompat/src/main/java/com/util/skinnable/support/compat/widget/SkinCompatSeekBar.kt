package com.util.skinnable.support.compat.widget

import android.content.Context
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatSeekBar
import com.util.skin.library.R
import com.util.skin.library.widget.SkinSupportable
import com.util.skinnable.support.compat.helpers.SkinCompatSeekBarHelper

class SkinCompatSeekBar @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = R.attr.seekBarStyle
) : AppCompatSeekBar(context, attrs, defStyleAttr), SkinSupportable {
    private val mSkinCompatSeekBarHelper= SkinCompatSeekBarHelper(this)

    init {
        mSkinCompatSeekBarHelper.loadFromAttributes(attrs, defStyleAttr)
    }


    override fun applySkin() {
        mSkinCompatSeekBarHelper.applySkin()
    }

}

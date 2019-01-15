package com.util.skinnable.support.compat.widget

import android.content.Context
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatSeekBar
import com.util.skin.library.widget.SkinSupportable
import com.util.skinnable.support.compat.R
import com.util.skinnable.support.compat.helpers.SkinSeekBarHelper

class SkinCompatSeekBar @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = R.attr.seekBarStyle
) : AppCompatSeekBar(context, attrs, defStyleAttr), SkinSupportable {
    private val mSkinCompatSeekBarHelper= SkinSeekBarHelper(this)

    init {
        mSkinCompatSeekBarHelper.loadFromAttributes(attrs, defStyleAttr)
    }


    override fun applySkin() {
        mSkinCompatSeekBarHelper.applySkin()
    }

}

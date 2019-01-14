package com.util.skinnable.support.design.widget

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.util.skin.library.helpers.SkinHelper
import com.util.skin.library.helpers.SkinHelper.Companion.INVALID_ID
import com.util.skin.library.widget.SkinSupportable
import com.util.skinnable.support.compat.helpers.SkinCompatImageHelper
import com.util.skin.library.res.SkinResourcesManager
import com.util.skinnable.support.design.R

@SuppressLint("CustomViewStyleable", "PrivateResource")
class SkinMaterialFloatingActionButton @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : FloatingActionButton(context, attrs, defStyleAttr), SkinSupportable {
    private var mRippleColorResId = INVALID_ID
    private var mBackgroundTintResId = INVALID_ID

    private val mImageHelper = SkinCompatImageHelper(this)

    init {
        val a = context.obtainStyledAttributes(
            attrs,
            R.styleable.FloatingActionButton, defStyleAttr,
            R.style.Widget_Design_FloatingActionButton
        )
        mBackgroundTintResId = a.getResourceId(R.styleable.FloatingActionButton_backgroundTint, INVALID_ID)
        mRippleColorResId = a.getResourceId(R.styleable.FloatingActionButton_rippleColor, INVALID_ID)
        a.recycle()
        applyBackgroundTintResource()
        applyRippleColorResource()

        mImageHelper.loadFromAttributes(attrs, defStyleAttr)
    }

    private fun applyBackgroundTintResource() {
        if (SkinHelper.checkResourceIdValid(mBackgroundTintResId)) {
            backgroundTintList = SkinResourcesManager.getColorStateList(context, mBackgroundTintResId)
        }
    }

    private fun applyRippleColorResource() {
        if (SkinHelper.checkResourceIdValid(mRippleColorResId)) {
            rippleColor = SkinResourcesManager.getColor(context, mRippleColorResId)
        }
    }

    override fun applySkin() {
        applyBackgroundTintResource()
        applyRippleColorResource()
        mImageHelper.applySkin()
    }

}

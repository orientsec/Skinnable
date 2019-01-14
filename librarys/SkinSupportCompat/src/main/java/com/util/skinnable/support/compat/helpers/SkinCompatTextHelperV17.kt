package com.util.skinnable.support.compat.helpers

import android.annotation.TargetApi
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.widget.TextView
import androidx.annotation.DrawableRes
import androidx.annotation.RequiresApi
import com.util.skin.library.R
import com.util.skin.library.helpers.SkinHelper
import com.util.skinnable.support.compat.res.SkinCompatVectorResources

@RequiresApi(17)
@TargetApi(17)
class SkinCompatTextHelperV17(view: TextView) : SkinCompatTextHelper(view) {
    private var mDrawableStartResId = INVALID_ID
    private var mDrawableEndResId = INVALID_ID

    override fun loadFromAttributes(attrs: AttributeSet?, defStyleAttr: Int) {
        val context = mView.context

        val a = context.obtainStyledAttributes(
            attrs, R.styleable.SkinCompatTextHelper,
            defStyleAttr, 0
        )
        if (a.hasValue(R.styleable.SkinCompatTextHelper_android_drawableStart)) {
            mDrawableStartResId = a.getResourceId(R.styleable.SkinCompatTextHelper_android_drawableStart, INVALID_ID)
        }
        if (a.hasValue(R.styleable.SkinCompatTextHelper_android_drawableEnd)) {
            mDrawableEndResId = a.getResourceId(R.styleable.SkinCompatTextHelper_android_drawableEnd, INVALID_ID)
        }
        a.recycle()
        super.loadFromAttributes(attrs, defStyleAttr)
    }

    override fun onSetCompoundDrawablesRelativeWithIntrinsicBounds(
        @DrawableRes start: Int, @DrawableRes top: Int, @DrawableRes end: Int, @DrawableRes bottom: Int
    ) {
        mDrawableStartResId = start
        mDrawableTopResId = top
        mDrawableEndResId = end
        mDrawableBottomResId = bottom
        applyCompoundDrawablesRelativeResource()
    }

    override fun applyCompoundDrawablesRelativeResource() {
        var drawableLeft: Drawable? = null
        var drawableTop: Drawable? = null
        var drawableRight: Drawable? = null
        var drawableBottom: Drawable? = null
        var drawableStart: Drawable? = null
        var drawableEnd: Drawable? = null
        if (checkResourceIdValid(mDrawableLeftResId)) {
            drawableLeft = SkinCompatVectorResources.getDrawableCompat(mView.context, mDrawableLeftResId)
        }
        if (checkResourceIdValid(mDrawableTopResId)) {
            drawableTop = SkinCompatVectorResources.getDrawableCompat(mView.context, mDrawableTopResId)
        }
        if (checkResourceIdValid(mDrawableRightResId)) {
            drawableRight = SkinCompatVectorResources.getDrawableCompat(mView.context, mDrawableRightResId)
        }
        if (checkResourceIdValid(mDrawableBottomResId)) {
            drawableBottom = SkinCompatVectorResources.getDrawableCompat(mView.context, mDrawableBottomResId)
        }
        if (SkinHelper.checkResourceIdValid(mDrawableStartResId)) {
            drawableStart = SkinCompatVectorResources.getDrawableCompat(mView.context, mDrawableStartResId)
        }
        if (drawableStart == null) {
            drawableStart = drawableLeft
        }
        if (SkinHelper.checkResourceIdValid(mDrawableEndResId)) {
            drawableEnd = SkinCompatVectorResources.getDrawableCompat(mView.context, mDrawableEndResId)
        }
        if (drawableEnd == null) {
            drawableEnd = drawableRight
        }
        if (mDrawableLeftResId != INVALID_ID
            || mDrawableTopResId != INVALID_ID
            || mDrawableRightResId != INVALID_ID
            || mDrawableBottomResId != INVALID_ID
            || mDrawableStartResId != INVALID_ID
            || mDrawableEndResId != INVALID_ID
        ) {
            mView.setCompoundDrawablesWithIntrinsicBounds(drawableStart, drawableTop, drawableEnd, drawableBottom)
        }
    }
}

package com.util.skinnable.support.design.widget

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import com.google.android.material.card.MaterialCardView
import com.util.skin.library.helpers.SkinHelper
import com.util.skin.library.helpers.SkinHelper.Companion.INVALID_ID
import com.util.skin.library.res.SkinResourcesManager
import com.util.skin.library.widget.SkinSupportable
import com.util.skinnable.support.compat.helpers.SkinBackgroundHelper
import com.util.skinnable.support.design.R

/**
 * 支持换肤的 MaterialCardView / CardView 控件。
 * 能够自动捕获并替换 app:cardBackgroundColor、app:strokeColor、app:rippleColor 以及背景样式。
 */
@SuppressLint("CustomViewStyleable", "PrivateResource")
class SkinMaterialCardView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = com.google.android.material.R.attr.materialCardViewStyle
) : MaterialCardView(context, attrs, defStyleAttr), SkinSupportable {

    private var mCardBackgroundColorResId = INVALID_ID
    private var mStrokeColorResId = INVALID_ID
    private var mRippleColorResId = INVALID_ID
    private val mBackgroundHelper = SkinBackgroundHelper(this)

    override val skinnable: Boolean by lazy {
        return@lazy SkinResourcesManager.parseSkinnable(
            context,
            attrs,
            defStyleAttr
        )
    }

    init {
        mBackgroundHelper.loadFromAttributes(attrs, defStyleAttr)

        val a = context.obtainStyledAttributes(
            attrs,
            R.styleable.CardView,
            defStyleAttr,
            R.style.Widget_MaterialComponents_CardView
        )
        if (a.hasValue(R.styleable.CardView_cardBackgroundColor)) {
            mCardBackgroundColorResId = a.getResourceId(R.styleable.CardView_cardBackgroundColor, INVALID_ID)
        }
        a.recycle()

        val ma = context.obtainStyledAttributes(
            attrs,
            R.styleable.MaterialCardView,
            defStyleAttr,
            R.style.Widget_MaterialComponents_CardView
        )
        if (ma.hasValue(R.styleable.MaterialCardView_strokeColor)) {
            mStrokeColorResId = ma.getResourceId(R.styleable.MaterialCardView_strokeColor, INVALID_ID)
        }
        if (ma.hasValue(R.styleable.MaterialCardView_rippleColor)) {
            mRippleColorResId = ma.getResourceId(R.styleable.MaterialCardView_rippleColor, INVALID_ID)
        }
        ma.recycle()

        applySkin()
    }

    override fun applySkin() {
        mBackgroundHelper.applySkin()

        if (SkinHelper.checkResourceIdValid(mCardBackgroundColorResId)) {
            val csl = SkinResourcesManager.getColorStateList(context, mCardBackgroundColorResId)
            if (csl != null) {
                setCardBackgroundColor(csl)
            }
        }

        if (SkinHelper.checkResourceIdValid(mStrokeColorResId)) {
            val csl = SkinResourcesManager.getColorStateList(context, mStrokeColorResId)
            if (csl != null) {
                setStrokeColor(csl)
            }
        }

        if (SkinHelper.checkResourceIdValid(mRippleColorResId)) {
            val csl = SkinResourcesManager.getColorStateList(context, mRippleColorResId)
            if (csl != null) {
                rippleColor = csl
            }
        }
    }
}

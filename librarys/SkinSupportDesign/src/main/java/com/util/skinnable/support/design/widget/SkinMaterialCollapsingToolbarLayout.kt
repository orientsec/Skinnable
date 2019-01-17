package com.util.skinnable.support.design.widget

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import com.google.android.material.appbar.CollapsingToolbarLayout
import com.util.skin.library.helpers.SkinHelper
import com.util.skin.library.helpers.SkinHelper.Companion.INVALID_ID
import com.util.skin.library.res.SkinResourcesManager
import com.util.skin.library.widget.SkinSupportable
import com.util.skinnable.support.compat.helpers.SkinBackgroundHelper
import com.util.skinnable.support.compat.res.SkinCompatVectorResources
import com.util.skinnable.support.design.R

@SuppressLint("CustomViewStyleable", "PrivateResource")
class SkinMaterialCollapsingToolbarLayout @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : CollapsingToolbarLayout(context, attrs, defStyleAttr), SkinSupportable {
    private var mContentScrimResId = INVALID_ID
    private var mStatusBarScrimResId = INVALID_ID
    private val mBackgroundTintHelper = SkinBackgroundHelper(this)
    override val skinnable: Boolean by lazy {
        return@lazy SkinResourcesManager.parseSkinnable(
            context,
            attrs,
            defStyleAttr
        )
    }

    init {

        val a = context.obtainStyledAttributes(
            attrs,
            R.styleable.CollapsingToolbarLayout, defStyleAttr,
            R.style.Widget_Design_CollapsingToolbar
        )
        mContentScrimResId = a.getResourceId(R.styleable.CollapsingToolbarLayout_contentScrim, INVALID_ID)
        mStatusBarScrimResId = a.getResourceId(R.styleable.CollapsingToolbarLayout_statusBarScrim, INVALID_ID)
        a.recycle()
        applyContentScrimResource()
        applyStatusBarScrimResource()
        mBackgroundTintHelper.loadFromAttributes(attrs, 0)
    }

    private fun applyStatusBarScrimResource() {
        if (SkinHelper.checkResourceIdValid(mStatusBarScrimResId)) {
            statusBarScrim = SkinCompatVectorResources.getDrawableCompat(context, mStatusBarScrimResId) ?: return
        }
    }

    private fun applyContentScrimResource() {
        if (SkinHelper.checkResourceIdValid(mContentScrimResId)) {
            contentScrim = SkinCompatVectorResources.getDrawableCompat(context, mContentScrimResId) ?: return
        }
    }

    override fun applySkin() {
        applyContentScrimResource()
        applyStatusBarScrimResource()
        mBackgroundTintHelper.applySkin()
    }

}

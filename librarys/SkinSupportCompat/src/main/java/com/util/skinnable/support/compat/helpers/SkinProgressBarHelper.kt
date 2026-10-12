package com.util.skinnable.support.compat.helpers

import android.graphics.Bitmap
import android.graphics.BitmapShader
import android.graphics.Shader
import android.graphics.drawable.*
import android.graphics.drawable.shapes.RoundRectShape
import android.graphics.drawable.shapes.Shape
import android.os.Build
import android.util.AttributeSet
import android.view.Gravity
import android.widget.ProgressBar
import com.util.skin.library.helpers.SkinHelper
import com.util.skin.library.res.SkinResourcesManager.getColorStateList
import com.util.skin.library.utils.SkinCompatVersionUtils.getV4DrawableWrapperWrappedDrawable
import com.util.skin.library.utils.SkinCompatVersionUtils.getV4WrappedDrawableWrappedDrawable
import com.util.skin.library.utils.SkinCompatVersionUtils.isV4DrawableWrapper
import com.util.skin.library.utils.SkinCompatVersionUtils.isV4WrappedDrawable
import com.util.skin.library.utils.SkinCompatVersionUtils.setV4DrawableWrapperWrappedDrawable
import com.util.skin.library.utils.SkinCompatVersionUtils.setV4WrappedDrawableWrappedDrawable
import com.util.skinnable.support.compat.R
import com.util.skinnable.support.compat.res.SkinCompatVectorResources.getDrawableCompat

/**
 * ProgressBar 控件换肤逻辑处理类。
 * 能够自动捕获并替换 indeterminateDrawable、progressDrawable 以及 progressTint、indeterminateTint 等着色属性。
 */
open class SkinProgressBarHelper constructor(view: ProgressBar) : SkinHelper(view) {

    private var mSampleTile: Bitmap? = null
    private var mIndeterminateDrawableResId = SkinHelper.INVALID_ID
    private var mProgressDrawableResId = SkinHelper.INVALID_ID
    private var mIndeterminateTintResId = SkinHelper.INVALID_ID
    private var mProgressTintResId = SkinHelper.INVALID_ID
    private var mProgressBackgroundTintResId = SkinHelper.INVALID_ID
    private var mSecondaryProgressTintResId = SkinHelper.INVALID_ID

    override val mView: ProgressBar
        get() = super.mView as ProgressBar

    private val drawableShape: Shape
        get() {
            val roundedCorners = floatArrayOf(5f, 5f, 5f, 5f, 5f, 5f, 5f, 5f)
            return RoundRectShape(roundedCorners, null, null)
        }

    @android.annotation.SuppressLint("ResourceType")
    override fun loadFromAttributes(attrs: AttributeSet?, defStyleAttr: Int) {
        val a = mView.context.obtainStyledAttributes(attrs, R.styleable.SkinCompatProgressBar, defStyleAttr, 0)

        mIndeterminateDrawableResId =
                a.getResourceId(R.styleable.SkinCompatProgressBar_android_indeterminateDrawable, INVALID_ID)
        mProgressDrawableResId =
                a.getResourceId(R.styleable.SkinCompatProgressBar_android_progressDrawable, INVALID_ID)

        a.recycle()
        if (Build.VERSION.SDK_INT >= 21) {
            // Android 系统属性 ID 必须严格升序排列，才能保证 obtainStyledAttributes 二分查找检索成功：
            // 1. android.R.attr.progressTint (0x01010499)
            // 2. android.R.attr.progressBackgroundTint (0x0101049a)
            // 3. android.R.attr.secondaryProgressTint (0x0101049b)
            // 4. android.R.attr.indeterminateTint (0x0101049c)
            val tintAttrs = intArrayOf(
                android.R.attr.progressTint,
                android.R.attr.progressBackgroundTint,
                android.R.attr.secondaryProgressTint,
                android.R.attr.indeterminateTint,
            )
            val ta = mView.context.obtainStyledAttributes(attrs, tintAttrs, defStyleAttr, 0)
            mProgressTintResId = ta.getResourceId(0, INVALID_ID)
            mProgressBackgroundTintResId = ta.getResourceId(1, INVALID_ID)
            mSecondaryProgressTintResId = ta.getResourceId(2, INVALID_ID)
            mIndeterminateTintResId = ta.getResourceId(3, INVALID_ID)
            ta.recycle()
        }
        applySkin()
    }

    override fun applySkin() {
        if (checkResourceIdValid(mIndeterminateDrawableResId)) {
            getDrawableCompat(mView.context, mIndeterminateDrawableResId)
                ?.let {
                    it.bounds = mView.indeterminateDrawable.bounds
                    mView.indeterminateDrawable = tilingIndeterminate(it)
                }
        }

        if (checkResourceIdValid(mProgressDrawableResId)) {
            getDrawableCompat(mView.context, mProgressDrawableResId)?.let {
                mView.progressDrawable = tiling(it, false)
            }
        }

        if (Build.VERSION.SDK_INT >= 21) {
            if (checkResourceIdValid(mIndeterminateTintResId)) {
                val csl = getColorStateList(mView.context, mIndeterminateTintResId)
                if (csl != null) {
                    mView.indeterminateTintList = csl
                }
            }
            if (checkResourceIdValid(mProgressTintResId)) {
                val csl = getColorStateList(mView.context, mProgressTintResId)
                if (csl != null) {
                    mView.progressTintList = csl
                }
            }
            if (checkResourceIdValid(mProgressBackgroundTintResId)) {
                val csl = getColorStateList(mView.context, mProgressBackgroundTintResId)
                if (csl != null) {
                    mView.progressBackgroundTintList = csl
                }
            }
            if (checkResourceIdValid(mSecondaryProgressTintResId)) {
                val csl = getColorStateList(mView.context, mSecondaryProgressTintResId)
                if (csl != null) {
                    mView.secondaryProgressTintList = csl
                }
            }
        }
    }

    /**
     * Converts a drawable to a tiled version of itself. It will recursively
     * traverse layer and state list drawables.
     */
    private fun tiling(drawable: Drawable, clip: Boolean): Drawable {
        when {
            isV4WrappedDrawable(drawable) -> {
                var inner = getV4WrappedDrawableWrappedDrawable(drawable)
                inner = tiling(inner, clip)
                setV4WrappedDrawableWrappedDrawable(drawable, inner)
            }
            isV4DrawableWrapper(drawable) -> {
                var inner = getV4DrawableWrapperWrappedDrawable(drawable)
                inner = tiling(inner, clip)
                setV4DrawableWrapperWrappedDrawable(drawable, inner)
            }
            drawable is LayerDrawable -> {
                val N = drawable.numberOfLayers
                val outDrawables = arrayOfNulls<Drawable>(N)

                for (i in 0 until N) {
                    val id = drawable.getId(i)
                    outDrawables[i] = tiling(
                        drawable.getDrawable(i),
                        id == android.R.id.progress || id == android.R.id.secondaryProgress
                    )
                }
                val newBg = LayerDrawable(outDrawables)

                for (i in 0 until N) {
                    newBg.setId(i, drawable.getId(i))
                }

                return newBg

            }
            drawable is BitmapDrawable -> {
                val tileBitmap = drawable.bitmap
                if (mSampleTile == null) {
                    mSampleTile = tileBitmap
                }

                val shapeDrawable = ShapeDrawable(drawableShape)
                val bitmapShader = BitmapShader(
                    tileBitmap,
                    Shader.TileMode.REPEAT, Shader.TileMode.CLAMP
                )
                shapeDrawable.paint.shader = bitmapShader
                shapeDrawable.paint.colorFilter = drawable.paint.colorFilter
                return if (clip)
                    ClipDrawable(
                        shapeDrawable, Gravity.LEFT,
                        ClipDrawable.HORIZONTAL
                    )
                else
                    shapeDrawable
            }
        }

        return drawable
    }

    /**
     * Convert a AnimationDrawable for use as a barberpole animation.
     * Each frame of the animation is wrapped in a ClipDrawable and
     * given a tiling BitmapShader.
     */
    private fun tilingIndeterminate(drawable: Drawable): Drawable {
        var drawable1 = drawable
        if (drawable1 is AnimationDrawable) {
            val background = drawable1
            val N = background.numberOfFrames
            val newBg = AnimationDrawable()
            newBg.isOneShot = background.isOneShot

            for (i in 0 until N) {
                val frame = tiling(background.getFrame(i), true)
                frame.level = 10000
                newBg.addFrame(frame, background.getDuration(i))
            }
            newBg.level = 10000
            drawable1 = newBg
        }
        return drawable1
    }
}

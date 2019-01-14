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
import com.util.skin.library.R
import com.util.skin.library.helpers.SkinHelper
import com.util.skin.library.res.SkinResourcesManager
import com.util.skin.library.utils.SkinCompatVersionUtils
import com.util.skinnable.support.compat.res.SkinCompatVectorResources

open class SkinCompatProgressBarHelper constructor(private val mView: ProgressBar) : SkinHelper() {

    private var mSampleTile: Bitmap? = null
    private var mIndeterminateDrawableResId = SkinHelper.INVALID_ID
    private var mProgressDrawableResId = SkinHelper.INVALID_ID
    private var mIndeterminateTintResId = SkinHelper.INVALID_ID

    private val drawableShape: Shape
        get() {
            val roundedCorners = floatArrayOf(5f, 5f, 5f, 5f, 5f, 5f, 5f, 5f)
            return RoundRectShape(roundedCorners, null, null)
        }

    open fun loadFromAttributes(attrs: AttributeSet?, defStyleAttr: Int) {
        var a = mView.context.obtainStyledAttributes(attrs, R.styleable.SkinCompatProgressBar, defStyleAttr, 0)

        mIndeterminateDrawableResId =
                a.getResourceId(R.styleable.SkinCompatProgressBar_android_indeterminateDrawable, SkinHelper.INVALID_ID)
        mProgressDrawableResId =
                a.getResourceId(R.styleable.SkinCompatProgressBar_android_progressDrawable, SkinHelper.INVALID_ID)

        a.recycle()
        if (Build.VERSION.SDK_INT > 21) {
            a = mView.context.obtainStyledAttributes(
                attrs,
                intArrayOf(android.R.attr.indeterminateTint),
                defStyleAttr,
                0
            )
            mIndeterminateTintResId = a.getResourceId(0, SkinHelper.INVALID_ID)
            a.recycle()
        }
        applySkin()
    }

    /**
     * Converts a drawable to a tiled version of itself. It will recursively
     * traverse layer and state list drawables.
     */
    private fun tileify(drawable: Drawable, clip: Boolean): Drawable {
        when {
            SkinCompatVersionUtils.isV4WrappedDrawable(drawable) -> {
                var inner = SkinCompatVersionUtils.getV4WrappedDrawableWrappedDrawable(drawable)
                inner = tileify(inner, clip)
                SkinCompatVersionUtils.setV4WrappedDrawableWrappedDrawable(drawable, inner)
            }
            SkinCompatVersionUtils.isV4DrawableWrapper(drawable) -> {
                var inner = SkinCompatVersionUtils.getV4DrawableWrapperWrappedDrawable(drawable)
                inner = tileify(inner, clip)
                SkinCompatVersionUtils.setV4DrawableWrapperWrappedDrawable(drawable, inner)
            }
            drawable is LayerDrawable -> {
                val N = drawable.numberOfLayers
                val outDrawables = arrayOfNulls<Drawable>(N)

                for (i in 0 until N) {
                    val id = drawable.getId(i)
                    outDrawables[i] = tileify(
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
    private fun tileifyIndeterminate(drawable: Drawable): Drawable {
        var drawable1 = drawable
        if (drawable1 is AnimationDrawable) {
            val background = drawable1
            val N = background.numberOfFrames
            val newBg = AnimationDrawable()
            newBg.isOneShot = background.isOneShot

            for (i in 0 until N) {
                val frame = tileify(background.getFrame(i), true)
                frame.level = 10000
                newBg.addFrame(frame, background.getDuration(i))
            }
            newBg.level = 10000
            drawable1 = newBg
        }
        return drawable1
    }

    override fun applySkin() {
        if (checkResourceIdValid(mIndeterminateDrawableResId)) {
            val drawable = SkinCompatVectorResources.getDrawableCompat(mView.context, mIndeterminateDrawableResId)
            drawable?.let {
                drawable.bounds = mView.indeterminateDrawable.bounds
                mView.indeterminateDrawable = tileifyIndeterminate(drawable)
            }
        }

        if (checkProgressDrawableResId(mProgressDrawableResId)) {
            val drawable = SkinCompatVectorResources.getDrawableCompat(mView.context, mProgressDrawableResId)
            drawable?.let {
                mView.progressDrawable = tileify(drawable, false)
            }
        }
        if (Build.VERSION.SDK_INT > 21) {
            if (checkResourceIdValid(mIndeterminateTintResId)) {
                mView.indeterminateTintList =
                        SkinResourcesManager.getColorStateList(mView.context, mIndeterminateTintResId)
            }
        }
    }

    private fun checkProgressDrawableResId(mProgressDrawableResId: Int): Boolean {
        return checkResourceIdValid(mProgressDrawableResId)
    }
}

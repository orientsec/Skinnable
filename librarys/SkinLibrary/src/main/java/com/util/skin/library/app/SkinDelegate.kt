package com.util.skin.library.app

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View

import com.util.skin.library.SkinManager
import com.util.skin.library.widget.SkinSupportable

import java.lang.ref.WeakReference
import java.util.ArrayList

class SkinDelegate private constructor(private val mContext: Context) : LayoutInflater.Factory2 {
    private var mSkinCompatViewInflater: SkinViewInflater? = null
    private val mSkinHelpers = ArrayList<WeakReference<SkinSupportable>>()

    override fun onCreateView(name: String?, context: Context?, attrs: AttributeSet?): View? {
        return null
    }

    override fun onCreateView(parent: View?, name: String, context: Context, attrs: AttributeSet): View? {
        val view = createView(parent, name, context, attrs) ?: return null

        if (view is SkinSupportable) {
            mSkinHelpers.add(WeakReference(view as SkinSupportable))
        }

        return view
    }

    private fun createView(
        parent: View?, name: String, context: Context,
        attrs: AttributeSet
    ): View? {
        var tempContext = context
        if (mSkinCompatViewInflater == null) {
            mSkinCompatViewInflater = SkinViewInflater()
        }

        val wrapperList = SkinManager.wrappers
        for (wrapper in wrapperList) {
            val wrappedContext = wrapper.wrapContext(mContext, parent, attrs)
            tempContext = wrappedContext
        }
        return mSkinCompatViewInflater!!.createView(name, tempContext, attrs)
    }

    fun applySkin() {
        if (!mSkinHelpers.isEmpty()) {
            for (ref in mSkinHelpers) {
                if (ref.get() != null) {
                    (ref.get() as SkinSupportable).applySkin()
                }
            }
        }
    }

    companion object {

        fun create(context: Context): SkinDelegate {
            return SkinDelegate(context)
        }
    }
}

package com.util.skin.library.factory

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import com.util.skin.library.widget.SkinSupportable
import java.lang.ref.WeakReference

/**
 * [LayoutInflater.onCreateView] 方法监听并转发
 */
internal class SkinFactory2Impl : LayoutInflater.Factory2 {
    private val mSkinHelpers = ArrayList<WeakReference<SkinSupportable>>()

    override fun onCreateView(name: String, context: Context, attrs: AttributeSet): View? {
        return null
    }

    override fun onCreateView(
        parent: View?,
        name: String,
        context: Context,
        attrs: AttributeSet
    ): View? {
        return SkinFactoryManager.onCreateView(name, context, attrs)?.apply {
            if (this is SkinSupportable && skinnable) {
                // 记录支持换肤功能的View
                mSkinHelpers.add(WeakReference(this as SkinSupportable))
            }
        }
    }

    fun applySkin() {
        mSkinHelpers.forEach {
            it.get()?.apply { if (skinnable) applySkin() }
        }
    }
}

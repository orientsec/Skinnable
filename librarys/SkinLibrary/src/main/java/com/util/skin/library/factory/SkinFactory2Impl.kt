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
    private var mOriginalFactory: LayoutInflater.Factory2? = null

    fun setOriginalFactory(factory: LayoutInflater.Factory2?) {
        mOriginalFactory = factory
    }

    override fun onCreateView(name: String, context: Context, attrs: AttributeSet): View? {
        return onCreateView(null, name, context, attrs)
    }

    override fun onCreateView(
        parent: View?,
        name: String,
        context: Context,
        attrs: AttributeSet
    ): View? {
        var view = SkinFactoryManager.onCreateView(name, context, attrs)
        if (view == null) {
            view = mOriginalFactory?.onCreateView(parent, name, context, attrs)
        }
        return view?.apply {
            if (this is SkinSupportable && skinnable) {
                // 记录支持换肤功能的View
                mSkinHelpers.add(WeakReference(this as SkinSupportable))
            }
        }
    }

    fun applySkin() {
        val iterator = mSkinHelpers.iterator()
        while (iterator.hasNext()) {
            val view = iterator.next().get()
            if (view == null) {
                iterator.remove() // 移除失效的弱引用，防止内存泄漏
            } else if (view.skinnable) {
                view.applySkin()
            }
        }
    }
}

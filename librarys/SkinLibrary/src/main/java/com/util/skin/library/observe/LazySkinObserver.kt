package com.util.skin.library.observe

import android.app.Activity
import android.content.Context
import com.util.skin.library.app.SkinActivityLifecycle
import com.util.skin.library.utils.Slog
import com.util.skin.library.widget.SkinSupportable

/**
 * @PackageName com.util.skin.library.observe
 * @date 2019/1/10 17:01
 * @author zhanglei
 */
internal class LazySkinObserver constructor(private val mContext: Context?) : SkinObserver {
    companion object {
        private const val TAG = "SkinActivityLifecycle"
    }

    private var mMarkNeedUpdate = false

    override fun updateSkin(observable: SkinObservable) {
        // 当前Activity，或者非Activity，立即刷新，否则延迟到下次onResume方法中刷新。
        if (SkinActivityLifecycle.mCurActivityRef == null
            || mContext === SkinActivityLifecycle.mCurActivityRef!!.get()
            || mContext !is Activity
        ) {
            updateSkinForce()
        } else {
            mMarkNeedUpdate = true
        }
    }

    internal fun updateSkinIfNeeded() {
        if (mMarkNeedUpdate) {
            updateSkinForce()
        }
    }

    private fun updateSkinForce() {
        if (Slog.DEBUG) {
            Slog.i(TAG, "Context: $mContext updateSkinForce")
        }
        if (mContext == null) {
            return
        }
        if (mContext is Activity && SkinActivityLifecycle.isContextSkinEnable(mContext)) {
            SkinActivityLifecycle.updateWindowBackground(mContext)
        }
        SkinActivityLifecycle.getSkinDelegate(mContext).applySkin()
        if (mContext is SkinSupportable && mContext.skinnable) {
            mContext.applySkin()
        }
        mMarkNeedUpdate = false
    }
}
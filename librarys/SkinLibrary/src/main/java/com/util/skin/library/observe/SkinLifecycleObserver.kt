package com.util.skin.library.observe

import android.app.Activity
import android.content.Context
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import com.util.skin.library.SkinManager
import com.util.skin.library.app.SkinActivityLifecycle
import com.util.skin.library.widget.SkinSupportable

/**
 * 供 Fragment/Dialog 或自定义 LifecycleOwner 绑定的换肤生命周期观察者。
 * 可以通过 lifecycle.addObserver(SkinLifecycleObserver(context)) 自动感知换肤刷新及解绑。
 */
class SkinLifecycleObserver(private val context: Context) : DefaultLifecycleObserver, SkinObserver {
    override fun onResume(owner: LifecycleOwner) {
        SkinManager.addObserver(this)
        updateSkinForce()
    }

    override fun onDestroy(owner: LifecycleOwner) {
        SkinManager.deleteObserver(this)
    }

    override fun updateSkin(observable: SkinObservable) {
        updateSkinForce()
    }

    private fun updateSkinForce() {
        if (context is Activity && SkinActivityLifecycle.isContextSkinEnable(context)) {
            SkinActivityLifecycle.updateWindowBackground(context)
        }
        SkinActivityLifecycle.skinFactory(context).applySkin()
        if (context is SkinSupportable && context.skinnable) {
            context.applySkin()
        }
    }
}

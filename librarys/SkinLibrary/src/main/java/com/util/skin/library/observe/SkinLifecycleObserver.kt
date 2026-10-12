package com.util.skin.library.observe

import android.app.Activity
import android.content.Context
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import com.util.skin.library.SkinManager
import com.util.skin.library.app.SkinActivityLifecycle
import com.util.skin.library.utils.applySkinToViewTree
import com.util.skin.library.widget.SkinSupportable

/**
 * 供 Fragment/Dialog/View 或自定义 LifecycleOwner 绑定的换肤生命周期观察者。
 * 可以通过 lifecycle.addObserver(SkinLifecycleObserver(target)) 自动感知换肤刷新及解绑。
 */
class SkinLifecycleObserver(
    private val target: Any
) : DefaultLifecycleObserver, SkinObserver {

    constructor(context: Context) : this(target = context as Any)

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
        when (target) {
            is Fragment -> {
                target.view?.let { applySkinToViewTree(it) }
                if (target is SkinSupportable && target.skinnable) {
                    target.applySkin()
                }
            }
            is View -> {
                applySkinToViewTree(target)
            }
            is Context -> {
                if (target is Activity && SkinActivityLifecycle.isContextSkinEnable(target)) {
                    SkinActivityLifecycle.updateWindowBackground(target)
                }
                SkinActivityLifecycle.skinFactory(target).applySkin()
                if (target is SkinSupportable && target.skinnable) {
                    target.applySkin()
                }
            }
        }
    }
}


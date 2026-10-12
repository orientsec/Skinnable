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
 * 供 [Fragment]、[android.app.Dialog]、[View] 或自定义 [LifecycleOwner] 绑定的生命周期感知换肤观察者。
 * 通过在目标生命周期组件中注册 `lifecycle.addObserver(SkinLifecycleObserver(this))`，
 * 可以实现在 `onResume` 时自动注册全局换肤监听并刷新界面，在 `onDestroy` 时自动接触注册防止内存泄漏。
 *
 * @param target 换肤目标组件（支持 [Fragment]、[View] 或 [Context]）
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


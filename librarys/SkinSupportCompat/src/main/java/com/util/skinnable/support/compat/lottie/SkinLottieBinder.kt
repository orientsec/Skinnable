package com.util.skinnable.support.compat.lottie

import com.airbnb.lottie.LottieAnimationView
import com.airbnb.lottie.model.KeyPath
import com.util.skin.library.SkinManager
import com.util.skin.library.helpers.SkinHelper
import com.util.skin.library.observe.SkinObservable
import com.util.skin.library.observe.SkinObserver
import java.lang.ref.WeakReference

/**
 * Lottie 换肤注解绑定器 [SkinLottieBinder]。
 * 通过反射或直接绑定 [LottieSkin] 注解规则，无侵入为任意 [LottieAnimationView] 实例绑定动态换肤功能。
 */
object SkinLottieBinder {

    /**
     * 自动扫描 target 对象（如 Activity、Fragment、ViewHolder 或 View）中带有 [@LottieSkin] 注解的 [LottieAnimationView] 字段，
     * 并为其应用动态换肤。
     */
    fun bind(target: Any) {
        val clazz = target.javaClass
        for (field in clazz.declaredFields) {
            val annotation = field.getAnnotation(LottieSkin::class.java) ?: continue
            field.isAccessible = true
            val view = field.get(target) as? LottieAnimationView ?: continue
            bindView(view, annotation)
        }
    }

    /**
     * 为指定的 [LottieAnimationView] 实例基于 [LottieSkin] 注解应用换肤
     */
    fun bindView(view: LottieAnimationView, skinAnnotation: LottieSkin) {
        val helper = SkinLottieHelper(view)

        if (SkinHelper.checkResourceIdValid(skinAnnotation.rawResId)) {
            helper.setRawResId(skinAnnotation.rawResId)
        }

        if (skinAnnotation.autoColor) {
            helper.addKeyPathColorBinding(
                KeyPath("**"),
                colorResId = SkinHelper.INVALID_ID,
                colorAttrId = androidx.appcompat.R.attr.colorPrimary
            )
        } else if (SkinHelper.checkResourceIdValid(skinAnnotation.colorResId) ||
            SkinHelper.checkResourceIdValid(skinAnnotation.colorAttrId)
        ) {
            val keys = skinAnnotation.keyPath.split("/").filter { it.isNotEmpty() }.toTypedArray()
            helper.addKeyPathColorBinding(
                KeyPath(*keys),
                skinAnnotation.colorResId,
                skinAnnotation.colorAttrId
            )
        }

        helper.setImageAssetDelegateEnabled(skinAnnotation.enableImageAssetDelegate)
        helper.applySkin()

        // 注册观察者监听皮肤切换
        val weakView = WeakReference(view)
        val observer = object : SkinObserver {
            override fun updateSkin(observable: SkinObservable) {
                val v = weakView.get()
                if (v != null) {
                    helper.applySkin()
                } else {
                    SkinManager.deleteObserver(this)
                }
            }
        }
        SkinManager.addObserver(observer)
    }
}

package com.util.skinnable.support.compat.lottie

import android.content.Context
import android.util.AttributeSet
import android.view.View
import com.util.skin.library.SkinManager
import com.util.skin.library.factory.SkinFactory

/**
 * 针对 LottieAnimationView 的换肤拦截器 [SkinFactory]。
 * 自动拦截 XML 布局中的 <com.airbnb.lottie.LottieAnimationView> 节点，
 * 替换为具备动态换肤能力的 [SkinLottieAnimationView] 实例。
 */
class LottieSkinInterceptor : SkinFactory {

    override fun onCreateView(name: String, context: Context, attrs: AttributeSet): View? {
        if (isLottieView(name)) {
            return SkinLottieAnimationView(context, attrs)
        }
        return null
    }

    private fun isLottieView(name: String): Boolean {
        return name == LOTTIE_ANIMATION_VIEW_CLASS ||
                name == SKIN_LOTTIE_ANIMATION_VIEW_CLASS ||
                name.endsWith(".LottieAnimationView") ||
                name.endsWith("LottieAnimationView")
    }

    companion object {
        private const val LOTTIE_ANIMATION_VIEW_CLASS = "com.airbnb.lottie.LottieAnimationView"
        private const val SKIN_LOTTIE_ANIMATION_VIEW_CLASS =
            "com.util.skinnable.support.compat.lottie.SkinLottieAnimationView"

        /**
         * 快捷注入 Lottie 换肤拦截器至 SkinManager 全局配置
         */
        fun register() {
            try {
                SkinManager.config.addFactory(LottieSkinInterceptor())
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}

package com.util.skin.library.loader

/**
 * 加载类型
 * @PackageName com.util.skin.library.loader
 * @date 2019/1/10 15:08
 * @author zhanglei
 */
enum class SkinLoaderStrategyType(val type: Int) {
    /**默认*/
    Default(-1),
    /**ASSETS中加载*/
    Assets(0),
    /**后缀加载*/
    BuildIn(1),
    /**前缀加载*/
    PrefixBuildIn(2);

    companion object {
        private val map by lazy { SkinLoaderStrategyType.values().associateBy { it.type } }
        fun parseType(type: Int): SkinLoaderStrategyType = map[type]
                ?: SkinLoaderStrategyType.Default
    }
}
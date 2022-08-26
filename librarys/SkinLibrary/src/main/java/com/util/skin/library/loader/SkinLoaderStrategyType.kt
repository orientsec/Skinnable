package com.util.skin.library.loader

/**
 * 加载类型
 * @PackageName com.util.skin.library.loader
 * @date 2019/1/10 15:08
 * @author zhanglei
 */
enum class SkinLoaderStrategyType(val type: Int) {
    /**ASSETS中加载*/
    Assets(0),

    /**后缀加载*/
    BuildIn(1),

    /**前缀加载*/
    PrefixBuildIn(2),

    /**从SD card中加载 */
    SDCard(3),

    /**加载zip文件*/
    Zip(4);

    companion object {
        private val map by lazy { values().associateBy { it.type } }
        fun parseType(type: Int): SkinLoaderStrategyType = map[type]
            ?: throw RuntimeException("没有对应的SkinLoaderStrategyType")
    }
}
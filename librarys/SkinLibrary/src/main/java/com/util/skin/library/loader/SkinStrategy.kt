package com.util.skin.library.loader

/**
 * 换肤策略
 * @PackageName com.util.skin.library.loader
 * @date 2022/8/29 15:20
 * @author zhanglei
 */
sealed interface SkinStrategy {
    /**
     * 皮肤名称
     */
    val skinName: String

    /**
     * 策略
     */
    val loaderStrategy: SkinLoaderStrategy

    /**ASSETS中加载*/
    data class Assets(
        override val skinName: String,
        override val loaderStrategy: SkinLoaderStrategy = SkinAssetsLoader(skinName)
    ) : SkinStrategy

    /**后缀加载*/
    data class BuildIn(
        override val skinName: String,
        override val loaderStrategy: SkinLoaderStrategy = SkinBuildInLoader(skinName)
    ) : SkinStrategy

    /**前缀加载*/
    data class PrefixBuildIn(
        override val skinName: String,
        override val loaderStrategy: SkinLoaderStrategy = SkinPrefixBuildInLoader(skinName)
    ) : SkinStrategy

    /**从SD card中加载 */
    data class SDCard(
        override val skinName: String,
        override val loaderStrategy: SkinLoaderStrategy = SkinAssetsLoader(skinName)
    ) : SkinStrategy

    /**加载zip文件*/
    data class Zip(
        override val skinName: String,
        override val loaderStrategy: SkinLoaderStrategy = SkinAssetsLoader(skinName)
    ) : SkinStrategy

}
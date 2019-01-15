package com.util.skin.library

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import android.content.pm.PackageManager
import android.content.res.Resources
import android.text.TextUtils

import com.util.skin.library.app.SkinActivityLifecycle
import com.util.skin.library.app.SkinLayoutInflater
import com.util.skin.library.app.SkinWrapper
import com.util.skin.library.loader.*
import com.util.skin.library.observe.SkinObservable
import com.util.skin.library.res.SkinResourcesManager
import com.util.skin.library.scope.BaseScope
import com.util.skin.library.utils.SkinPreference
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

import java.util.ArrayList
import kotlin.coroutines.CoroutineContext

@SuppressLint("StaticFieldLeak")
object SkinManager : SkinObservable(), CoroutineScope {
    lateinit var context: Context
    private val mWrappers = ArrayList<SkinWrapper>()
    private val mInflaters = ArrayList<SkinLayoutInflater>()
    val strategies = HashMap<SkinLoaderStrategyType, SkinLoaderStrategy>()
    private var mSkinAllActivityEnable = true
    private var mSkinWindowBackgroundColorEnable = true

    private val scope: BaseScope = BaseScope()
    override val coroutineContext: CoroutineContext
        get() = scope.coroutineContext

    val wrappers: List<SkinWrapper>
        get() = mWrappers.toList()

    val inflaters: List<SkinLayoutInflater>
        get() = mInflaters

    init {
        initLoaderStrategy()
        scope.create()
    }

    private fun initLoaderStrategy() {
        strategies[SkinLoaderStrategyType.Default] = SkinNoneLoader()
        strategies[SkinLoaderStrategyType.Assets] = SkinAssetsLoader()
        strategies[SkinLoaderStrategyType.BuildIn] = SkinBuildInLoader()
        strategies[SkinLoaderStrategyType.PrefixBuildIn] = SkinPrefixBuildInLoader()
    }

    /**
     * 添加皮肤包加载策略.
     *
     * @param strategy 自定义加载策略
     * @return
     */
    fun addStrategy(strategy: SkinLoaderStrategy): SkinManager {
        strategies[strategy.type] = strategy
        return this
    }

    /**
     * 自定义View换肤时，可选择添加一个[SkinLayoutInflater]
     *
     * @param inflater 在[SkinLayoutInflater.createView]方法中调用.
     * @return
     */
    fun addInflater(inflater: SkinLayoutInflater): SkinManager {
        if (inflater is SkinWrapper) {
            mWrappers.add(inflater as SkinWrapper)
        }
        mInflaters.add(inflater)
        return this
    }

    /**
     * 恢复默认主题，使用应用自带资源.
     */
    fun restoreDefaultSkin() {
        loadSkin("", SkinLoaderStrategyType.Default)
    }

    /**
     * 设置是否所有Activity都换肤.
     *
     * @param enable true: 所有Activity都换肤; false: 添加注解Skinnable或实现SkinSupportable的Activity支持换肤.
     * @return
     */
    fun setSkinAllActivityEnable(enable: Boolean): SkinManager {
        mSkinAllActivityEnable = enable
        return this
    }

    internal fun isSkinAllActivityEnable(): Boolean {
        return mSkinAllActivityEnable
    }

    /**
     * 设置WindowBackground换肤，使用Theme中的[android.R.attr.windowBackground]属性.
     *
     * @param enable true: 打开; false: 关闭.
     * @return
     */
    fun setSkinWindowBackgroundEnable(enable: Boolean): SkinManager {
        mSkinWindowBackgroundColorEnable = enable
        return this
    }

    internal fun isSkinWindowBackgroundEnable(): Boolean {
        return mSkinWindowBackgroundColorEnable
    }

    /**
     * 加载记录的皮肤包，一般在Application中初始化换肤框架后调用.
     *
     * @param listener 皮肤包加载监听.
     * @return
     */
    fun loadSkin(listener: SkinLoaderListener? = null) {
        val skin = SkinPreference.skinName
        val strategy = SkinLoaderStrategyType.parseType(SkinPreference.skinStrategy)
        if (skin.isNotBlank() && strategy != SkinLoaderStrategyType.Default) {
            loadSkin(skin, strategy, listener)
        }
    }

    /**
     * 加载皮肤包.
     *
     * @param skinName 皮肤包名称.
     * @param listener 皮肤包加载监听.
     * @param strategy 皮肤包加载策略.
     * @return
     */
    fun loadSkin(skinName: String, strategy: SkinLoaderStrategyType, listener: SkinLoaderListener? = null) {
        val loaderStrategy = strategies[strategy] ?: return
        launch {
            listener?.onStart()
            try {
                // 加载资源
                loadTask(skinName, loaderStrategy)
                // skinName 为""时，恢复默认皮肤
                SkinPreference.setSkinName(skinName).setSkinStrategy(loaderStrategy.type).commitEditor()
                notifyUpdateSkin()
                listener?.onSuccess()
            } catch (e: Exception) {
                e.printStackTrace()

                SkinResourcesManager.reset()
                SkinPreference.setSkinName("").setSkinStrategy(SkinLoaderStrategyType.Default).commitEditor()
                listener?.onFailed("皮肤资源获取失败")
            }
        }
    }

    private suspend fun loadTask(skinName: String, strategy: SkinLoaderStrategy) {
        val backName = strategy.loadSkinInBackground(context, skinName)
        if (TextUtils.isEmpty(backName)) {
            SkinResourcesManager.reset(strategy)
        }
    }

    /**
     * 获取皮肤包包名.
     *
     * @param skinPkgPath sdcard中皮肤包路径.
     * @return
     */
    internal fun getSkinPackageName(skinPkgPath: String): String {
        val mPm = context.packageManager
        val info = mPm.getPackageArchiveInfo(skinPkgPath, PackageManager.GET_ACTIVITIES)
        return info.packageName
    }

    /**
     * 获取皮肤包资源[Resources].
     *
     * @param skinPkgPath sdcard中皮肤包路径.
     * @return
     */
    internal fun getSkinResources(skinPkgPath: String): Resources? {
        try {
            val packageInfo = context.packageManager.getPackageArchiveInfo(skinPkgPath, 0)
            packageInfo.applicationInfo.sourceDir = skinPkgPath
            packageInfo.applicationInfo.publicSourceDir = skinPkgPath
            val res = context.packageManager.getResourcesForApplication(packageInfo.applicationInfo)
            val superRes = context.resources
            return Resources(res.assets, superRes.displayMetrics, superRes.configuration)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return null
    }

    /**
     * 初始化换肤框架，监听Activity生命周期. 通过该方法初始化，应用中Activity无需继承
     *
     * @param application 应用Application.
     * @return
     */
    fun init(application: Application): SkinManager {
        this.context = application
        SkinPreference.init(context)
        SkinActivityLifecycle.init(application)
        return this
    }

    fun destroy() {
        scope.destroy()
    }
}
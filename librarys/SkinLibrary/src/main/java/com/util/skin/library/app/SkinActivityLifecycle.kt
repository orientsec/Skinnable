package com.util.skin.library.app

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Application
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.LayoutInflater.Factory2
import android.view.View
import androidx.core.graphics.ColorUtils
import androidx.core.view.LayoutInflaterCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import com.util.skin.library.SkinManager
import com.util.skin.library.annotation.Skinnable
import com.util.skin.library.factory.SkinFactory2Impl
import com.util.skin.library.helpers.SkinHelper.Companion.checkResourceIdValid
import com.util.skin.library.observe.LazySkinObserver
import com.util.skin.library.res.SkinResourcesManager
import com.util.skin.library.res.SkinThemeUtils
import com.util.skin.library.utils.applySkinToViewTree
import com.util.skin.library.widget.SkinSupportable
import java.lang.ref.WeakReference
import java.util.*

internal object SkinActivityLifecycle : Application.ActivityLifecycleCallbacks {
    private val mSkinFactoryMap: WeakHashMap<Context, SkinFactory2Impl> = WeakHashMap()
    private val mSkinObserverMap: WeakHashMap<Context, LazySkinObserver> = WeakHashMap()

    /**
     * 用于记录当前Activity，在换肤后，立即刷新当前Activity以及非Activity创建的View。
     */
    var mCurActivityRef: WeakReference<Activity>? = null

    fun init(application: Application): SkinActivityLifecycle {
        application.registerActivityLifecycleCallbacks(this)
        installLayoutFactory(application)
        SkinManager.addObserver(getObserver(application))
        return this
    }

    override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
        if (isContextSkinEnable(activity)) {
            installLayoutFactory(activity)
            updateWindowBackground(activity)
            registerFragmentLifecycleCallbacks(activity)
            SkinManager.checkAndApplySystemDarkMode(activity)
            if (activity is SkinSupportable && activity.skinnable) {
                (activity as SkinSupportable).applySkin()
            }
        }
    }

    private fun registerFragmentLifecycleCallbacks(activity: Activity) {
        if (activity is FragmentActivity) {
            activity.supportFragmentManager.registerFragmentLifecycleCallbacks(
                object : FragmentManager.FragmentLifecycleCallbacks() {
                    override fun onFragmentViewCreated(
                        fm: FragmentManager,
                        f: Fragment,
                        v: View,
                        savedInstanceState: Bundle?
                    ) {
                        applySkinToViewTree(v)
                        if (f is SkinSupportable && f.skinnable) {
                            f.applySkin()
                        }
                    }

                    override fun onFragmentResumed(fm: FragmentManager, f: Fragment) {
                        f.view?.let { view ->
                            applySkinToViewTree(view)
                        }
                        if (f is SkinSupportable && f.skinnable) {
                            f.applySkin()
                        }
                    }
                },
                true // recursive = true 递归监控所有 ChildFragmentManager 中的 Fragment
            )
        }
    }

    fun applySkinToFragments(activity: FragmentActivity) {
        fun refreshFragmentManager(fm: FragmentManager) {
            for (f in fm.fragments) {
                if (f != null && f.isAdded) {
                    f.view?.let { applySkinToViewTree(it) }
                    if (f is SkinSupportable && f.skinnable) {
                        f.applySkin()
                    }
                    refreshFragmentManager(f.childFragmentManager)
                }
            }
        }
        refreshFragmentManager(activity.supportFragmentManager)
    }

    override fun onActivityStarted(activity: Activity) {

    }

    override fun onActivityResumed(activity: Activity) {
        mCurActivityRef = WeakReference(activity)
        if (isContextSkinEnable(activity)) {
            val observer = getObserver(activity)
            SkinManager.addObserver(observer)
            observer.updateSkinIfNeeded()
        }
    }

    override fun onActivityPaused(activity: Activity) {
        if (isContextSkinEnable(activity)) {
            val observer = getObserver(activity)
            SkinManager.deleteObserver(observer)
        }
    }

    override fun onActivityStopped(activity: Activity) {

    }

    override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {
    }

    override fun onActivityDestroyed(activity: Activity) {
        if (isContextSkinEnable(activity)) {
            mSkinObserverMap.remove(activity)
            mSkinFactoryMap.remove(activity)
        }
    }

    private fun installLayoutFactory(context: Context) {
        val inflater = LayoutInflater.from(context)
        val factory = skinFactory(context)
        if (inflater.factory2 !is SkinFactory2Impl) {
            factory.setOriginalFactory(inflater.factory2)
            forceSetFactory2(inflater, factory)
        }
    }

    fun skinFactory(context: Context): SkinFactory2Impl {
        val mSkinFactory = mSkinFactoryMap[context] ?: SkinFactory2Impl()
        mSkinFactoryMap[context] = mSkinFactory
        return mSkinFactory
    }

    private fun getObserver(context: Context): LazySkinObserver {
        val observer = mSkinObserverMap[context] ?: LazySkinObserver(context)
        mSkinObserverMap[context] = observer
        return observer
    }

    fun updateWindowBackground(activity: Activity) {
        if (SkinManager.config.windowBackground) {
            val windowBackgroundResId = SkinThemeUtils.getWindowBackgroundResId(activity)
            if (checkResourceIdValid(windowBackgroundResId)) {
                SkinResourcesManager.getDrawable(activity, windowBackgroundResId)
                    ?.let { activity.window.setBackgroundDrawable(it) }
            }
        }
        updateStatusBarColor(activity)
    }

    private fun updateStatusBarColor(activity: Activity) {
        try {
            val statusBarResId = SkinThemeUtils.getStatusBarColorResId(activity)
            if (checkResourceIdValid(statusBarResId)) {
                val color = SkinResourcesManager.getColor(activity, statusBarResId)
                activity.window.statusBarColor = color
                val controller = WindowInsetsControllerCompat(activity.window, activity.window.decorView)
                val isLight = ColorUtils.calculateLuminance(color) > 0.5
                controller.isAppearanceLightStatusBars = isLight
            }
        } catch (e: Exception) {
            // Ignore system bar tinting exceptions
        }
    }

    fun isContextSkinEnable(context: Context): Boolean {
        return (SkinManager.config.allActivity
                || context.javaClass.getAnnotation(Skinnable::class.java) != null
                || context is SkinSupportable)
    }

    /**
     * For APIs < 21, there was a framework bug that prevented a LayoutInflater's
     * Factory2 from being merged properly if set after a cloneInContext from a LayoutInflater
     * that already had a Factory2 registered. We work around that bug here. If we can't we
     * log an error.
     */
    @SuppressLint("DiscouragedPrivateApi")
    private fun forceSetFactory2(inflater: LayoutInflater, factory: Factory2) {
        try {
            LayoutInflaterCompat.setFactory2(inflater, factory)
        } catch (e: Exception) {
            try {
                val sLayoutInflaterFactory2Field =
                    LayoutInflater::class.java.getDeclaredField("mFactory2")
                sLayoutInflaterFactory2Field.isAccessible = true
                sLayoutInflaterFactory2Field[inflater] = factory
            } catch (e2: NoSuchFieldException) {
                e2.printStackTrace()
            } catch (e2: IllegalAccessException) {
                e2.printStackTrace()
            }
        }
    }
}
package com.util.skin.library.app

import android.app.Activity
import android.app.Application
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import androidx.core.view.LayoutInflaterCompat
import com.util.skin.library.SkinManager
import com.util.skin.library.annotation.Skinnable
import com.util.skin.library.helpers.SkinHelper.Companion.checkResourceIdValid
import com.util.skin.library.observe.LazySkinObserver
import com.util.skin.library.res.SkinResourcesManager
import com.util.skin.library.res.SkinThemeUtils
import com.util.skin.library.widget.SkinSupportable
import java.lang.ref.WeakReference
import java.util.*

internal object SkinActivityLifecycle : Application.ActivityLifecycleCallbacks {
    private var mSkinDelegateMap: WeakHashMap<Context, SkinDelegate>? = null
    private var mSkinObserverMap: WeakHashMap<Context, LazySkinObserver>? = null
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
            if (activity is SkinSupportable && activity.skinnable) {
                (activity as SkinSupportable).applySkin()
            }
        }
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

    override fun onActivityPaused(activity: Activity) {}

    override fun onActivityStopped(activity: Activity) {

    }

    override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle?) {

    }

    override fun onActivityDestroyed(activity: Activity) {
        if (isContextSkinEnable(activity)) {
            SkinManager.deleteObserver(getObserver(activity))
            mSkinObserverMap?.remove(activity)
            mSkinDelegateMap?.remove(activity)
        }
    }

    private fun installLayoutFactory(context: Context) {
        val layoutInflater = LayoutInflater.from(context)
        try {
            val field = LayoutInflater::class.java.getDeclaredField("mFactorySet")
            field.isAccessible = true
            field.setBoolean(layoutInflater, false)
            LayoutInflaterCompat.setFactory2(layoutInflater, getSkinDelegate(context))
        } catch (e: NoSuchFieldException) {
            e.printStackTrace()
        } catch (e: IllegalArgumentException) {
            e.printStackTrace()
        } catch (e: IllegalAccessException) {
            e.printStackTrace()
        }

    }

    fun getSkinDelegate(context: Context): SkinDelegate {
        if (mSkinDelegateMap == null) {
            mSkinDelegateMap = WeakHashMap()
        }

        val mSkinDelegate = mSkinDelegateMap!![context] ?: SkinDelegate.create(context)
        mSkinDelegateMap!![context] = mSkinDelegate
        return mSkinDelegate
    }

    private fun getObserver(context: Context): LazySkinObserver {
        if (mSkinObserverMap == null) {
            mSkinObserverMap = WeakHashMap()
        }
        val observer = mSkinObserverMap!![context] ?: LazySkinObserver(context)
        mSkinObserverMap!![context] = observer
        return observer
    }

    fun updateWindowBackground(activity: Activity) {
        if (SkinManager.isSkinWindowBackgroundEnable()) {
            val windowBackgroundResId = SkinThemeUtils.getWindowBackgroundResId(activity)
            if (checkResourceIdValid(windowBackgroundResId)) {
                SkinResourcesManager.getDrawable(activity, windowBackgroundResId)
                    ?.let { activity.window.setBackgroundDrawable(it) }
            }
        }
    }

    fun isContextSkinEnable(context: Context): Boolean {
        return (SkinManager.isSkinAllActivityEnable()
                || context.javaClass.getAnnotation(Skinnable::class.java) != null
                || context is SkinSupportable)
    }
}
package com.util.skin.library.app

import android.content.Context
import android.content.ContextWrapper
import android.os.Build
import android.util.AttributeSet
import android.view.View
import androidx.collection.ArrayMap
import androidx.core.view.ViewCompat
import com.util.skin.library.SkinManager
import java.lang.reflect.Constructor
import java.lang.reflect.InvocationTargetException
import java.lang.reflect.Method

class SkinViewInflater {

    private val mConstructorArgs = arrayOfNulls<Any>(2)

    fun createView(name: String, context: Context, attrs: AttributeSet): View? {
        val view = createViewFromInflater(context, name, attrs) ?: createViewFromTag(context, name, attrs)
        view?.apply {
            // If we have created a view, check it's android:onClick
            checkOnClickListener(this, attrs)
        }
        return view
    }

    private fun createViewFromInflater(context: Context, name: String, attrs: AttributeSet): View? {
        var view: View? = null
        for (inflater in SkinManager.inflaters) {
            view = inflater.createView(context, name, attrs)
            if (view == null) {
                continue
            } else {
                break
            }
        }
        return view
    }

    private fun createViewFromTag(context: Context, name: String, attrs: AttributeSet): View? {
        var tempName = name
        if ("view" == tempName) {
            tempName = attrs.getAttributeValue(null, "class")
        }

        try {
            mConstructorArgs[0] = context
            mConstructorArgs[1] = attrs

            if (-1 == tempName.indexOf('.')) {
                for (i in sClassPrefixList.indices) {
                    val view = createView(context, tempName, sClassPrefixList[i])
                    if (view != null) {
                        return view
                    }
                }
                return null
            } else {
                return createView(context, tempName, null)
            }
        } catch (e: Exception) {
            // We do not want to catch these, lets return null and let the actual LayoutInflater
            // try
            return null
        } finally {
            // Don't retain references on context.
            mConstructorArgs[0] = null
            mConstructorArgs[1] = null
        }
    }

    /**
     * android:onClick doesn't handle views with a ContextWrapper context. This method
     * backports new framework functionality to traverse the Context wrappers to find a
     * suitable target.
     */
    private fun checkOnClickListener(view: View, attrs: AttributeSet) {
        val context = view.context

        if (context !is ContextWrapper || Build.VERSION.SDK_INT >= 15 && !ViewCompat.hasOnClickListeners(view)) {
            // Skip our compat functionality if: the Context isn't a ContextWrapper, or
            // the view doesn't have an OnClickListener (we can only rely on this on API 15+ so
            // always use our compat code on older devices)
            return
        }

        val a = context.obtainStyledAttributes(attrs, sOnClickAttrs)
        val handlerName = a.getString(0)
        if (handlerName != null) {
            view.setOnClickListener(DeclaredOnClickListener(view, handlerName))
        }
        a.recycle()
    }

    private fun createView(context: Context, name: String, prefix: String?): View? {
        var constructor = sConstructorMap[name]

        try {
            if (constructor == null) {
                // Class not found in the cache, see if it's real, and try to add it
                val clazz = context.classLoader.loadClass(
                    if (prefix != null) prefix + name else name
                ).asSubclass(View::class.java)

                constructor = clazz.getConstructor(*sConstructorSignature)
                sConstructorMap[name] = constructor!!
            }
            constructor.isAccessible = true
            return constructor.newInstance(*mConstructorArgs)
        } catch (e: Exception) {
            e.printStackTrace()
            // We do not want to catch these, lets return null and let the actual LayoutInflater
            // try
            return null
        }

    }

    /**
     * An implementation of OnClickListener that attempts to lazily load a
     * named click handling method from a parent or ancestor context.
     */
    private class DeclaredOnClickListener(private val mHostView: View, private val mMethodName: String) :
        View.OnClickListener {

        private var mResolvedMethod: Method? = null
        private var mResolvedContext: Context? = null

        override fun onClick(v: View) {
            if (mResolvedMethod == null) {
                resolveMethod(mHostView.context)
            }

            try {
                mResolvedMethod!!.invoke(mResolvedContext, v)
            } catch (e: IllegalAccessException) {
                throw IllegalStateException(
                    "Could not execute non-public method for android:onClick", e
                )
            } catch (e: InvocationTargetException) {
                throw IllegalStateException(
                    "Could not execute method for android:onClick", e
                )
            }

        }

        private fun resolveMethod(context: Context?) {
            var tempContext = context
            while (tempContext != null) {
                try {
                    if (!tempContext.isRestricted) {
                        val method = tempContext.javaClass.getMethod(mMethodName, View::class.java)
                        mResolvedMethod = method
                        mResolvedContext = tempContext
                        return
                    }
                } catch (e: NoSuchMethodException) {
                    // Failed to find method, keep searching up the hierarchy.
                }

                tempContext = if (tempContext is ContextWrapper) {
                    tempContext.baseContext
                } else {
                    // Can't search up the hierarchy, null out and fail.
                    null
                }
            }

            val id = mHostView.id
            val idText = if (id == View.NO_ID)
                ""
            else
                " with id '" + mHostView.context.resources.getResourceEntryName(id) + "'"
            throw IllegalStateException(
                "Could not find method " + mMethodName
                        + "(View) in a parent or ancestor Context for android:onClick "
                        + "attribute defined on view " + mHostView.javaClass + idText
            )
        }
    }

    companion object {
        private val sConstructorSignature = arrayOf(Context::class.java, AttributeSet::class.java)
        private val sOnClickAttrs = intArrayOf(android.R.attr.onClick)

        private val sClassPrefixList = arrayOf("android.widget.", "android.view.", "android.webkit.")

        private val sConstructorMap = ArrayMap<String, Constructor<out View>>()
    }
}

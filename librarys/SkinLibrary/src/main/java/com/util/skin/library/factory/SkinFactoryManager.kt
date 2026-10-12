package com.util.skin.library.factory

import android.content.Context
import android.content.ContextWrapper
import android.util.AttributeSet
import android.view.View
import androidx.core.view.ViewCompat
import com.util.skin.library.SkinManager
import java.lang.reflect.Constructor
import java.lang.reflect.InvocationTargetException
import java.lang.reflect.Method
import java.util.concurrent.ConcurrentHashMap

/**
 * 视图膨胀拦截管理器。
 * 拦截 LayoutInflater 的 View 创建，支持自定义 Factory 转发以及原生 Tag/Class 反射解析与构造函数缓存。
 */
internal object SkinFactoryManager : SkinFactory {
    private val sConstructorSignature = arrayOf(Context::class.java, AttributeSet::class.java)
    private val sOnClickAttrs = intArrayOf(android.R.attr.onClick)

    private val sClassPrefixList = arrayOf("android.widget.", "android.view.", "android.webkit.")

    // 使用 ConcurrentHashMap 保证多线程/异步 View 膨胀时的并发安全与无锁高效查找
    private val sConstructorMap = ConcurrentHashMap<String, Constructor<out View>>()

    override fun onCreateView(name: String, context: Context, attrs: AttributeSet): View? {
        val view = createViewFromFactory(context, name, attrs) ?: createViewFromTag(context, name, attrs)
        view?.apply {
            checkOnClickListener(this, attrs)
        }
        return view
    }

    private fun createViewFromFactory(context: Context, name: String, attrs: AttributeSet): View? {
        return SkinManager.config
            .factories
            .firstNotNullOfOrNull { inflater ->
                inflater.onCreateView(name, context, attrs)
            }
    }

    private fun createViewFromTag(context: Context, name: String, attrs: AttributeSet): View? {
        var tempName = name
        if ("view" == tempName) {
            tempName = attrs.getAttributeValue(null, "class") ?: return null
        }

        try {
            if (!tempName.contains('.')) {
                for (cls in sClassPrefixList) {
                    val view = createView(context, tempName, cls, attrs)
                    if (view != null) return view
                }
                return null
            } else {
                return createView(context, tempName, null, attrs)
            }
        } catch (e: Exception) {
            return null
        }
    }

    private fun checkOnClickListener(view: View, attrs: AttributeSet) {
        val context = view.context

        if (context !is ContextWrapper || !ViewCompat.hasOnClickListeners(view)) {
            return
        }

        val a = context.obtainStyledAttributes(attrs, sOnClickAttrs)
        a.getString(0)?.let { handlerName ->
            view.setOnClickListener(DeclaredOnClickListener(view, handlerName))
        }
        a.recycle()
    }

    private fun createView(
        context: Context,
        name: String,
        prefix: String?,
        attrs: AttributeSet
    ): View? {
        val fullName = if (prefix != null) prefix + name else name
        try {
            val constructor = sConstructorMap[fullName] ?: run {
                val clazz = context.classLoader.loadClass(fullName).asSubclass(View::class.java)
                val ctor = clazz.getConstructor(*sConstructorSignature)
                ctor.isAccessible = true // 仅在反射放入缓存时执行一次 setup，提升后续调用性能
                sConstructorMap[fullName] = ctor
                ctor
            }
            return constructor.newInstance(context, attrs)
        } catch (e: Exception) {
            return null
        }
    }

    private class DeclaredOnClickListener(
        private val mHostView: View,
        private val mMethodName: String
    ) : View.OnClickListener {

        private var mResolvedMethod: Method? = null
        private var mResolvedContext: Context? = null

        override fun onClick(v: View) {
            if (mResolvedMethod == null) {
                resolveMethod(mHostView.context)
            }

            try {
                mResolvedMethod?.invoke(mResolvedContext, v)
            } catch (e: IllegalAccessException) {
                throw IllegalStateException("Could not execute non-public method for android:onClick", e)
            } catch (e: InvocationTargetException) {
                throw IllegalStateException("Could not execute method for android:onClick", e)
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
                    // Continue searching ancestor context
                }

                tempContext = if (tempContext is ContextWrapper) {
                    tempContext.baseContext
                } else {
                    null
                }
            }

            val id = mHostView.id
            val idText = if (id == View.NO_ID) "" else " with id '" + mHostView.context.resources.getResourceEntryName(id) + "'"
            throw IllegalStateException(
                "Could not find method " + mMethodName + "(View) in a parent or ancestor Context for android:onClick attribute defined on view " + mHostView.javaClass + idText
            )
        }
    }
}

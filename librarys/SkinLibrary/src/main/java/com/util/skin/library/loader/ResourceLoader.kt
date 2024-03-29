package com.util.skin.library.loader

import android.content.Context
import android.content.res.ColorStateList
import android.content.res.XmlResourceParser
import android.graphics.drawable.Drawable
import android.util.TypedValue
import androidx.annotation.AnyRes
import com.util.skin.library.model.SkinResource

/**
 * 资源加载
 * @PackageName com.util.skin.library.loader
 * @date 2022/8/26 16:48
 * @author zhanglei
 */
interface ResourceLoader {
    /**
     * 初始化.
     *
     * @param context  [Context]
     * @return 加载成功，返回皮肤包SkinResource；失败，则返回空。
     */
    fun initStrategy(context: Context): SkinResource?

    /**
     * 开发者可以拦截应用中的资源ID，返回对应color值。
     *
     * @param context  [Context]
     * @param resId    应用中需要换肤的资源ID.
     * @return 获得拦截后的颜色值，添加到ColorStateList的defaultColor中。不需要拦截，则返回空
     */
    fun getColor(context: Context, resId: Int): Int

    /**
     * 开发者可以拦截应用中的资源ID，返回对应ColorStateList。
     *
     * @param context  [Context]
     * @param resId    应用中需要换肤的资源ID.
     * @return 返回对应ColorStateList。不需要拦截，则返回空
     */
    fun getColorStateList(context: Context, resId: Int): ColorStateList?

    /**
     * 开发者可以拦截应用中的资源ID，返回对应Drawable。
     *
     * @param context  [Context]
     * @param resId    应用中需要换肤的资源ID.
     * @return 返回对应Drawable。不需要拦截，则返回空
     */
    fun getDrawable(context: Context, resId: Int): Drawable?

    /**
     * Return an XmlResourceParser through which you can read a generic XML
     * resource for the given resource ID.
     *
     * <p>The XmlPullParser implementation returned here has some limited
     * functionality.  In particular, you can't change its input, and only
     * high-level parsing events are available (since the document was
     * pre-parsed for you at build time, which involved merging text and
     * stripping comments).
     *
     * @param resId The desired resource identifier, as generated by the aapt
     *           tool. This integer encodes the package, type, and resource
     *           entry. The value 0 is an invalid identifier.
     *
     * @return A new parser object through which you can read
     *         the XML data.
     *
     */
    fun getXml(context: Context, resId: Int): XmlResourceParser?

    /**
     * Return the raw data associated with a particular resource ID.
     *
     * @param resId The desired resource identifier, as generated by the aapt
     *           tool. This integer encodes the package, type, and resource
     *           entry. The value 0 is an invalid identifier.
     * @param outValue Object in which to place the resource data.
     * @param resolveRefs If true, a resource that is a reference to another
     *                    resource will be followed so that you receive the
     *                    actual final resource data.  If false, the TypedValue
     *                    will be filled in with the reference itself.
     *
     */
    fun getValue(context: Context, @AnyRes resId: Int, outValue: TypedValue, resolveRefs: Boolean)

}
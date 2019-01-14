package com.util.skin.library.utils

import android.graphics.drawable.Drawable

import java.lang.reflect.Method

object SkinCompatVersionUtils {
    private const val TAG = "SkinCompatUtils"
    // 27.1.0后删除
    private var sV4DrawableWrapperClass: Class<*>? = null
    private var sV4DrawableWrapperGetM: Method? = null
    private var sV4DrawableWrapperSetM: Method? = null
    // 27.1.0后增加
    private var sV4WrappedDrawableClass: Class<*>? = null
    private var sV4WrappedDrawableGetM: Method? = null
    private var sV4WrappedDrawableSetM: Method? = null

    private var sV7DrawableWrapperClass: Class<*>? = null
    private var sV7DrawableWrapperGetM: Method? = null
    private var sV7DrawableWrapperSetM: Method? = null

    init {
        try {
            sV4WrappedDrawableClass = Class.forName("android.support.v4.graphics.drawable.WrappedDrawable")
        } catch (e: ClassNotFoundException) {
            if (Slog.DEBUG) {
                Slog.i(TAG, "hasV4WrappedDrawable = false")
            }
        }

        try {
            sV4DrawableWrapperClass = Class.forName("android.support.v4.graphics.drawable.DrawableWrapper")
        } catch (e: ClassNotFoundException) {
            if (Slog.DEBUG) {
                Slog.i(TAG, "hasV4DrawableWrapper = false")
            }
        }

        try {
            sV7DrawableWrapperClass = Class.forName("android.support.v7.graphics.drawable.DrawableWrapper")
        } catch (e: ClassNotFoundException) {
            if (Slog.DEBUG) {
                Slog.i(TAG, "hasV7DrawableWrapper = false")
            }
        }

    }

    fun hasV4WrappedDrawable(): Boolean {
        return sV4WrappedDrawableClass != null
    }

    fun isV4WrappedDrawable(drawable: Drawable): Boolean {
        return sV4WrappedDrawableClass != null && sV4WrappedDrawableClass!!.isAssignableFrom(drawable.javaClass)
    }

    fun getV4WrappedDrawableWrappedDrawable(drawable: Drawable): Drawable {
        if (sV4WrappedDrawableClass != null) {
            if (sV4WrappedDrawableGetM == null) {
                try {
                    sV4WrappedDrawableGetM = sV4WrappedDrawableClass!!.getDeclaredMethod("getWrappedDrawable")
                    sV4WrappedDrawableGetM!!.isAccessible = true
                } catch (e: Exception) {
                    if (Slog.DEBUG) {
                        Slog.i(TAG, "getV4WrappedDrawableWrappedDrawable No Such Method")
                    }
                }

            }
            if (sV4WrappedDrawableGetM != null) {
                try {
                    return sV4WrappedDrawableGetM!!.invoke(drawable) as Drawable
                } catch (e: Exception) {
                    if (Slog.DEBUG) {
                        Slog.i(TAG, "getV4WrappedDrawableWrappedDrawable invoke error: $e")
                    }
                }

            }
        }
        return drawable
    }

    fun setV4WrappedDrawableWrappedDrawable(drawable: Drawable, inner: Drawable) {
        if (sV4WrappedDrawableClass != null) {
            if (sV4WrappedDrawableSetM == null) {
                try {
                    sV4WrappedDrawableSetM = sV4WrappedDrawableClass!!.getDeclaredMethod("setWrappedDrawable", Drawable::class.java)
                    sV4WrappedDrawableSetM!!.isAccessible = true
                } catch (e: Exception) {
                    if (Slog.DEBUG) {
                        Slog.i(TAG, "setV4WrappedDrawableWrappedDrawable No Such Method")
                    }
                }

            }
            if (sV4WrappedDrawableSetM != null) {
                try {
                    sV4WrappedDrawableSetM!!.invoke(drawable, inner)
                } catch (e: Exception) {
                    if (Slog.DEBUG) {
                        Slog.i(TAG, "setV4WrappedDrawableWrappedDrawable invoke error: $e")
                    }
                }

            }
        }
    }

    fun hasV4DrawableWrapper(): Boolean {
        return sV4DrawableWrapperClass != null
    }

    fun isV4DrawableWrapper(drawable: Drawable): Boolean {
        return sV4DrawableWrapperClass != null && sV4DrawableWrapperClass!!.isAssignableFrom(drawable.javaClass)
    }

    fun getV4DrawableWrapperWrappedDrawable(drawable: Drawable): Drawable {
        if (sV4DrawableWrapperClass != null) {
            if (sV4DrawableWrapperGetM == null) {
                try {
                    sV4DrawableWrapperGetM = sV4DrawableWrapperClass!!.getDeclaredMethod("getWrappedDrawable")
                    sV4DrawableWrapperGetM!!.isAccessible = true
                } catch (e: Exception) {
                    if (Slog.DEBUG) {
                        Slog.i(TAG, "getV4DrawableWrapperWrappedDrawable No Such Method")
                    }
                }

            }
            if (sV4DrawableWrapperGetM != null) {
                try {
                    return sV4DrawableWrapperGetM!!.invoke(drawable) as Drawable
                } catch (e: Exception) {
                    if (Slog.DEBUG) {
                        Slog.i(TAG, "getV4DrawableWrapperWrappedDrawable invoke error: $e")
                    }
                }

            }
        }
        return drawable
    }

    fun setV4DrawableWrapperWrappedDrawable(drawable: Drawable, inner: Drawable) {
        if (sV4DrawableWrapperClass != null) {
            if (sV4DrawableWrapperSetM == null) {
                try {
                    sV4DrawableWrapperSetM = sV4DrawableWrapperClass!!.getDeclaredMethod("setWrappedDrawable", Drawable::class.java)
                    sV4DrawableWrapperSetM!!.isAccessible = true
                } catch (e: Exception) {
                    if (Slog.DEBUG) {
                        Slog.i(TAG, "setV4DrawableWrapperWrappedDrawable No Such Method")
                    }
                }

            }
            if (sV4DrawableWrapperSetM != null) {
                try {
                    sV4DrawableWrapperSetM!!.invoke(drawable, inner)
                } catch (e: Exception) {
                    if (Slog.DEBUG) {
                        Slog.i(TAG, "setV4DrawableWrapperWrappedDrawable invoke error: $e")
                    }
                }

            }
        }
    }

    fun hasV7DrawableWrapper(): Boolean {
        return sV7DrawableWrapperClass != null
    }

    fun isV7DrawableWrapper(drawable: Drawable): Boolean {
        return sV7DrawableWrapperClass != null && sV7DrawableWrapperClass!!.isAssignableFrom(drawable.javaClass)
    }

    fun getV7DrawableWrapperWrappedDrawable(drawable: Drawable): Drawable {
        if (sV7DrawableWrapperClass != null) {
            if (sV7DrawableWrapperGetM == null) {
                try {
                    sV7DrawableWrapperGetM = sV7DrawableWrapperClass!!.getDeclaredMethod("getWrappedDrawable")
                    sV7DrawableWrapperGetM!!.isAccessible = true
                } catch (e: Exception) {
                    if (Slog.DEBUG) {
                        Slog.i(TAG, "getV7DrawableWrapperWrappedDrawable No Such Method")
                    }
                }

            }
            if (sV7DrawableWrapperGetM != null) {
                try {
                    return sV7DrawableWrapperGetM!!.invoke(drawable) as Drawable
                } catch (e: Exception) {
                    if (Slog.DEBUG) {
                        Slog.i(TAG, "getV7DrawableWrapperWrappedDrawable invoke error: $e")
                    }
                }

            }
        }
        return drawable
    }

    fun setV7DrawableWrapperWrappedDrawable(drawable: Drawable, inner: Drawable) {
        if (sV7DrawableWrapperClass != null) {
            if (sV7DrawableWrapperSetM == null) {
                try {
                    sV7DrawableWrapperSetM = sV7DrawableWrapperClass!!.getDeclaredMethod("setWrappedDrawable", Drawable::class.java)
                    sV7DrawableWrapperSetM!!.isAccessible = true
                } catch (e: Exception) {
                    if (Slog.DEBUG) {
                        Slog.i(TAG, "setV7DrawableWrapperWrappedDrawable No Such Method")
                    }
                }

            }
            if (sV7DrawableWrapperSetM != null) {
                try {
                    sV7DrawableWrapperSetM!!.invoke(drawable, inner)
                } catch (e: Exception) {
                    if (Slog.DEBUG) {
                        Slog.i(TAG, "setV7DrawableWrapperWrappedDrawable invoke error: $e")
                    }
                }

            }
        }
    }

}

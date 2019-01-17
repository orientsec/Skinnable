package com.util.skin.library.utils

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import com.util.skin.library.loader.SkinLoaderStrategyType

internal object SkinPreference {
    private lateinit var mPref: SharedPreferences
    private lateinit var mEditor: SharedPreferences.Editor

    private const val FILE_NAME = "meta-data"

    private const val KEY_SKIN_NAME = "skin-name"
    private const val KEY_SKIN_STRATEGY = "skin-strategy"
    private const val KEY_SKIN_USER_THEME = "skin-user-theme-json"

    @SuppressLint("CommitPrefEdits")
    fun init(context: Context) {
        mPref = context.getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE)
        mEditor = mPref.edit()
    }

    val skinName: String
        get() = mPref.getString(KEY_SKIN_NAME, "") ?: ""

    val skinStrategy: Int
        get() = mPref.getInt(KEY_SKIN_STRATEGY, SkinLoaderStrategyType.Default.type)

    val userTheme: String
        get() = mPref.getString(KEY_SKIN_USER_THEME, "") ?: ""

    fun setSkinName(skinName: String): SkinPreference {
        mEditor.putString(KEY_SKIN_NAME, skinName)
        return this
    }

    fun setSkinStrategy(strategy: SkinLoaderStrategyType): SkinPreference {
        mEditor.putInt(KEY_SKIN_STRATEGY, strategy.type)
        return this
    }

    fun setUserTheme(themeJson: String): SkinPreference {
        mEditor.putString(KEY_SKIN_USER_THEME, themeJson)
        return this
    }

    fun commitEditor() {
        mEditor.apply()
    }
}

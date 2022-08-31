package com.util.skin.library.utils

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import com.util.skin.library.loader.SkinStrategy

internal object SkinPreference {
    private lateinit var mPref: SharedPreferences
    private lateinit var mEditor: SharedPreferences.Editor

    private const val FILE_NAME = "meta-data"

    private const val KEY_SKIN_USER_THEME = "skin-user-theme-json"
    private const val KEY_SKIN_RESOURCES = "key_skin_resources"

    @SuppressLint("CommitPrefEdits")
    fun init(context: Context) {
        mPref = context.getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE)
        mEditor = mPref.edit()
    }

    val userTheme: String
        get() = mPref.getString(KEY_SKIN_USER_THEME, "") ?: ""

    private val keyRes: String
        get() = mPref.getString(KEY_SKIN_RESOURCES, "") ?: ""

    /**
     * 获取当前换肤策略
     */
    fun strategies(): HashSet<SkinStrategy> {
        val entrySet = hashSetOf<SkinStrategy>()
        val set = keyRes.split(";").toHashSet()
        set.forEach { value ->
            if (value.isNotBlank() && value.contains(":")) {
                val params = value.split(":")
                entrySet.add(
                    initialStrategy(params[0], params[1])
                )
            }
        }
        return entrySet
    }

    fun setUserTheme(themeJson: String): SkinPreference {
        mEditor.putString(KEY_SKIN_USER_THEME, themeJson)
        return this
    }

    fun addResources(value: String): SkinPreference {
        val set = keyRes.split(";").toHashSet()
        if (set.contains(value)) return this
        set.add(value)
        val saveValue = set.fold("") { acc, s -> "$acc$s;" }
        mEditor.putString(KEY_SKIN_RESOURCES, saveValue)
        return this
    }

    fun removeResources(value: String): SkinPreference {
        val set = keyRes.split(";").toHashSet()
        if (!set.contains(value)) return this
        set.remove(value)
        val saveValue = set.fold("") { acc, s -> "$acc$s;" }
        mEditor.putString(KEY_SKIN_RESOURCES, saveValue)
        return this
    }

    fun resetResources(): SkinPreference {
        mEditor.putString(KEY_SKIN_RESOURCES, "")
        return this
    }

    fun commitEditor() {
        mEditor.apply()
    }

    private fun initialStrategy(skinName: String, simpleName: String): SkinStrategy {
        return when (simpleName) {
            SkinStrategy.Assets::class.java.simpleName -> SkinStrategy.Assets(skinName)
            SkinStrategy.BuildIn::class.java.simpleName -> SkinStrategy.BuildIn(skinName)
            SkinStrategy.PrefixBuildIn::class.java.simpleName -> SkinStrategy.PrefixBuildIn(skinName)
            SkinStrategy.SDCard::class.java.simpleName -> SkinStrategy.SDCard(skinName)
            SkinStrategy.Zip::class.java.simpleName -> SkinStrategy.Zip(skinName)
            else -> throw IllegalArgumentException("not have name $simpleName")
        }
    }
}

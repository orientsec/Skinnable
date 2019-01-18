package com.util.skin.library.utils

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import com.util.skin.library.loader.SkinLoaderStrategyType
import com.util.skin.library.model.ResEntry

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

    suspend fun getEntries(): HashSet<ResEntry> {
        val entrySet = hashSetOf<ResEntry>()
        val set = keyRes.split(";").toHashSet()
        set.forEach { value ->
            if (value.isNotBlank() && value.contains(":")) {
                val params = value.split(":")
                entrySet.add(ResEntry(params[0], SkinLoaderStrategyType.parseType(params[1].toInt())))
            }
        }
        return entrySet
    }

    fun setUserTheme(themeJson: String): SkinPreference {
        mEditor.putString(KEY_SKIN_USER_THEME, themeJson)
        return this
    }

    suspend fun addResources(value: String): SkinPreference {
        val set = keyRes.split(";").toHashSet()
        if (set.contains(value)) return this
        set.add(value)
        val saveValue = set.fold("") { acc, s -> "$acc$s;" }
        mEditor.putString(KEY_SKIN_RESOURCES, saveValue)
        return this
    }

    suspend fun removeResources(value: String): SkinPreference {
        val set = keyRes.split(";").toHashSet()
        if (!set.contains(value)) return this
        set.remove(value)
        val saveValue = set.fold("") { acc, s -> "$acc$s;" }
        mEditor.putString(KEY_SKIN_RESOURCES, saveValue)
        return this
    }

    suspend fun resetResources(): SkinPreference {
        mEditor.putString(KEY_SKIN_RESOURCES, "")
        return this
    }

    fun commitEditor() {
        mEditor.apply()
    }
}

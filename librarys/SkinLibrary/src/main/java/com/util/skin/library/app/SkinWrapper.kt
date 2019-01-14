package com.util.skin.library.app

import android.content.Context
import android.util.AttributeSet
import android.view.View

interface SkinWrapper {
    fun wrapContext(context: Context, parent: View?, attrs: AttributeSet): Context
}

package com.util.skinnable.support.constraint

import android.content.Context
import android.util.AttributeSet
import android.view.View
import com.util.skin.library.app.SkinLayoutInflater

class SkinConstraintViewInflater : SkinLayoutInflater {
    override fun createView(context: Context, name: String, attrs: AttributeSet): View? {
        var view: View? = null
        when (name) {
            "androidx.constraintlayout.widget.ConstraintLayout" -> view = SkinCompatConstraintLayout(context, attrs)
            else -> {
            }
        }
        return view
    }
}

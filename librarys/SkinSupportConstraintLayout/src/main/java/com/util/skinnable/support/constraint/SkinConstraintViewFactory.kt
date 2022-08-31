package com.util.skinnable.support.constraint

import android.content.Context
import android.util.AttributeSet
import android.view.View
import com.util.skin.library.factory.SkinFactory

class SkinConstraintViewFactory : SkinFactory {
    override fun onCreateView(name: String, context: Context, attrs: AttributeSet): View? {
        return when (name) {
            "androidx.constraintlayout.widget.ConstraintLayout" -> SkinCompatConstraintLayout(
                context,
                attrs
            )
            else -> null
        }
    }
}

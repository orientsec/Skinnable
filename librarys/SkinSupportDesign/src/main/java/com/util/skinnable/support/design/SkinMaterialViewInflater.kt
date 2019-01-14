package com.util.skinnable.support.design

import android.content.Context
import android.util.AttributeSet
import android.view.View
import com.util.skin.library.app.SkinLayoutInflater
import com.util.skinnable.support.design.widget.*

class SkinMaterialViewInflater : SkinLayoutInflater {
    override fun createView(context: Context, name: String, attrs: AttributeSet): View? {
        if ("androidx.coordinatorlayout.widget.CoordinatorLayout" == name) {
            return SkinMaterialCoordinatorLayout(context, attrs)
        }
        if (!name.startsWith("com.google.android.material.")) {
            return null
        }
        var view: View? = null
        when (name) {
            "com.google.android.material.appbar.AppBarLayout" -> view = SkinMaterialAppBarLayout(context, attrs)
            "com.google.android.material.tabs.TabLayout" -> view = SkinMaterialTabLayout(context, attrs)
            "com.google.android.material.textfield.TextInputLayout" -> view =
                    SkinMaterialTextInputLayout(context, attrs)
            "com.google.android.material.textfield.TextInputEditText" -> view =
                    SkinMaterialTextInputEditText(context, attrs)
            "com.google.android.material.navigation.NavigationView" -> view = SkinMaterialNavigationView(context, attrs)
            "com.google.android.material.floatingactionbutton.FloatingActionButton" -> view =
                    SkinMaterialFloatingActionButton(context, attrs)
            "com.google.android.material.bottomnavigation.BottomNavigationView" -> view =
                    SkinMaterialBottomNavigationView(context, attrs)
            "com.google.android.material.appbar.CollapsingToolbarLayout" -> view =
                    SkinMaterialCollapsingToolbarLayout(context, attrs)
            else -> {
            }
        }
        return view
    }
}

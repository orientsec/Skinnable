package com.util.skinnable.support.design

import android.content.Context
import android.util.AttributeSet
import android.view.View
import com.util.skin.library.factory.SkinFactory
import com.util.skinnable.support.design.widget.*

class SkinMaterialViewFactory : SkinFactory {
    override fun onCreateView(name: String, context: Context, attrs: AttributeSet): View? {
        if ("androidx.coordinatorlayout.widget.CoordinatorLayout" == name) {
            return SkinMaterialCoordinatorLayout(context, attrs)
        }
        if (!name.startsWith("com.google.android.material.")) {
            return null
        }
        return when (name) {
            "com.google.android.material.appbar.AppBarLayout" ->
                SkinMaterialAppBarLayout(context, attrs)
            "com.google.android.material.tabs.TabLayout" ->
                SkinMaterialTabLayout(context, attrs)
            "com.google.android.material.textfield.TextInputLayout" ->
                SkinMaterialTextInputLayout(context, attrs)
            "com.google.android.material.textfield.TextInputEditText" ->
                SkinMaterialTextInputEditText(context, attrs)
            "com.google.android.material.navigation.NavigationView" ->
                SkinMaterialNavigationView(context, attrs)
            "com.google.android.material.floatingactionbutton.FloatingActionButton" ->
                SkinMaterialFloatingActionButton(context, attrs)
            "com.google.android.material.bottomnavigation.BottomNavigationView" ->
                SkinMaterialBottomNavigationView(context, attrs)
            "com.google.android.material.appbar.CollapsingToolbarLayout" ->
                SkinMaterialCollapsingToolbarLayout(context, attrs)
            else -> null
        }
    }
}

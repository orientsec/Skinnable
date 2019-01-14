package com.util.skinnable.skinnable.main.app

import android.app.Application
import com.util.skin.library.SkinManager
import com.util.skinnable.support.compat.SkinAppCompatViewInflater
import com.util.skinnable.support.constraint.SkinConstraintViewInflater
import com.util.skinnable.support.design.SkinMaterialViewInflater

/**
 * @PackageName com.util.skinnable.skinnable.main.app
 * @date 2019/1/11 13:20
 * @author zhanglei
 */
class App : Application() {
    override fun onCreate() {
        super.onCreate()
        SkinManager.init(this)
            .addInflater(SkinAppCompatViewInflater())
            .addInflater(SkinConstraintViewInflater())
            .addInflater(SkinMaterialViewInflater())
            .loadSkin()
    }
}
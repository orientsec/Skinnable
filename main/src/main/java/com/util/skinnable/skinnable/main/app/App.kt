package com.util.skinnable.skinnable.main.app

import android.app.Application
import com.util.skin.library.model.SkinConfig
import com.util.skinnable.support.compat.SkinAppCompatViewFactory
import com.util.skinnable.support.constraint.SkinConstraintViewFactory
import com.util.skinnable.support.design.SkinMaterialViewFactory

/**
 * @PackageName com.util.skinnable.skinnable.main.app
 * @date 2019/1/11 13:20
 * @author zhanglei
 */
class App : Application() {
    override fun onCreate() {
        super.onCreate()
        SkinConfig.builder()
            .addInflater(SkinAppCompatViewFactory())
            .addInflater(SkinConstraintViewFactory())
            .addInflater(SkinMaterialViewFactory())
            .build()
            .initManager(this)
            .loadSkin()
    }
}
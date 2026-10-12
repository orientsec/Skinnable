package com.util.skinnable.skinnable.main.app

import android.app.Application
import android.widget.Toast
import com.util.skin.library.exception.SkinErrorListener
import com.util.skin.library.exception.SkinErrorType
import com.util.skin.library.model.SkinConfig
import com.util.skin.library.utils.Slog
import com.util.skinnable.support.compat.SkinAppCompatViewFactory
import com.util.skinnable.support.constraint.SkinConstraintViewFactory
import com.util.skinnable.support.design.SkinMaterialViewFactory

class App : Application() {
    override fun onCreate() {
        super.onCreate()
        SkinConfig.builder()
            .addInflater(SkinAppCompatViewFactory())
            .addInflater(SkinConstraintViewFactory())
            .addInflater(SkinMaterialViewFactory())
            .setEnableAutoFallback(true)
            .setMaxErrorThreshold(5)
            .setErrorListener(object : SkinErrorListener {
                override fun onError(type: SkinErrorType, message: String, cause: Throwable?) {
                    Slog.e("AppSkinMonitor", "Skin Exception [$type]: $message", cause)
                }

                override fun onFallbackToDefault(reason: String) {
                    Slog.w("AppSkinMonitor", "Skin Fallback Triggered: $reason")
                    Toast.makeText(this@App, "换肤异常: $reason", Toast.LENGTH_LONG).show()
                }
            })
            .build()
            .initManager(this)
            .loadSkin()
    }
}

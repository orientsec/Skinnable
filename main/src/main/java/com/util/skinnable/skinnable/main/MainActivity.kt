package com.util.skinnable.skinnable.main

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.util.skin.library.SkinManager
import com.util.skin.library.loader.SkinLoaderStrategyType
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    private var loadDefault = true
    private var loadDefault2 = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        tv_text1.setOnClickListener {
            if (loadDefault) {
                SkinManager.loadSkin("skin.night", SkinLoaderStrategyType.Assets)
            } else {
                SkinManager.resetSkin("skin.night", SkinLoaderStrategyType.Assets)
            }
            loadDefault = !loadDefault
        }

        tv_text2.setOnClickListener {
            if (loadDefault2) {
                SkinManager.loadSkin("night", SkinLoaderStrategyType.BuildIn)
            } else {
                SkinManager.resetSkin("night", SkinLoaderStrategyType.BuildIn)
            }
            loadDefault2 = !loadDefault2
        }
        tv_text3.setOnClickListener {
            SkinManager.restoreDefaultSkin()
        }
    }
}

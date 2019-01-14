package com.util.skinnable.skinnable.main

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.util.skin.library.SkinManager
import com.util.skin.library.loader.SkinLoaderStrategyType
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    private var loadDefault = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        tv_text1.setOnClickListener {
            if (loadDefault) {
                SkinManager.loadSkin("skin.night", SkinLoaderStrategyType.Assets)
            } else {
                SkinManager.restoreDefaultSkin()
            }
            loadDefault = !loadDefault
        }
    }
}

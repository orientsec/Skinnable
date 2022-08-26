package com.util.skinnable.skinnable.main

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.tabs.TabLayout
import com.util.skin.library.SkinManager
import com.util.skin.library.loader.SkinLoaderStrategyType
import com.util.skinnable.skinnable.main.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private var _binding: ActivityMainBinding? = null
    private val binding: ActivityMainBinding
        get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.apply {
            tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
                override fun onTabSelected(tab: TabLayout.Tab?) {
                    when (tab!!.position) {
                        0 -> {
                            SkinManager.loadSkin("night", SkinLoaderStrategyType.BuildIn)
                        }
                        1 -> {
                            SkinManager.resetSkin("night", SkinLoaderStrategyType.BuildIn)
                        }
                        2 -> {
                            SkinManager.loadSkin("skin.night", SkinLoaderStrategyType.Assets)
                        }
                        3 -> {
                            SkinManager.resetSkin("skin.night", SkinLoaderStrategyType.Assets)
                        }
                        4 -> {
                            SkinManager.restoreDefaultSkin()
                        }
                    }
                }

                override fun onTabUnselected(tab: TabLayout.Tab?) {
                }

                override fun onTabReselected(tab: TabLayout.Tab?) {
                }
            })
        }

    }
}

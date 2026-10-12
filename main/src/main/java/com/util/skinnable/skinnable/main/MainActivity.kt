package com.util.skinnable.skinnable.main

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.tabs.TabLayout
import com.util.skin.library.SkinManager
import com.util.skin.library.loader.SkinStrategy
import com.util.skin.library.loadSkinSuspend
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
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
            tvText1.setOnClickListener {
                SkinManager.restoreDefaultSkin()
            }
            tvText2.setOnClickListener {
                lifecycleScope.launch {
                    SkinManager.loadSkinSuspend(SkinStrategy.BuildIn("night"))
                }
            }
            tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
                override fun onTabSelected(tab: TabLayout.Tab?) {
                    when (tab!!.position) {
                        0 -> {
                            SkinManager.restoreDefaultSkin()
                        }
                        1 -> {
                            lifecycleScope.launch {
                                SkinManager.loadSkinSuspend(SkinStrategy.BuildIn("night"))
                            }
                        }
                        2 -> {
                            lifecycleScope.launch {
                                SkinManager.loadSkinSuspend(SkinStrategy.PrefixBuildIn("night"))
                            }
                        }
                    }
                }

                override fun onTabUnselected(tab: TabLayout.Tab?) {
                }

                override fun onTabReselected(tab: TabLayout.Tab?) {
                }
            })
            
            // 动态添加一个 Fragment 来演示 SkinLifecycleObserver 的运作情况
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, TestFragment())
                .commit()
        }

    }
}

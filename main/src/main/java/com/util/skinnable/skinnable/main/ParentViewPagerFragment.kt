package com.util.skinnable.skinnable.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.google.android.material.tabs.TabLayoutMediator
import com.util.skinnable.skinnable.main.databinding.FragmentParentViewPagerBinding

class ParentViewPagerFragment : Fragment() {
    private var _binding: FragmentParentViewPagerBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentParentViewPagerBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val pages = listOf("Tab 1 (Child A)", "Tab 2 (Child B)", "Tab 3 (Child C)")

        binding.viewPager2Nested.adapter = object : FragmentStateAdapter(this) {
            override fun getItemCount(): Int = pages.size

            override fun createFragment(position: Int): Fragment {
                return ChildFragment.newInstance("Nested Page: ${pages[position]}")
            }
        }

        TabLayoutMediator(binding.tabLayoutNested, binding.viewPager2Nested) { tab, position ->
            tab.text = pages[position]
        }.attach()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
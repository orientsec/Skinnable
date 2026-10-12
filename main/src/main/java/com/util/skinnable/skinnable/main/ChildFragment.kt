package com.util.skinnable.skinnable.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.util.skin.library.utils.bindSkinColor
import com.util.skinnable.skinnable.main.databinding.FragmentChildBinding

class ChildFragment : Fragment() {
    private var _binding: FragmentChildBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentChildBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val title = arguments?.getString(ARG_TITLE) ?: "Child Fragment"
        binding.tvChildTitle.text = title

        // 演示 Feature 2: 代码动态换肤 DSL (bindSkinColor)
        binding.tvDynamicDsl.bindSkinColor(R.color.demo_primary) { color ->
            text = "DSL Dynamic Color Bound Text"
            setTextColor(color)
        }

        // 演示 Feature 1: RecyclerView 列表及其缓存池自动换肤刷新
        binding.rvChildList.layoutManager = LinearLayoutManager(requireContext())
        binding.rvChildList.adapter = object : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
            override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
                val tv = TextView(parent.context).apply {
                    setPadding(16, 16, 16, 16)
                    textSize = 14f
                    // 绑定 Item 格式及 DSL 换肤
                    bindSkinColor(R.color.demo_text) { c -> setTextColor(c) }
                }
                return object : RecyclerView.ViewHolder(tv) {}
            }

            override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
                (holder.itemView as TextView).text = "RecyclerView Item #$position ($title)"
            }

            override fun getItemCount(): Int = 5
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        private const val ARG_TITLE = "arg_title"
        fun newInstance(title: String): ChildFragment {
            return ChildFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_TITLE, title)
                }
            }
        }
    }
}
package com.android.tabcorder.ui.main

import androidx.lifecycle.ViewModelProvider
import com.android.tabcorder.base.BaseFragment
import com.android.tabcorder.databinding.FragmentMainBinding

class MainFragment : BaseFragment<FragmentMainBinding>(FragmentMainBinding::inflate) {

    private lateinit var viewModel: MainViewModel

    override fun initView() {
        super.initView()

        viewModel = ViewModelProvider(this)[MainViewModel::class.java]
    }

    companion object {
        fun newInstance() = MainFragment()
    }
}
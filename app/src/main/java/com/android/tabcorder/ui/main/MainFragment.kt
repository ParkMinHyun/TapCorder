package com.android.tabcorder.ui.main

import androidx.fragment.app.viewModels
import com.android.tabcorder.base.BaseFragment
import com.android.tabcorder.databinding.FragmentMainBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainFragment : BaseFragment<FragmentMainBinding>(FragmentMainBinding::inflate) {

    private val viewModel: MainViewModel by viewModels()

}
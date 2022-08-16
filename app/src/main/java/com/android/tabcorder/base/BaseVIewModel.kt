package com.android.tabcorder.base

import androidx.lifecycle.ViewModel

abstract class BaseViewModel : ViewModel() {
    open fun initViewModel() {
    }
}
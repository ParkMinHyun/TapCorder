package com.android.tapcorder.base

import androidx.lifecycle.ViewModel

abstract class BaseViewModel : ViewModel() {
    open fun initViewModel() {
    }
}
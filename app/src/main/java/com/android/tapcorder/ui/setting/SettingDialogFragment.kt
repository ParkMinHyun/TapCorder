package com.android.tapcorder.ui.setting

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.android.tapcorder.R
import com.android.tapcorder.databinding.FragmentSettingBinding
import com.android.tapcorder.repository.SettingRepository
import com.android.tapcorder.service.AudioRecordService
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class SettingDialogFragment : BottomSheetDialogFragment() {

    private lateinit var viewBinding: FragmentSettingBinding
    private lateinit var settingCallback: SettingCallback

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        viewBinding = FragmentSettingBinding.inflate(inflater)
        return viewBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initViews()
        setUpStartButton()
        setUpRecordingTime()
    }

    private fun initViews() {
        if (AudioRecordService.isServiceRunning) {
            for (i in 0 until viewBinding.recordTimeGroup.childCount) {
                viewBinding.recordTimeGroup.getChildAt(i).isEnabled = false
            }
            viewBinding.startRecordButton.text = "Stop"
        }
    }

    private fun setUpStartButton() {
        viewBinding.startRecordButton.setOnClickListener {
            if (AudioRecordService.isServiceRunning) {
                settingCallback.onStopped()
            } else {
                settingCallback.onStarted()
            }
            dismiss()

        }
    }

    private fun setUpRecordingTime() {
        when (SettingRepository.audioRecordTime) {
            30 -> viewBinding.recordTime30.isChecked = true
            60 -> viewBinding.recordTime60.isChecked = true
            180 -> viewBinding.recordTime180.isChecked = true
            300 -> viewBinding.recordTime300.isChecked = true
            else -> viewBinding.recordTime10.isChecked = true
        }

        viewBinding.recordTimeGroup.setOnCheckedChangeListener { _, checkedId ->
            when (checkedId) {
                R.id.recordTime10 -> SettingRepository.audioRecordTime = 10
                R.id.recordTime30 -> SettingRepository.audioRecordTime = 30
                R.id.recordTime60 -> SettingRepository.audioRecordTime = 60
                R.id.recordTime180 -> SettingRepository.audioRecordTime = 180
                R.id.recordTime300 -> SettingRepository.audioRecordTime = 300
            }
        }
    }

    fun setSettingCallback(settingCallback: SettingCallback) {
        this.settingCallback = settingCallback
    }

    interface SettingCallback {
        fun onStarted()

        fun onStopped()
    }
}
package com.android.tapcorder.ui.main

import android.annotation.SuppressLint
import android.content.Intent
import android.view.View
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.android.tapcorder.App
import com.android.tapcorder.base.BaseFragment
import com.android.tapcorder.databinding.FragmentMainBinding
import com.android.tapcorder.service.TapcorderService
import com.android.tapcorder.ui.audio.AudioRVAdapter
import com.android.tapcorder.ui.setting.SettingDialogFragment
import com.android.tapcorder.notification.NotificationAction
import dagger.hilt.android.AndroidEntryPoint
import java.io.File

@AndroidEntryPoint
class MainFragment : BaseFragment<FragmentMainBinding>(FragmentMainBinding::inflate) {

    private val viewModel: MainViewModel by viewModels()

    private lateinit var audioRVAdapter: AudioRVAdapter

    override fun initView() {
        super.initView()

        viewModel.recordedAudioLiveData.observe(viewLifecycleOwner) {
            audioRVAdapter.addItem(it)
        }
    }

    override fun setUpViews() {
        super.setUpViews()

        setUpRecordButton()
        setUpAudioRecyclerView()
        setUpSettingButton()
    }

    private fun setUpSettingButton() {
        val settingDialogTag = SettingDialogFragment::class.java.simpleName

        if (childFragmentManager.findFragmentByTag(settingDialogTag) != null) {
            return
        }

        viewBinding.settingButton.setOnClickListener {
            SettingDialogFragment().apply {
                setSettingCallback(object : SettingDialogFragment.SettingCallback{
                    override fun onStarted() {
                        startTapcorderService()
                        App.showToast("onStarted")
                    }

                    override fun onStopped() {
                        App.showToast("onStopped")
                    }
                })
            }.show(childFragmentManager, settingDialogTag)
        }
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private fun setUpRecordButton() {
//        viewBinding.audioRecordButton.setOnClickListener {
//            if (viewModel.isAudioRecording) {
//                viewBinding.audioRecordButton.setImageDrawable(App.getDrawableImage(R.drawable.ic_record))
//                viewModel.stopRecording()
//            } else {
//                viewBinding.audioRecordButton.setImageDrawable(App.getDrawableImage(R.drawable.ic_recording))
//                viewModel.startRecording()
//            }
//        }
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private fun setUpAudioRecyclerView() {
        audioRVAdapter = AudioRVAdapter().apply {
            setOnItemClickListener(object : AudioRVAdapter.OnIconClickListener {
                override fun onItemClick(view: View?, position: Int) {
                    val uriName = audioRVAdapter.audioDataList[position].toString()
                    viewModel.playAudio(File(uriName))
                }
            })
        }

        viewBinding.recyclerview.adapter = audioRVAdapter
        viewBinding.recyclerview.layoutManager = LinearLayoutManager(App.getContext()).apply {
            orientation = LinearLayoutManager.VERTICAL
        }
    }

    private fun startTapcorderService() {
        Intent(activity, TapcorderService::class.java).also {
            it.action = NotificationAction.START
            activity?.startForegroundService(it)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()

        viewModel.recordedAudioLiveData.removeObservers(viewLifecycleOwner)
    }
}
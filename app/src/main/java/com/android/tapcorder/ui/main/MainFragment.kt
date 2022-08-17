package com.android.tapcorder.ui.main

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.Uri
import android.util.Log
import android.view.View
import androidx.fragment.app.viewModels
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.android.tapcorder.App
import com.android.tapcorder.Constant.INTENT_AUDIO_FILE
import com.android.tapcorder.Constant.INTENT_NOTIFY_SAVE_AUDIO
import com.android.tapcorder.base.BaseFragment
import com.android.tapcorder.databinding.FragmentMainBinding
import com.android.tapcorder.notification.NotificationAction
import com.android.tapcorder.service.AudioRecordService
import com.android.tapcorder.ui.audio.AudioRVAdapter
import com.android.tapcorder.ui.setting.SettingDialogFragment
import com.android.tapcorder.util.ExtensionUtil.TAG
import dagger.hilt.android.AndroidEntryPoint
import java.io.File


@AndroidEntryPoint
class MainFragment : BaseFragment<FragmentMainBinding>(FragmentMainBinding::inflate) {

    private val viewModel: MainViewModel by viewModels()

    private lateinit var audioRVAdapter: AudioRVAdapter

    private val messageReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            with(Uri.parse(intent.getStringExtra(INTENT_AUDIO_FILE))) {
                Log.e(TAG, "onReceive - $this")
                audioRVAdapter.addItem(this)
            }
        }
    }

    override fun initView() {
        super.initView()

        LocalBroadcastManager.getInstance(App.getCurrentActivity()).registerReceiver(
            messageReceiver, IntentFilter(INTENT_NOTIFY_SAVE_AUDIO)
        )
    }

    override fun setUpViews() {
        super.setUpViews()

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
                        Log.i(TAG, "Audio Recording Started")
                        startService()
                    }

                    override fun onStopped() {
                        Log.i(TAG, "Audio Recording Stopped")
                        stopService()
                    }
                })
            }.show(childFragmentManager, settingDialogTag)
        }
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

    private fun startService() {
        activity?.startForegroundService(
            Intent(activity, AudioRecordService::class.java).apply {
                action = NotificationAction.START
            }
        )
    }

    private fun stopService() {
        activity?.startForegroundService(
            Intent(activity, AudioRecordService::class.java).apply {
                action = NotificationAction.STOP
            }
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()

        viewModel.recordedAudioLiveData.removeObservers(viewLifecycleOwner)
    }
}
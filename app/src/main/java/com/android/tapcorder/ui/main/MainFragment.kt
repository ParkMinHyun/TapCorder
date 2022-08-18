package com.android.tapcorder.ui.main

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.util.Log
import android.view.View
import androidx.core.content.FileProvider
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.android.tapcorder.App
import com.android.tapcorder.Constant
import com.android.tapcorder.Constant.INTENT_AUDIO_DATA
import com.android.tapcorder.Constant.INTENT_NOTIFY_SAVE_AUDIO
import com.android.tapcorder.base.BaseFragment
import com.android.tapcorder.data.audio.AudioData
import com.android.tapcorder.databinding.FragmentMainBinding
import com.android.tapcorder.notification.NotificationAction
import com.android.tapcorder.repository.AudioRepository
import com.android.tapcorder.service.AudioRecordService
import com.android.tapcorder.ui.audio.AudioDialogFragment
import com.android.tapcorder.ui.audio.AudioRVAdapter
import com.android.tapcorder.ui.setting.SettingDialogFragment
import com.android.tapcorder.util.ExtensionUtil.TAG
import com.android.tapcorder.util.FileUtil
import dagger.hilt.android.AndroidEntryPoint
import java.io.File


@AndroidEntryPoint
class MainFragment : BaseFragment<FragmentMainBinding>(FragmentMainBinding::inflate) {

    private val viewModel: MainViewModel by viewModels()

    private lateinit var audioRVAdapter: AudioRVAdapter

    private val messageReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val data = intent.getStringExtra(INTENT_AUDIO_DATA)!!

            Log.i(TAG, "onReceive - $data")
            audioRVAdapter.addItem(AudioRepository.getAudioData(data))

            view?.post {
                viewBinding.emptyText.isVisible = false
                viewBinding.recyclerview.scrollToPosition(0)
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
                        startService()
                    }

                    override fun onStopped() {
                        stopService()
                    }
                })
            }.show(childFragmentManager, settingDialogTag)
        }
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private fun setUpAudioRecyclerView() {
        audioRVAdapter = AudioRVAdapter().apply {
            setItemClickListener(object : AudioRVAdapter.ItemClickListener {
                override fun onExpanded(view: View?, position: Int) {
                    for (index in 0 until viewBinding.recyclerview.childCount) {
                        if (position == index) continue
                        val holder = viewBinding.recyclerview.findViewHolderForAdapterPosition(index)
                        audioRVAdapter.collapsedHolder(holder as AudioRVAdapter.AudioHolder)
                    }

                    val audioData = audioRVAdapter.audioDataList[position]
                    val audioFilePath = FileUtil.SAVE_FILE_DIR + "/" + audioData.name
                    viewModel.playAudio(File(audioFilePath)) {
                        viewModel.stopAudio()
                    }
                }

                override fun onCollapsed(view: View?, position: Int) {
                    viewModel.stopAudio()
                }
            })
            setItemLongCLickListener(object: AudioRVAdapter.ItemLongClickListener {
                override fun onItemLongClick(view: View?, position: Int) {
                    AudioDialogFragment().apply {
                        setAudioDialogListener(object: AudioDialogFragment.AudioDialogListener{
                            override fun onDialogChangeNameClick() {
                                changeAudioFileName(position)
                            }

                            override fun onDialogShareClick() {
                                shareAudioFile(audioRVAdapter.audioDataList[position])
                            }

                            override fun onDialogRemoveClick() {
                                removeAudioFile(position)
                            }
                        })
                    }.show(childFragmentManager, TAG)
                }
            })
        }

        if (audioRVAdapter.audioDataList.isNotEmpty()) {
            viewBinding.emptyText.isVisible = false
        }

        with(viewBinding.recyclerview) {
            adapter = audioRVAdapter
            layoutManager = LinearLayoutManager(App.getContext()).apply {
                orientation = LinearLayoutManager.VERTICAL
            }
        }
    }

    private fun changeAudioFileName(position: Int) {

    }

    private fun shareAudioFile(audioData: AudioData) {
        val audioFilePath = FileUtil.SAVE_FILE_DIR + "/" + audioData.name
        val internalFile = File(audioFilePath)
        val contentUri = FileProvider.getUriForFile(requireContext(), "${requireContext().packageName}.provider", internalFile)

        Intent(Intent.ACTION_SEND).apply {
            type = "audio/*"
            putExtra(Intent.EXTRA_STREAM, contentUri)
        }.also {
            startActivity(Intent.createChooser(it, Constant.INTENT_SHARE_AUDIO_FILE))
        }
    }

    private fun removeAudioFile(position: Int) {
        val audioFilePath = FileUtil.SAVE_FILE_DIR + "/" + audioRVAdapter.audioDataList[position].name
        FileUtil.deleteFilePath(audioFilePath)
        audioRVAdapter.removeItem(position)
    }

    private fun startService() {
        Log.i(TAG, "Audio Recording Started")

        activity?.startForegroundService(
            Intent(activity, AudioRecordService::class.java).apply {
                action = NotificationAction.START
            }
        )
    }

    private fun stopService() {
        Log.i(TAG, "Audio Recording Stopped")

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
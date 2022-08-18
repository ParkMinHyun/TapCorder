package com.android.tapcorder.ui.main

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.util.Log
import android.view.View
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
import com.android.tapcorder.data.player.PlayerDuration
import com.android.tapcorder.databinding.FragmentMainBinding
import com.android.tapcorder.repository.AudioRepository
import com.android.tapcorder.ui.audio.AudioNameChangeDialog
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

        viewModel.audioPlayLiveData.observe(viewLifecycleOwner) { durationData ->
            onAudioPlayProgressed(durationData)
        }
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
                        viewModel.startRecordService()
                    }

                    override fun onStopped() {
                        viewModel.stopRecordService()
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
                    for (holder in boundViewHolders) {
                        if (holder.adapterPosition == position) continue
                        holder.collapseView()
                    }

                    val audioData = audioRVAdapter.audioDataList[position]
                    val audioFilePath = FileUtil.SAVE_FILE_DIR + "/" + audioData.name
                    viewModel.playAudio(File(audioFilePath)) {
                        viewModel.stopAudio()
                        for (holder in boundViewHolders) {
                            holder.collapseView()
                        }
                    }
                }

                override fun onCollapsed(view: View?, position: Int) {
                    viewModel.stopAudio()
                }
            })
            setSeekBarTouchListener(object : AudioRVAdapter.SeekBarTouchListener{
                override fun onStartTrackingTouch() {
                    viewModel.pauseAudio()
                }

                override fun onStopTrackingTouch(progress: Int) {
                    viewModel.moveAudioPosition(progress)
                }

            })
            setAudioSettingListener(object : AudioRVAdapter.AudioSettingListener{
                override fun onAudioRenameTouch(adapterPosition: Int) {
                    changeAudioFileName(adapterPosition)
                }

                override fun onAudioShareTouch(adapterPosition: Int) {
                    val audioData = audioRVAdapter.audioDataList[adapterPosition]
                    val contentUri = FileUtil.getContentUri(audioData)

                    Intent(Intent.ACTION_SEND).apply {
                        type = "audio/*"
                        putExtra(Intent.EXTRA_STREAM, contentUri)
                    }.also {
                        activity?.startActivity(Intent.createChooser(it, Constant.INTENT_SHARE_AUDIO_FILE))
                    }
                }

                override fun onAudioDeleteTouch(adapterPosition: Int) {
                    removeAudioFile(adapterPosition)
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
        AudioNameChangeDialog().apply {
            setAudioNameChangeDialogListener(object: AudioNameChangeDialog.AudioNameChangeDialogListener{
                override fun onDialogPositiveClick(name: String) {
                    val audioFilePath = FileUtil.SAVE_FILE_DIR + "/"
                    val from = audioRVAdapter.audioDataList[position].name
                    val to = "$name.mp3"
                    val newFile = FileUtil.renameFile(audioFilePath, from, to)

                    audioRVAdapter.replaceItem(position, AudioData(
                        newFile.name,
                        audioRVAdapter.audioDataList[position].duration,
                        audioRVAdapter.audioDataList[position].date
                    ))

                    Log.i(TAG, "renameAudioFile - $to is saved")
                }
            })
            setHint(audioRVAdapter.audioDataList[position].name.replace(".mp3", ""))
        }.show(childFragmentManager, TAG)
    }

    private fun onAudioPlayProgressed(durationData: PlayerDuration) {
        Log.w(TAG, "onAudioPlayProgressed $durationData")
        audioRVAdapter.updateAudioProgress(durationData)
    }

    private fun removeAudioFile(position: Int) {
        val audioFilePath = FileUtil.SAVE_FILE_DIR + "/" + audioRVAdapter.audioDataList[position].name
        FileUtil.deleteFilePath(audioFilePath)
        audioRVAdapter.removeItem(position)
    }

    override fun onDestroyView() {
        super.onDestroyView()

        viewModel.audioPlayLiveData.removeObservers(viewLifecycleOwner)
    }
}
package com.android.tapcorder.ui.audio

import android.app.Dialog
import android.content.Context
import android.graphics.Point
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.android.tapcorder.R
import com.android.tapcorder.databinding.FragmentAudioSettingBinding

class AudioDialogFragment : DialogFragment() {
    private var _binding: FragmentAudioSettingBinding? = null
    private val binding get() = _binding!!

    private lateinit var audioSettingRVAdapter: AudioSettingRVAdapter
    private var listener: AudioDialogListener? = null

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        _binding = FragmentAudioSettingBinding.inflate(LayoutInflater.from(context))
        return activity?.let {
            audioSettingRVAdapter = AudioSettingRVAdapter().apply {
                setOnItemClickListener(object: AudioSettingRVAdapter.OnItemClickListener{
                    override fun onItemClick(view: View?, position: Int) {
                        when (position) {
                            0 -> listener?.onDialogChangeNameClick()
                            1 -> listener?.onDialogShareClick()
                            2 -> listener?.onDialogRemoveClick()
                        }
                        dismiss()
                    }
                })
            }
            with(binding.recyclerview) {
                adapter = audioSettingRVAdapter
                layoutManager = LinearLayoutManager(context).apply {
                    orientation = LinearLayoutManager.VERTICAL
                }
            }
            val builder = AlertDialog.Builder(it, R.style.WrapContentDialog)
            builder.setView(binding.root)
            builder.create()
        } ?: throw IllegalStateException("Activity cannot be null")
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    fun setAudioDialogListener(listener: AudioDialogListener) {
        this.listener = listener
    }

    interface AudioDialogListener {
        fun onDialogChangeNameClick()
        fun onDialogShareClick()
        fun onDialogRemoveClick()
    }
}
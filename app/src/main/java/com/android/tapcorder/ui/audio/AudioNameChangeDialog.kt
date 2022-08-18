package com.android.tapcorder.ui.audio

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import androidx.fragment.app.DialogFragment
import com.android.tapcorder.databinding.FragmentAudioNameSettingBinding

class AudioNameChangeDialog: DialogFragment() {
    private var _binding: FragmentAudioNameSettingBinding? = null
    private val binding get() = _binding!!

    private var listener: AudioNameChangeDialogListener? = null

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        _binding = FragmentAudioNameSettingBinding.inflate(LayoutInflater.from(context))
        return activity?.let {
            val builder = AlertDialog.Builder(it)
            builder.setView(binding.root)
                .setPositiveButton("확인") { _, _ ->
                    listener?.onDialogPositiveClick(binding.name.text.toString())
                }
                .setNegativeButton("취소", null)
            builder.create()
        } ?: throw IllegalStateException("Activity cannot be null")
    }

    interface AudioNameChangeDialogListener {
        fun onDialogPositiveClick(name: String)
    }

    fun setAudioNameChangeDialogListener(listener: AudioNameChangeDialogListener) {
        this.listener = listener
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
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
    private var hint: String = "파일 이름"

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        _binding = FragmentAudioNameSettingBinding.inflate(LayoutInflater.from(context))
        return activity?.let {
            val builder = AlertDialog.Builder(it)
            binding.btnOk.setOnClickListener { _ ->
                listener?.onDialogPositiveClick(binding.name.text.toString())
                dismiss()
            }
            binding.btnCancel.setOnClickListener { _-> dismiss() }
            binding.name.hint = hint
            builder.setTitle("음성 파일 이름 변경")
            builder.setView(binding.root)
            builder.create()
        } ?: throw IllegalStateException("Activity cannot be null")
    }

    interface AudioNameChangeDialogListener {
        fun onDialogPositiveClick(name: String)
    }

    fun setAudioNameChangeDialogListener(listener: AudioNameChangeDialogListener) {
        this.listener = listener
    }

    fun setHint(hint: String) {
        this.hint = hint
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
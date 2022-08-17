package com.android.tapcorder.ui.audio

import android.annotation.SuppressLint
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.android.tapcorder.R
import com.android.tapcorder.data.AudioDB
import com.android.tapcorder.data.AudioData
import com.android.tapcorder.ui.custom.ScalableImageButton
import com.android.tapcorder.util.ExtensionUtil.TAG
import com.android.tapcorder.util.ExtensionUtil.toMinuteFormat

class AudioRVAdapter : RecyclerView.Adapter<AudioRVAdapter.AudioHolder>() {

    val audioDataList: ArrayList<AudioData> = arrayListOf()
    private var listener: OnIconClickListener? = null
    private var itemLongClickListener: OnItemLongClickListener? = null

    init {
        audioDataList.addAll(AudioDB.getSavedAudioData())
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AudioHolder {
        return AudioHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.layout_audio_holder, parent, false)
        )
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: AudioHolder, position: Int) {
        val audioData = audioDataList[position]

        holder.audioName.text = audioData.name
        holder.audioDate.text = audioData.date
        holder.audioDuration.text = audioData.duration.toMinuteFormat()
    }

    @Synchronized
    fun addItem(audioData: AudioData) {
        Log.i(TAG, "addItem - $audioData")
        audioDataList.add(audioData)
        notifyItemInserted(itemCount)
    }

    @Synchronized
    override fun getItemCount(): Int {
        return audioDataList.size
    }

    fun setOnItemClickListener(listener: OnIconClickListener?) {
        this.listener = listener
    }

    fun setOnItemLongCLickListener(itemLongClickListener: OnItemLongClickListener?) {
        this.itemLongClickListener = itemLongClickListener
    }

    interface OnIconClickListener {
        fun onItemClick(view: View?, position: Int)
    }

    interface OnItemLongClickListener {
        fun onItemLongClickListener(view: View?, position: Int)
    }

    inner class AudioHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var audioImage: ScalableImageButton
        var audioName: TextView
        var audioDuration: TextView
        var audioDate: TextView

        init {
            audioImage = itemView.findViewById(R.id.audio_state_image)
            audioName = itemView.findViewById(R.id.audio_name)
            audioDuration = itemView.findViewById(R.id.audio_duration)
            audioDate = itemView.findViewById(R.id.audio_date)

            audioImage.setOnClickListener { view ->
                val pos = adapterPosition
                if (pos != RecyclerView.NO_POSITION) {
                    listener?.onItemClick(view, pos)
                }
            }
            itemView.setOnLongClickListener { view ->
                val pos = adapterPosition
                if (pos != RecyclerView.NO_POSITION) {
                    itemLongClickListener?.onItemLongClickListener(view, pos)
                }
                false
            }
        }
    }
}
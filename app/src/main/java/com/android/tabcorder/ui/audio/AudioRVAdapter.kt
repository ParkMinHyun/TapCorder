package com.android.tabcorder.ui.audio

import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.android.tabcorder.R
import com.android.tabcorder.util.FileUtil
import java.io.File
import java.lang.String.valueOf

class AudioRVAdapter : RecyclerView.Adapter<AudioRVAdapter.AudioHolder>() {

    val audioDataList: ArrayList<Uri> = arrayListOf()
    private var listener: OnIconClickListener? = null

    init {
        audioDataList.addAll(FileUtil.getSavedAudioUris())
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AudioHolder {
        return AudioHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.item_audio, parent, false)
        )
    }

    override fun onBindViewHolder(holder: AudioHolder, position: Int) {
        val uriName = valueOf(audioDataList[position])
        val file = File(uriName)
        holder.audioTitle.text = file.name
    }

    @Synchronized
    fun addItem(uri: Uri) {
        audioDataList.add(uri)
        notifyItemInserted(itemCount)
    }

    @Synchronized
    override fun getItemCount(): Int {
        return audioDataList.size
    }

    fun setOnItemClickListener(listener: OnIconClickListener?) {
        this.listener = listener
    }

    interface OnIconClickListener {
        fun onItemClick(view: View?, position: Int)
    }

    inner class AudioHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var audioBtn: ImageButton
        var audioTitle: TextView

        init {
            audioBtn = itemView.findViewById(R.id.playBtn_itemAudio)
            audioTitle = itemView.findViewById(R.id.audioTitle_itemAudio)
            audioBtn.setOnClickListener { view ->
                val pos = adapterPosition
                if (pos != RecyclerView.NO_POSITION) {
                    listener?.onItemClick(view, pos)
                }
            }
        }
    }
}
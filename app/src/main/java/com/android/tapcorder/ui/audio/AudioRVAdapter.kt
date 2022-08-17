package com.android.tapcorder.ui.audio

import android.net.Uri
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.android.tapcorder.R
import com.android.tapcorder.util.ExtensionUtil.TAG
import com.android.tapcorder.util.FileUtil
import java.io.File
import java.lang.String.valueOf

class AudioRVAdapter : RecyclerView.Adapter<AudioRVAdapter.AudioHolder>() {

    val audioDataList: ArrayList<Uri> = arrayListOf()
    private var listener: OnIconClickListener? = null
    private var itemLongClickListener: OnItemLongClickListener? = null

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
        Log.i(TAG, "addItem - $uri")
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
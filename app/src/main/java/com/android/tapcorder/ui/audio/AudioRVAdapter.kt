package com.android.tapcorder.ui.audio

import android.annotation.SuppressLint
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.android.tapcorder.R
import com.android.tapcorder.data.audio.AudioData
import com.android.tapcorder.repository.AudioRepository
import com.android.tapcorder.ui.custom.ScalableImageButton
import com.android.tapcorder.util.ExtensionUtil.TAG
import com.android.tapcorder.util.ExtensionUtil.toMinuteFormat
import com.skydoves.expandablelayout.ExpandableLayout

class AudioRVAdapter : RecyclerView.Adapter<AudioRVAdapter.AudioHolder>() {

    val audioDataList: ArrayList<AudioData> = arrayListOf()
    private var itemClickListener: ItemClickListener? = null
    private var itemLongClickListener: ItemLongClickListener? = null

    init {
        for (audioData in AudioRepository.getSavedAudioData()) {
            audioDataList.add(0, audioData)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AudioHolder {
        val inflater = LayoutInflater.from(parent.context)
        val holderView = inflater.inflate(R.layout.layout_audio_holder, parent, false)
        return AudioHolder(holderView)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: AudioHolder, position: Int) {
        val audioData = audioDataList[position]

        holder.audioName.text = audioData.name.split('.').first()
        holder.audioDate.text = audioData.date
        holder.audioDuration.text = audioData.duration.toMinuteFormat()
    }

    @Synchronized
    fun addItem(audioData: AudioData) {
        Log.i(TAG, "addItem - $audioData")

        audioDataList.add(0, audioData)
        notifyItemInserted(0)
    }

    @Synchronized
    fun removeItem(position: Int) {
        val audioData = audioDataList[position]
        AudioRepository.deleteAudioData(audioData)
        audioDataList.remove(audioData)
        notifyItemRemoved(position)
    }

    fun collapsedHolder(holder: AudioHolder) {
        holder.collapseView()
    }

    @Synchronized
    fun replaceItem(position: Int, audioData: AudioData) {
        val oldAudioData = audioDataList[position]
        AudioRepository.deleteAudioData(oldAudioData)
        AudioRepository.insertAudioData(audioData)
        audioDataList[position] = audioData
        notifyItemChanged(position)
    }

    @Synchronized
    override fun getItemCount(): Int {
        return audioDataList.size
    }

    fun setItemClickListener(itemClickedListener: ItemClickListener?) {
        this.itemClickListener = itemClickedListener
    }

    fun setItemLongCLickListener(itemLongClickListener: ItemLongClickListener?) {
        this.itemLongClickListener = itemLongClickListener
    }

    interface ItemClickListener {
        fun onExpanded(view: View?, position: Int)

        fun onCollapsed(view: View?, position: Int)
    }

    interface ItemLongClickListener {
        fun onItemLongClick(view: View?, position: Int)
    }

    inner class AudioHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var holderTitleView: View
        var audioImage: ScalableImageButton
        var audioName: TextView
        var audioDuration: TextView
        var audioDate: TextView
        var expandableLayout: ExpandableLayout

        init {
            holderTitleView = itemView.findViewById(R.id.holder_title_view)
            expandableLayout = itemView.findViewById(R.id.expandable_layout)
            audioImage = itemView.findViewById(R.id.audio_state_image)
            audioName = itemView.findViewById(R.id.audio_name)
            audioDuration = itemView.findViewById(R.id.audio_duration)
            audioDate = itemView.findViewById(R.id.audio_date)

            setUpHolderViews()
        }

        private fun setUpHolderViews() {
            audioImage.setOnClickListener { processViewClickEvent(it) }

            holderTitleView.setOnClickListener { processViewClickEvent(it) }
            holderTitleView.setOnLongClickListener { view ->
                val pos = adapterPosition
                if (pos != RecyclerView.NO_POSITION) {
                    itemLongClickListener?.onItemLongClick(view, pos)
                }
                false
            }
        }

        private fun processViewClickEvent(view: View) {
            if (expandableLayout.isExpanded) {
                collapseView()
                itemClickListener?.onCollapsed(view, adapterPosition)
            } else {
                expandView()
                itemClickListener?.onExpanded(view, adapterPosition)
            }
        }

        fun collapseView() {
            expandableLayout.collapse()
            audioImage.setBackgroundResource(R.drawable.ic_play)
        }

        fun expandView() {
            expandableLayout.expand()
            audioImage.setBackgroundResource(R.drawable.ic_pause)
        }
    }
}
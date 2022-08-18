package com.android.tapcorder.ui.audio

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.android.tapcorder.R

class AudioSettingRVAdapter: RecyclerView.Adapter<AudioSettingRVAdapter.AudioSettingHolder>() {
    private val audioSettingOptionImgList: ArrayList<Int> = arrayListOf()
    private val audioSettingOptionTxtList: ArrayList<String> = arrayListOf()

    private var listener: OnItemClickListener? = null

    init {
        audioSettingOptionImgList.add(R.drawable.ic_edit)
        audioSettingOptionTxtList.add("이름 변경")
        audioSettingOptionImgList.add(R.drawable.ic_share)
        audioSettingOptionTxtList.add("음성 공유")
        audioSettingOptionImgList.add(R.drawable.ic_delete)
        audioSettingOptionTxtList.add("음성 삭제")
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AudioSettingHolder {
        return AudioSettingHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.layout_audio_setting_holder, parent, false)
        )
    }

    override fun onBindViewHolder(holder: AudioSettingHolder, position: Int) {
        holder.optionImage.setImageResource(audioSettingOptionImgList[position])
        holder.optionTitle.text = audioSettingOptionTxtList[position]
    }

    override fun getItemCount(): Int {
        return audioSettingOptionTxtList.size
    }

    fun setOnItemClickListener(listener: OnItemClickListener?) {
        this.listener = listener
    }

    interface OnItemClickListener {
        fun onItemClick(view: View?, position: Int)
    }

    inner class AudioSettingHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var optionImage: ImageView
        var optionTitle: TextView

        init {
            optionImage = itemView.findViewById(R.id.iv_option)
            optionTitle = itemView.findViewById(R.id.tv_option)

            itemView.setOnClickListener { view ->
                val pos = adapterPosition
                if (pos != RecyclerView.NO_POSITION) {
                    listener?.onItemClick(view, pos)
                }
            }
        }
    }
}
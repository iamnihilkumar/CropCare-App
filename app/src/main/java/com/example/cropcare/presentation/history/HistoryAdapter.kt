package com.example.cropcare.presentation.history

import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.cropcare.R
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide

class HistoryAdapter(
    private val list: List<HistoryModel>,
    private val onItemClick: (HistoryModel) -> Unit
) : RecyclerView.Adapter<HistoryAdapter.ViewHolder>() {

    class ViewHolder(inflater: LayoutInflater, parent: ViewGroup) :
        RecyclerView.ViewHolder(inflater.inflate(R.layout.item_history, parent, false)) {

        val img: ImageView = itemView.findViewById(R.id.imgHistory)
        val tvDisease: TextView = itemView.findViewById(R.id.tvHistoryDisease)
        val tvConfidence: TextView = itemView.findViewById(R.id.tvHistoryConfidence)
        val tvDate: TextView = itemView.findViewById(R.id.tvHistoryDate)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(parent.context), parent)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = list[position]

        val uri = item.imageUri.let { Uri.parse(it) }

        Glide.with(holder.itemView.context)
            .load(uri)
            .placeholder(R.drawable.ic_image_placeholder)
            .error(R.drawable.ic_broken_image)
            .into(holder.img)

        holder.tvDisease.text = item.disease.ifBlank { "Unknown" }
        holder.tvConfidence.text = "Confidence: ${item.confidence}"
        holder.tvDate.text = item.date

        holder.itemView.setOnClickListener { onItemClick(item) }
    }

    override fun getItemCount(): Int = list.size
}

package com.example.cropcare.presentation.news

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.cropcare.R

class NewsAdapter(
    private val list: List<NewsModel>,
    private val onClick: (String) -> Unit
) : RecyclerView.Adapter<NewsAdapter.ViewHolder>() {

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvTitle: TextView = itemView.findViewById(R.id.tvNewsTitle)
        val tvDesc: TextView = itemView.findViewById(R.id.tvNewsDesc)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_news, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val news = list[position]

        holder.tvTitle.text = news.title
        holder.tvDesc.text = news.description

        holder.itemView.setOnClickListener {
            onClick(news.link)
        }
    }

    override fun getItemCount(): Int = list.size
}

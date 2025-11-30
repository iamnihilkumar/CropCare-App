package com.example.cropcare.presentation.news

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.cropcare.R

class NewsActivity : AppCompatActivity() {

    private val newsList = ArrayList<NewsModel>()
    private lateinit var adapter: NewsAdapter
    private lateinit var recyclerNews: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_news)

        recyclerNews = findViewById(R.id.recyclerNews)
        recyclerNews.layoutManager = LinearLayoutManager(this)

        loadNewsItem()

        adapter = NewsAdapter(newsList) { link ->
            // open link in external browser
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(link))
            startActivity(intent)
        }

        recyclerNews.adapter = adapter
    }

    private fun loadNewsItem() {
        newsList.add(
            NewsModel(
                "Visit AgriNews.in",
                "Latest agriculture news, updates, and farming insights.",
                "https://agrinews.in/"
            )
        )
        // add more dummy items if you want
    }
}

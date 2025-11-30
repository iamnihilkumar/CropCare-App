package com.example.cropcare.presentation.history

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.cropcare.R
import com.example.cropcare.data.local.AppDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class HistoryActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: HistoryAdapter

    private val historyList = mutableListOf<HistoryModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_history)

        recyclerView = findViewById(R.id.recyclerHistory)
        recyclerView.layoutManager = LinearLayoutManager(this)

        adapter = HistoryAdapter(historyList) { selectedItem ->
            val intent = Intent(this, HistoryDetailActivity::class.java)
            intent.putExtra("ID", selectedItem.id)
            intent.putExtra("IMAGE_URI", selectedItem.imageUri)
            intent.putExtra("DISEASE", selectedItem.disease)
            intent.putExtra("CONFIDENCE", selectedItem.confidence)
            intent.putExtra("DATE", selectedItem.date)
            intent.putExtra("RESULT", selectedItem.fullResult)
            startActivity(intent)
        }

        recyclerView.adapter = adapter

        loadHistoryData()
    }

    override fun onResume() {
        super.onResume()
        loadHistoryData()   // refresh when coming back
    }

    private fun loadHistoryData() {
        lifecycleScope.launch(Dispatchers.IO) {

            val dao = AppDatabase.getDatabase(this@HistoryActivity).historyDao()
            val entities = dao.getAllHistory()

            val mappedList = entities.map {
                HistoryModel(
                    id = it.id,
                    imageUri = it.imageUri,
                    disease = it.disease,
                    confidence = it.confidence,
                    date = it.dateTime,
                    fullResult = it.resultText
                )
            }

            withContext(Dispatchers.Main) {
                val emptyText = findViewById<View>(R.id.tvEmptyHistory)

                if (mappedList.isEmpty()) {
                    emptyText.visibility = View.VISIBLE
                    recyclerView.visibility = View.GONE
                } else {
                    emptyText.visibility = View.GONE
                    recyclerView.visibility = View.VISIBLE
                }

                historyList.clear()
                historyList.addAll(mappedList)
                adapter.notifyDataSetChanged()
            }
        }
    }
}

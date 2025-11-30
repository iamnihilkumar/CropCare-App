package com.example.cropcare.presentation.history

import android.net.Uri
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.example.cropcare.R
import com.example.cropcare.data.local.AppDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class HistoryDetailActivity : AppCompatActivity() {

    private var historyId: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_history_detail)

        val img = findViewById<ImageView>(R.id.imgDetail)
        val tvDisease = findViewById<TextView>(R.id.tvDetailDisease)
        val tvConfidence = findViewById<TextView>(R.id.tvDetailConfidence)
        val tvResult = findViewById<TextView>(R.id.tvDetailResult)
        val tvDate = findViewById<TextView>(R.id.tvDetailDate)
        val btnDelete = findViewById<Button>(R.id.btnDelete)

        historyId = intent.getIntExtra("ID", -1)
        val imageUri = intent.getStringExtra("IMAGE_URI")
        val disease = intent.getStringExtra("DISEASE") ?: "Unknown"
        val confidence = intent.getStringExtra("CONFIDENCE") ?: "-"
        val date = intent.getStringExtra("DATE") ?: "-"
        val result = intent.getStringExtra("RESULT") ?: "No details available"

        // Safe image loading
        imageUri?.let {
            Glide.with(this)
                .load(Uri.parse(it))
                .placeholder(R.drawable.ic_image_placeholder)
                .error(R.drawable.ic_broken_image)
                .into(img)
        }

        tvDisease.text = "Disease: $disease"
        tvConfidence.text = "Confidence: $confidence"
        tvDate.text = date
        tvResult.text = result

        btnDelete.setOnClickListener {
            if (historyId != -1) deleteHistoryEntry(historyId)
        }
    }

    private fun deleteHistoryEntry(id: Int) {
        lifecycleScope.launch(Dispatchers.IO) {
            val dao = AppDatabase.getDatabase(this@HistoryDetailActivity).historyDao()
            dao.deleteById(id)

            withContext(Dispatchers.Main) {
                Toast.makeText(this@HistoryDetailActivity, "Deleted ✔", Toast.LENGTH_SHORT).show()
                finish()
            }
        }
    }
}

package com.example.cropcare.presentation.result

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.util.Base64
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.cropcare.R
import com.example.cropcare.data.local.AppDatabase
import com.example.cropcare.data.local.HistoryEntity
import com.example.cropcare.presentation.home.HomeActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import org.json.JSONObject
import java.io.ByteArrayOutputStream
import java.io.InputStream

class ResultActivity : AppCompatActivity() {

    private lateinit var ivPreview: ImageView
    private lateinit var tvDisease: TextView
    private lateinit var tvAccuracy: TextView
    private lateinit var tvDescription: TextView
    private lateinit var btnSaveHistory: Button
    private lateinit var btnBackHome: Button

    private var selectedImageUri: Uri? = null
    private var finalResultText = ""

    // Must start with "AIza"
    private val GEMINI_API_KEY = "AIzaSyBo2uaxHYmUVbQId0N02rTuvC3tGu4QQhg"
    private val TAG = "ResultActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_result)

        initializeViews()
        setupImagePreview()
        analyzeImageWithGemini()
    }

    private fun initializeViews() {
        ivPreview = findViewById(R.id.ivResultPreview)
        tvDisease = findViewById(R.id.tvDiseaseName)
        tvAccuracy = findViewById(R.id.tvAccuracy)
        tvDescription = findViewById(R.id.tvDescription)
        btnSaveHistory = findViewById(R.id.btnSaveHistory)
        btnBackHome = findViewById(R.id.btnBackHome)

        btnBackHome.setOnClickListener {
            startActivity(Intent(this, HomeActivity::class.java))
            finish()
        }

        btnSaveHistory.isEnabled = false
        btnSaveHistory.setOnClickListener {
            saveHistory()
        }
    }

    private fun setupImagePreview() {
        selectedImageUri = intent.getStringExtra("IMAGE_URI")?.let { Uri.parse(it) }
        selectedImageUri?.let { ivPreview.setImageURI(it) }
            ?: run { tvDescription.text = "Error: No image selected" }
    }

    private fun analyzeImageWithGemini() {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val bitmap = selectedImageUri?.let { uriToBitmap(it) }
                if (bitmap == null) {
                    withContext(Dispatchers.Main) {
                        tvDescription.text = "Error: Could not load image"
                    }
                    return@launch
                }

                val result = sendToGemini(bitmap)
                withContext(Dispatchers.Main) { updateUI(result) }

            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    tvDescription.text = "Error: ${e.localizedMessage}"
                }
            }
        }
    }

    private suspend fun sendToGemini(bitmap: Bitmap): String = withContext(Dispatchers.IO) {

        val base64Image = bitmapToBase64(bitmap)

        val prompt = """
            Analyze this plant leaf image. Identify any disease, severity, and treatment.
            Respond in EXACTLY this format, no any additional text.:

            Disease: [name or Healthy]
            Severity: [Low/Medium/High]
            Confidence: [number%]
            Description: [short explanation]
            Treatment: [steps]
        """.trimIndent()

        val json = """
        {  
          "contents": [
            {
              "parts": [
                { "text": "$prompt" },
                { 
                  "inline_data": {
                    "mime_type": "image/jpeg",
                    "data": "$base64Image"
                  }
                }
              ]
            }
          ]
        }
        """.trimIndent()

        val requestBody = RequestBody.create(
            "application/json".toMediaType(),
            json
        )

        val client = OkHttpClient()

        val request = Request.Builder()
            .url("https://generativelanguage.googleapis.com/v1beta/models/gemini-2.5-flash:generateContent")
            .addHeader("Content-Type", "application/json")
            .addHeader("x-goog-api-key", GEMINI_API_KEY)
            .post(requestBody)
            .build()

        val response = client.newCall(request).execute()
        val body = response.body?.string() ?: return@withContext "Error: Empty response"

        Log.e("GEMINI_RESPONSE", body)

        return@withContext try {
            val jsonObj = JSONObject(body)

            if (jsonObj.has("error")) {
                "Error: ${jsonObj.getJSONObject("error").getString("message")}"
            } else {
                jsonObj
                    .getJSONArray("candidates")
                    .getJSONObject(0)
                    .getJSONObject("content")
                    .getJSONArray("parts")
                    .getJSONObject(0)
                    .getString("text")
            }
        } catch (e: Exception) {
            "Failed to parse: ${e.message}"
        }
    }

    private fun bitmapToBase64(bitmap: Bitmap): String {
        val stream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 90, stream)
        val bytes = stream.toByteArray()
        return Base64.encodeToString(bytes, Base64.NO_WRAP)
    }

    private fun uriToBitmap(uri: Uri): Bitmap? {
        return try {
            val inputStream: InputStream? = contentResolver.openInputStream(uri)
            BitmapFactory.decodeStream(inputStream)
        } catch (e: Exception) {
            null
        }
    }

    private fun updateUI(result: String) {
        val lines = result.lines()

        tvDisease.text = lines.find { it.startsWith("Disease:") } ?: "Disease: Unknown"
        tvAccuracy.text = lines.find { it.startsWith("Confidence:") } ?: "Confidence: -"
        tvDescription.text = result

        finalResultText = result
        btnSaveHistory.isEnabled = true
    }
    private fun saveHistory() {
        val imageUriStr = selectedImageUri?.toString() ?: return

        val disease = tvDisease.text.toString().removePrefix("Disease: ").trim()
        val confidence = tvAccuracy.text.toString().removePrefix("Confidence: ").trim()
        val dt = System.currentTimeMillis().toString()

        val historyEntity = HistoryEntity(
            imageUri = imageUriStr,
            disease = disease,
            confidence = confidence,
            resultText = finalResultText,
            dateTime = dt
        )

        CoroutineScope(Dispatchers.IO).launch {
            val dao = AppDatabase.getDatabase(this@ResultActivity).historyDao()
            dao.insertHistory(historyEntity)

            withContext(Dispatchers.Main) {
                Toast.makeText(this@ResultActivity, "Saved to History ✔", Toast.LENGTH_SHORT).show()
                btnSaveHistory.isEnabled = false
            }
        }
    }
}

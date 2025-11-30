package com.example.cropcare.presentation.chatbot

import android.os.Bundle
import android.widget.EditText
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.cropcare.R
import kotlinx.coroutines.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject

class ChatbotActivity : AppCompatActivity() {

    private lateinit var recycler: RecyclerView
    private lateinit var etMessage: EditText
    private lateinit var btnSend: ImageView

    private val messages = mutableListOf<ChatMessage>()
    private lateinit var adapter: ChatAdapter

    private val ioScope = CoroutineScope(Dispatchers.IO)

    private val client = OkHttpClient()
    private val GEMINI_API_KEY = "AIzaSyBo2uaxHYmUVbQId0N02rTuvC3tGu4QQhg"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chatbot)

        recycler = findViewById(R.id.recyclerChat)
        etMessage = findViewById(R.id.etMessage)
        btnSend = findViewById(R.id.btnSend)

        adapter = ChatAdapter(messages)
        recycler.layoutManager = LinearLayoutManager(this)
        recycler.adapter = adapter

        addMessage(
            "🌿 Hi! I'm your CropCare Assistant.\n\n" +
                    "You can ask me anything about:\n" +
                    "• Plant diseases\n" +
                    "• Crop health\n" +
                    "• Farming tips\n" +
                    "• Weather & soil guidance\n" +
                    "• Or anything else you’d like to know!\n\n" +
                    "How can I help you today? 😊",
            false
        )

        btnSend.setOnClickListener {
            val text = etMessage.text.toString().trim()
            if (text.isNotEmpty()) onUserSendMessage(text)
        }
    }

    private fun onUserSendMessage(userText: String) {
        btnSend.isEnabled = false

        addMessage(userText, true)
        etMessage.setText("")

        askGeminiFlash(userText)
    }

    private fun addMessage(msg: String, isUser: Boolean) {
        messages.add(ChatMessage(msg, isUser))
        adapter.notifyItemInserted(messages.size - 1)
        recycler.scrollToPosition(messages.size - 1)
    }

    private fun askGeminiFlash(prompt: String) {

        addMessage("Thinking…", false)
        val replyIndex = messages.size - 1

        ioScope.launch {
            try {

                val safePrompt = prompt.replace("\"", "'")

                val jsonBody = """
                {
                  "contents": [
                    {
                      "parts": [
                        { "text": "$safePrompt" }
                      ]
                    }
                  ]
                }
                """.trimIndent()

                val requestBody =
                    jsonBody.toRequestBody("application/json".toMediaType())

                val request = Request.Builder()
                    .url("https://generativelanguage.googleapis.com/v1beta/models/gemini-2.5-flash:generateContent")
                    .addHeader("Content-Type", "application/json")
                    .addHeader("x-goog-api-key", GEMINI_API_KEY)
                    .post(requestBody)
                    .build()

                val response = client.newCall(request).execute()
                val responseText = response.body?.string()

                if (responseText == null) {
                    withContext(Dispatchers.Main) {
                        updateGeminiReplyMessage(replyIndex, "Error: Empty response")
                    }
                    return@launch
                }

                val json = JSONObject(responseText)

                val reply = try {
                    json.getJSONArray("candidates")
                        .getJSONObject(0)
                        .getJSONObject("content")
                        .getJSONArray("parts")
                        .getJSONObject(0)
                        .getString("text")
                } catch (e: Exception) {
                    "Error parsing reply: ${e.message}"
                }

                withContext(Dispatchers.Main) {
                    updateGeminiReplyMessage(replyIndex, reply)
                }

            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    updateGeminiReplyMessage(replyIndex, "Error: ${e.localizedMessage}")
                }
            }
        }
    }

    private fun updateGeminiReplyMessage(index: Int, newText: String) {
        messages[index] = ChatMessage(newText, false)
        adapter.notifyItemChanged(index)
        btnSend.isEnabled = true
    }
}

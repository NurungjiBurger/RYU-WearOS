package com.example.mullyu.presentation.networking

import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.WebSocket
import okhttp3.WebSocketListener
import okio.ByteString
import android.util.Log
import okio.ByteString.Companion.decodeHex

// HTTP
class MullyuHTTP {
    private val client: OkHttpClient = OkHttpClient()
    private lateinit var webSocket: WebSocket

    fun init() {
        // 초기화 작업 수행
    }

    // 연결
    fun connect() {
        val request = Request.Builder().url("wss://echo.websocket.org").build()
        val listener = EchoWebSocketListener()
        webSocket = client.newWebSocket(request, listener)
    }

    // 연결 해제
    fun disconnect() {
        if (::webSocket.isInitialized) {
            webSocket.close(1000, "Client closed connection")
        }
    }

    // 동작테스트
    private inner class EchoWebSocketListener : WebSocketListener() {
        override fun onOpen(webSocket: WebSocket, response: okhttp3.Response) {
            Log.d("WebSocket", "Connected to the server")
            webSocket.send("Hello, WebSocket!")
            webSocket.send("deadbeef".decodeHex())
        }

        override fun onMessage(webSocket: WebSocket, text: String) {
            Log.d("WebSocket", "Receiving: $text")
        }

        override fun onMessage(webSocket: WebSocket, bytes: ByteString) {
            Log.d("WebSocket", "Receiving bytes: ${bytes.hex()}")
        }

        override fun onClosing(webSocket: WebSocket, code: Int, reason: String) {
            Log.d("WebSocket", "Closing: $code / $reason")
            webSocket.close(1000, null)
        }

        override fun onFailure(webSocket: WebSocket, t: Throwable, response: okhttp3.Response?) {
            Log.d("WebSocket", "Error: ${t.message}")
        }
    }
}

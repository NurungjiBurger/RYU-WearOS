package com.example.mullyu.presentation

import MullyuViewModel
import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import com.example.mullyu.R
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class MullyuDataList(private val viewModel: MullyuViewModel, private val context: Context) {
    // mqtt or http 로 통신 후
    // private val datalist 동적 생성
    private val topic = "KFC"

    private val mqttClient = MullyuMQTT(context) { message ->
        handleMessage(message)
    }

    fun connect() {
        mqttClient.connectToMQTTBroker()
    }

    fun disconnect() {
        mqttClient.disconnect()
    }

    private fun handleMessage(message: String) {
        mqttClient.unsubscribe(topic)

        val newItems = parseMessageToMullyuList(message)
        viewModel.updateDataList(newItems)
    }

    private fun parseMessageToMullyuList(message: String): List<Mullyu> {
        return message.split(",").map { item ->
            val parts = item.split("/")
            Mullyu(
                imageName = getDrawableResIdByName(parts[0]),
                name = parts[0],
                quantity = parts[1],
                process = false
            )
        }
    }

    private fun getDrawableResIdByName(name: String): Int {
        return context.resources.getIdentifier(name.lowercase(), "drawable", context.packageName)
    }

}
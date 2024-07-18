package com.example.mullyu.presentation

import MullyuViewModel
import android.content.Context
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class MullyuDataList(private val viewModel: MullyuViewModel, private val context: Context) {
    // MQTT 또는 HTTP로 통신 후 데이터 목록 동적 생성
    private val Topic = "KFC"
    private val _ProcessCnt = MutableStateFlow(0)

    val ProcessCnt: StateFlow<Int> = _ProcessCnt.asStateFlow()

    private val mqttClient = MullyuMQTT(context) { message ->
        handleMessage(message)
    }

    fun connect() {
        mqttClient.connectToMQTTBroker()
        mqttClient.subscribe(Topic)
    }

    fun disconnect() {
        mqttClient.disconnect()
    }

    fun DataProcessCheck() {
        if (viewModel.DataProcessCheck()) {
            ReSubscribeTopic()
        }
    }

    private fun ReSubscribeTopic() {
        mqttClient.unsubscribe(Topic)
        mqttClient.subscribe(Topic)
    }

    private fun handleMessage(message: String) {
        // 데이터 처리 중에 구독 해제
        mqttClient.unsubscribe(Topic)

        val newItems = parseMessageToMullyuList(message)
        viewModel.updateDataList(newItems)

        // 데이터 처리 완료 후 구독 다시 설정
        mqttClient.subscribe(Topic)
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

package com.example.mullyu.presentation.data

import android.content.Context
import androidx.lifecycle.viewModelScope
import com.example.mullyu.presentation.networking.MullyuMQTT
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

// MQTT로 통신 후 데이터 목록 동적 생성
class MullyuDataList(private val viewModel: MullyuViewModel, private val context: Context) {
    // 주제
    private val mqttTopic = "KFC"
    // 처리된 데이터의 수
    private val _processedCnt = MutableStateFlow(0)
    val processedCnt: StateFlow<Int> = _processedCnt.asStateFlow()

    // 콜백함수로 handleMessage 등록하면서 MQTT 초기화
    private val mqttClient = MullyuMQTT(context) { message ->
        handleMessage(message)
    }

    // MQTT에 mqttTopic으로 연결 및 구독
    fun connect() {
        mqttClient.connectToMQTTBroker()
        mqttClient.subscribe(mqttTopic)
        initalizeDataList()
    }

    // MQTT 연결 해제
    fun disConnect() {
        mqttClient.disconnect()
    }

    // 앱이 처음 실행될 때 내부 DB를 보고 동적으로 리스트 생성
    fun initalizeDataList() {
        viewModel.viewModelScope.launch {
            try {
                // 데이터베이스에서 모든 데이터 가져오기
                val allData = viewModel.getAllDataFromDatabase()

                // 모든 데이터가 처리 완료되었는지 확인
                if (allData.all { it.isProcess }) {
                    // 모든 데이터가 처리 완료된 경우 다음 메시지를 기다려야함
                    reSubscribeTopic()  // 구독 재설정
                } else {
                    // 모든 데이터가 처리되지 않은 경우
                    // 모든 데이터로 다시 리스트를 생성해서 전달
                    val newItems = allData

                    // newItems를 viewModel에 전달하여 업데이트
                    mqttClient.unSubscribe(mqttTopic)
                    viewModel.updateDataList(newItems)
                }
            } catch (e: Exception) {
            }
        }
    }

    // 모든 데이터가 처리됐는지 체크
    fun dataProcessCheck() {
        if (viewModel.dataProcessCheck()) {
            reSubscribeTopic()
            viewModel.updateDataList(emptyList())
        }
    }

    // 구독 재개
    private fun reSubscribeTopic() {
        mqttClient.unSubscribe(mqttTopic)
        mqttClient.subscribe(mqttTopic)
    }

    // 메시지가 들어왔을때 실행될 함수
    private fun handleMessage(message: String) {
        // 데이터 처리 중에 구독 해제
        mqttClient.unSubscribe(mqttTopic)

        // 새 아이템을 만들어주고
        val newItems = parseMessageToMullyuList(message)
        viewModel.updateDataList(newItems)
    }

    // 들어온 메시지로부터 Mullyu 데이터에 맞춰서 데이터 객체 생성
    // 물류 데이터 JSON 형태로 바꿔야 함
    private fun parseMessageToMullyuList(message: String): List<MullyuLogistics> {
        return message.split(",").map { item ->
            val parts = item.split("/")
            MullyuLogistics(
                imageName = getDrawableResIdByName(parts[0]),
                name = parts[0],
                quantity = parts[1],
                isProcess = false
            )
        }
    }

    // 객체에 사용될 이름을 통해 객체의 이미지를 찾아서 넘겨줌
    private fun getDrawableResIdByName(name: String): Int {
        return context.resources.getIdentifier(name.lowercase(), "drawable", context.packageName)
    }
}

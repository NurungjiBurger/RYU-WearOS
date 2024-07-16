package com.example.mullyu.presentation

import org.eclipse.paho.client.mqttv3.MqttCallback
import org.eclipse.paho.client.mqttv3.MqttClient
import org.eclipse.paho.client.mqttv3.MqttConnectOptions
import org.eclipse.paho.client.mqttv3.MqttException
import org.eclipse.paho.client.mqttv3.MqttMessage
import java.util.UUID

class MullyuMQTT(){//callback: MqttCallback?) {
    private val mqttClient: MqttClient
    private val brokerUrl = "tcp://test.mosquitto.org:1883"//"tcp://iot.eclipse.org:1883"//"tcp://test.mosquitto.org:1883"
    private val clientId = UUID.randomUUID().toString() // UUID 사용

    init {
        mqttClient = MqttClient(brokerUrl, clientId, null) // null을 전달
        //mqttClient.setCallback(callback)
    }

    fun connect() {
        try {
            val options = MqttConnectOptions()
            options.isCleanSession = true
            mqttClient.connect(options)
            println("MQTT 연결 시도")
            if (mqttClient.isConnected) {
                println("MQTT 연결 성공")
            }
        } catch (e: MqttException) {
            println("MQTT 연결 실패: ${e.message}")
            e.printStackTrace()
        }
    }

    fun publish(topic: String?, message: String) {
        try {
            val mqttMessage = MqttMessage(message.toByteArray())
            mqttMessage.qos = 1 // 또는 0으로 변경
            mqttClient.publish(topic, mqttMessage)
            println("MQTT 메시지 발행: $message")
        } catch (e: MqttException) {
            println("MQTT 메시지 발행 실패")
            e.printStackTrace()
        }
    }

    fun subscribe(topic: String?) {
        try {
            mqttClient.subscribe(topic ?: "test/topic") // 기본 주제 설정
            println("MQTT 주제 구독: ${topic ?: "test/topic"}")
        } catch (e: MqttException) {
            println("MQTT 주제 구독 실패: ${e.message}")
            e.printStackTrace()
        }
    }

    fun disconnect() {
        try {
            mqttClient.disconnect()
            println("MQTT 연결 종료")
        } catch (e: MqttException) {
            println("MQTT 연결 종료 실패")
            e.printStackTrace()
        }
    }
}

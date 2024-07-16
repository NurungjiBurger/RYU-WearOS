package com.example.mullyu.presentation

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken
import org.eclipse.paho.client.mqttv3.MqttCallback
import org.eclipse.paho.client.mqttv3.MqttClient
import org.eclipse.paho.client.mqttv3.MqttConnectOptions
import org.eclipse.paho.client.mqttv3.MqttException
import org.eclipse.paho.client.mqttv3.MqttMessage
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence
import java.util.UUID

class MullyuMQTT() {
    private lateinit var mqttClient: MqttClient
    private val MQTT_BROKER = "tcp://70.12.246.77:1883"//"tcp://192.168.170.193:1883"
    private val MQTT_TOPIC = "KFC"

    init {
        mqttClient = MqttClient(MQTT_BROKER, MqttClient.generateClientId(), MemoryPersistence())
    }

    fun connectToMQTTBroker() {
        try {
            mqttClient.connect()
            mqttClient.setCallback(object : MqttCallback {
                override fun connectionLost(cause: Throwable?) {
                    println("연결이 끊어졌습니다.")
                }

                override fun messageArrived(topic: String?, message: MqttMessage?) {
                    // 메시지가 도착했을 때의 행동을 정의할 수 있습니다.
                    message?.let {
                        println("Received message: ${String(it.payload)}")
                    }
                }

                override fun deliveryComplete(token: IMqttDeliveryToken?) {
                    // 메시지 배달이 완료되었을 때의 행동을 정의할 수 있습니다.
                    println("MQTT message to deleiver")
                }
            })
            subscribe(MQTT_TOPIC)
            println("Connected to MQTT Broker")
        } catch (e: MqttException) {
            println("Failed to connect to MQTT Broker: ${e.message}")
        }
    }

    fun sendMQTTMessage(msg: String) {
        try {
            val message = MqttMessage()
            message.payload = msg.toByteArray()
            mqttClient.publish(MQTT_TOPIC, message)
            println("MQTT Message sent: $message")
        } catch (e: MqttException) {
            println("MQTT Failed to send message: ${e.message}")
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

package com.example.mullyu.presentation

import android.annotation.SuppressLint
import android.content.ContentValues.TAG
import android.content.Context
import android.util.Log
import io.github.cdimascio.dotenv.Dotenv
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
import io.github.cdimascio.dotenv.dotenv
import java.io.File


//                          callback 함수 설정
class MullyuMQTT(private val context: Context, private val messageListener: (String) -> Unit) {
    private lateinit var mqttClient: MqttClient
    private val MQTT_TOPIC = "KFC"

    // dotenv 파일 불러오기
    init {
        try {
            val dotenv = dotenv {
                // 앱 내부
                // 여기서는 /data/user/0/com.example.mullyu/files/.env
                directory = context.filesDir.absolutePath
                filename = ".env"
            }
            Log.d("Dotenv", "Loaded MQTT_BROKER_IP: ${dotenv["MQTT_BROKER_IP"]}")
            // 찾았으면 할당
            val MQTT_BROKER_ID = dotenv["MQTT_BROKER_IP"]
            
            // 해당 IP로 연결
            //mqttClient = MqttClient(MQTT_BROKER_ID, MqttClient.generateClientId(), MemoryPersistence())
            mqttClient = MqttClient("tcp://70.12.246.77:1883", MqttClient.generateClientId(), MemoryPersistence())

        } catch (e: Exception) {
            Log.e("Dotenv", "Error loading .env file: ${e.message}")
        }
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
                        messageListener(String(it.payload))
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

    fun unsubscribe(topic: String?) {
        try{
            mqttClient.unsubscribe(topic ?: "test/topic")
            println("MQTT 구독 중지 : ${topic ?: "test/topic"}")
        } catch (e: MqttException) {
            println("MQTT 구독 중지 실패 : ${e.message}")
            e.printStackTrace()
        }
    }
}

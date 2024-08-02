package com.example.mullyu.presentation.networking

import android.content.Context
import android.util.Log
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken
import org.eclipse.paho.client.mqttv3.MqttCallback
import org.eclipse.paho.client.mqttv3.MqttClient
import org.eclipse.paho.client.mqttv3.MqttException
import org.eclipse.paho.client.mqttv3.MqttMessage
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence
import io.github.cdimascio.dotenv.dotenv


// MQTT
class MullyuMQTT(private val sectorName: String, private val context: Context, private val messageListener: (String) -> Unit) {
    // MQTT를 위한 클라이언트 객체 생성
    private lateinit var mqttClient: MqttClient
    // 사용할 주제 ( 넘겨받은 것으로 해도됨 )
    private val mqttTopic = sectorName

    // dotenv 파일에서 브로커의 주소를 찾아서 초기화 진행
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
            val mqttBrokerIp = dotenv["MQTT_BROKER_IP"]
            
            // 해당 IP로 연결
            mqttClient = MqttClient(mqttBrokerIp, MqttClient.generateClientId(), MemoryPersistence())
            //mqttClient = MqttClient("tcp://70.12.246.77:1883", MqttClient.generateClientId(), MemoryPersistence())

        } catch (e: Exception) {
            Log.e("Dotenv", "Error loading .env file: ${e.message}")
        }
    }

    // MQTT 브로커에 연결하기
    fun connectToMQTTBroker() {
        try {
            // 이름 설정 전에 연결되는거 방지
            if (mqttTopic == "sector") return
            println("this topic : ${mqttTopic}")
            // 연결 설정A
            mqttClient.connect()
            // 콜백 함수 설정
            mqttClient.setCallback(object : MqttCallback {
                // 연결이 끊어졌을때 무엇을 해야할까 ?
                override fun connectionLost(cause: Throwable?) {
                    println("연결이 끊어졌습니다.")
                }

                // 메시지가 왔을때 무엇을 해야할까 ?
                override fun messageArrived(topic: String?, message: MqttMessage?) {
                    message?.let {
                        println("Received message: ${String(it.payload)}")
                        messageListener(String(it.payload))
                    }
                }

                // 메시지가 전송 됐을때 무엇을 해야할까 ?
                override fun deliveryComplete(token: IMqttDeliveryToken?) {
                    println("MQTT message to deleiver")
                }
            })
            // 구독 설정
            subscribe(mqttTopic)
            println("Connected to MQTT Broker")
        } catch (e: MqttException) {
            println("Failed to connect to MQTT Broker: ${e.message}")
        }
    }

    // MQTT 메시지 보내기
    fun sendMQTTMessage(topic: String, msg: String) {
        try {
            // 전달받은 문자열을 MqttMessage로 변환
            val message = MqttMessage()
            message.payload = msg.toByteArray()
            // 특정 주제에게 메시지 publish
            mqttClient.publish(topic, message)
            println("MQTT Message sent: $message")
        } catch (e: MqttException) {
            println("MQTT Failed to send message: ${e.message}")
        }
    }

    // 구독
    fun subscribe(topic: String?) {
        try {
            // 매개변수로 들어온 주제에 대해서 구독
            mqttClient.subscribe(topic ?: "test/topic") // 기본 주제 설정
            println("MQTT 주제 구독: ${topic ?: "test/topic"}")
        } catch (e: MqttException) {
            println("MQTT 주제 구독 실패: ${e.message}")
            e.printStackTrace()
        }
    }

    // 연결 해제
    fun disconnect() {
        try {
            mqttClient.disconnect()
            println("MQTT 연결 종료")
        } catch (e: MqttException) {
            println("MQTT 연결 종료 실패")
            e.printStackTrace()
        }
    }

    // 구독 해제
    fun unSubscribe(topic: String?) {
        try{
            mqttClient.unsubscribe(topic ?: "test/topic")
            println("MQTT 구독 중지 : ${topic ?: "test/topic"}")
        } catch (e: MqttException) {
            println("MQTT 구독 중지 실패 : ${e.message}")
            e.printStackTrace()
        }
    }
}

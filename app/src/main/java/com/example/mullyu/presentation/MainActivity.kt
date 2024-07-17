/* While this template provides a good starting point for using Wear Compose, you can always
 * take a look at https://github.com/android/wear-os-samples/tree/main/ComposeStarter and
 * https://github.com/android/wear-os-samples/tree/main/ComposeAdvanced to find the most up to date
 * changes to the libraries and their usages.
 */

package com.example.mullyu.presentation

import MullyuViewModel
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.Image
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.wear.compose.material.Button
import androidx.wear.compose.material.ButtonDefaults
import androidx.wear.compose.material.CircularProgressIndicator
import androidx.wear.compose.material.MaterialTheme
import androidx.wear.compose.material.Scaffold
import androidx.wear.compose.material.Text
import androidx.wear.compose.material.TimeText
import androidx.wear.compose.material.TimeTextDefaults
import androidx.wear.compose.material.Vignette
import androidx.wear.compose.material.VignettePosition
import androidx.wear.compose.material.placeholder
import com.example.mullyu.R
import com.example.mullyu.presentation.theme.MullyuTheme
import com.example.mullyu.presentation.MullyuMQTT
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken
import org.eclipse.paho.client.mqttv3.MqttCallback
import org.eclipse.paho.client.mqttv3.MqttMessage

class MainActivity : ComponentActivity() {
    //private lateinit var mullyuHTTP: MullyuHTTP
    private val viewModel: MullyuViewModel by viewModels()
    private lateinit var mullyuDataList: MullyuDataList
    //private lateinit var mullyuMQTT: MullyuMQTT

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        installSplashScreen()
        setTheme(android.R.style.Theme_DeviceDefault)

    //    mullyuHTTP = MullyuHTTP()
    //    mullyuHTTP.init()
    //    mullyuHTTP.connect()

//        val mqttClient = MullyuMQTT { message ->
//            test()
//        }

        //mullyuHTTP.connect()


        //mullyuMQTT.connectToMQTTBroker()

        mullyuDataList = MullyuDataList(viewModel, applicationContext)
        mullyuDataList.connect()

        setContent {
            val mullyuData by viewModel.mullyuData.collectAsStateWithLifecycle()
            val dataList by viewModel.dataList.collectAsStateWithLifecycle()

            Scaffold(
                timeText = {
                    TimeText(
                        timeTextStyle = TimeTextDefaults.timeTextStyle(
                            fontSize = 10.sp,
                            color = Color.White
                        )
                    )
                },
                vignette = {
                    Vignette(vignettePosition = VignettePosition.TopAndBottom)
                }
            ) {
                if (mullyuData == null) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text(
                            text = "No Data",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.width(100.dp) // 고정된 텍스트 너비
                        )
                }
            } else {
                Mullyu(
                    data = mullyuData!!,
                    onConfirmClick = {
                        viewModel.nextImage()
                        // Optional: Send a message through WebSocket here if needed
                    },
                    sendMQTTMessage = { message ->
                        sendMQTTMessage(message)
                    },
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black)
                )
                }
            }
        }
    }

    private fun test() {
        println("MQTT READY")
    }

    private fun sendMQTTMessage(message: String) {
        //mullyuMQTT.sendMQTTMessage(message)
    }

    override fun onDestroy() {
        super.onDestroy()
        //mullyuHTTP.disconnect()
        //mullyuMQTT.disconnect()
    }


}

@Composable
private fun Mullyu(
    data: Mullyu,
    onConfirmClick: () -> Unit,
    sendMQTTMessage: (message: String) -> Unit,
    modifier: Modifier = Modifier
) {

    Column(
        modifier = modifier
            .padding(16.dp)
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Spacer(modifier = Modifier.width(16.dp)) // 왼쪽에 간격 추가

            Image(
                painter = painterResource(id = data.imageName),
                contentDescription = null,
                modifier = Modifier.size(64.dp) // 고정된 이미지 크기
            )

            Spacer(modifier = Modifier.width(16.dp))

            Box(
                modifier = Modifier
                    .width(1.dp)
                    .height(70.dp)
                    .background(Color.Gray)
            )

            Spacer(modifier = Modifier.width(16.dp))

            Column(
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = data.name,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    textAlign = TextAlign.Start,
                    modifier = Modifier.width(100.dp) // 고정된 텍스트 너비
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "수량 : ${data.quantity}",
                    fontSize = 16.sp,
                    color = Color.White,
                    textAlign = TextAlign.Start,
                    modifier = Modifier.width(100.dp) // 고정된 텍스트 너비
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Box(
            modifier = Modifier
                .width(180.dp)
                .height(1.dp)
                .background(Color.Gray)
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                onConfirmClick()
                sendMQTTMessage(data.name)
            },
            colors = ButtonDefaults.buttonColors(
                backgroundColor = Color.Magenta, // 버튼 배경색
            ),
            modifier = Modifier
                .fillMaxWidth() // 고정된 버튼 너비
                .height(48.dp) // 고정된 버튼 높이
        ) {
            Text(text = "Confirm", color = Color.White)
        }
    }
}

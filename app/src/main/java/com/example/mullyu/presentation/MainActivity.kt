/* While this template provides a good starting point for using Wear Compose, you can always
 * take a look at https://github.com/android/wear-os-samples/tree/main/ComposeStarter and
 * https://github.com/android/wear-os-samples/tree/main/ComposeAdvanced to find the most up to date
 * changes to the libraries and their usages.
 */

package com.example.mullyu.presentation

import com.example.mullyu.presentation.data.MullyuViewModel
import android.os.Bundle
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
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.wear.compose.material.Button
import androidx.wear.compose.material.ButtonDefaults
import androidx.wear.compose.material.Scaffold
import androidx.wear.compose.material.Text
import androidx.wear.compose.material.TimeText
import androidx.wear.compose.material.TimeTextDefaults
import androidx.wear.compose.material.Vignette
import androidx.wear.compose.material.VignettePosition
import com.example.mullyu.R
import com.example.mullyu.presentation.data.MullyuDataList
import com.example.mullyu.presentation.data.MullyuLogistics

class MainActivity : ComponentActivity() {
    // HTTP, MQTT 사용을 위한 선언
    // private lateinit var mullyuHTTP: MullyuHTTP
    // private lateinit var mullyuMQTT: MullyuMQTT

    // 내부 처리 로직 관리 모델 선언
    private val viewModel: MullyuViewModel by viewModels()
    // 동적 리스트를 생성하고 관리할 객체 선언
    private lateinit var mullyuDataList: MullyuDataList

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        installSplashScreen()
        setTheme(android.R.style.Theme_DeviceDefault)

        // HTTP, MQTT 사용 초기화 및 연결
        // mullyuHTTP = MullyuHTTP()
        // mullyuHTTP.init()
        // mullyuHTTP.connect()

        // mullyuMQTT.connectToMQTTBroker()

        // 동적 데이터 리스트를 관리하고 생성할 객체 초기화
        mullyuDataList = MullyuDataList(viewModel, applicationContext)
        // 동적으로 생성하기 위해 MQTT 연결
        mullyuDataList.connect()

        setContent {
            // 현재 화면상에 보여주어야하는 데이터
            val mullyuData by viewModel.mullyuData.collectAsStateWithLifecycle()
            val nowProcessed by viewModel.processCount.collectAsStateWithLifecycle()
            val maxProcessed by viewModel.dataList.collectAsStateWithLifecycle()

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
                // 메시지를 수신하지 못해서 보여줄 물류 데이터가 없다면 대기화면 출력
                if (mullyuData == null) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text(
                            text = "No Data",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.width(100.dp)
                        )
                    }
            }
                // 메시지 수신 시 데이터를 화면에 UI와 함께 보여줄 것
                else {
                Mullyu(
                    data = mullyuData!!,
                    nowSize = nowProcessed,
                    maxSize = maxProcessed.size,
                    onConfirmClick = {
                        viewModel.ConfirmMullyuData()
                        mullyuDataList.dataProcessCheck()
                    },
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black)
                )
                }
            }
        }
    }
}

@Composable
private fun Mullyu(
    data: MullyuLogistics,
    nowSize: Int,
    maxSize: Int,
    onConfirmClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    // UI 세로 배치
    Column(
        modifier = modifier
            .padding(16.dp)
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // UI 윗부분 이미지와 글 가로 배치
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            // 간격
            Spacer(modifier = Modifier.width(16.dp))

            // 이미지 삽입 구간
            Box {
                Image(
                    painter = painterResource(id = data.imageName),
                    contentDescription = null,
                    modifier = Modifier
                        .size(64.dp)
                        .let { if (data.isProcess) it.graphicsLayer { alpha = 0.3f } else it }
                )
                // 이미 처리된 이미지라면 체크표시 이미지를 덮어 씌움
                if (data.isProcess) {
                    Image(
                        painter = painterResource(id = R.drawable.check),
                        contentDescription = null,
                        modifier = Modifier
                            .size(32.dp)
                            .align(Alignment.Center)
                    )
                }
            }

            Spacer(modifier = Modifier.width(16.dp))
            // 구분선
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
                    // 텍스트가 들어갈 너비를 고정하여 글자수가 달라지더라도 UI가 움직이지 않게 함
                    modifier = Modifier.width(100.dp)
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "수량 : ${data.quantity}",
                    fontSize = 16.sp,
                    color = Color.White,
                    textAlign = TextAlign.Start,
                    modifier = Modifier.width(100.dp)
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
            },
            colors = ButtonDefaults.buttonColors(
                // 차후 로봇의 구분을 위해 버튼에 색깔을 넣을 수 있음
                backgroundColor = Color.Cyan,
            ),
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
        ) {
            if (data.isProcess) Text(text = "Next ${nowSize} / ${maxSize}", color = Color.White)
            else Text(text = "Confirm ${nowSize} / ${maxSize}", color = Color.White)
        }
    }
}
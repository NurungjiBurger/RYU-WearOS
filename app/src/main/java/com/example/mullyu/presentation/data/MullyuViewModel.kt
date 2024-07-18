package com.example.mullyu.presentation.data

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.room.Room
import androidx.room.RoomDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

// 내부 로직 체계
class MullyuViewModel(application: Application) : AndroidViewModel(application) {

    private var database: MullyuRoomDataBase? = null

    // 초기 데이터 삽입 예시
    private val initialData = listOf(
        MullyuLogistics(name = "Package A", quantity = 10, status = "Processed"),
        MullyuLogistics(name = "Package B", quantity = 20, status = "Pending")
    )

    // 변경 가능한 val 변수 ?
    private val _imageIndex = MutableStateFlow(0)

    // 외부에서 참조하기 위한 변수들 변경 불가
    val imageIndex: StateFlow<Int> = _imageIndex.asStateFlow()

    private val _dataList = MutableStateFlow<List<Mullyu>>(emptyList())
    val dataList: StateFlow<List<Mullyu>> = _dataList.asStateFlow()

    private val _mullyuData = MutableStateFlow<Mullyu?>(null)
    val mullyuData: StateFlow<Mullyu?> = _mullyuData.asStateFlow()

    private val _ProcessCount = MutableStateFlow(0)
    val ProcessCount: StateFlow<Int> = _ProcessCount.asStateFlow()

    init {
        println("DB 출력")
        printAllData()
    }

    // Confirm 버튼을 누르면 해당 물류에 대한 처리가 완료되었음을 의미
    fun ConfirmMullyuData() {
        // 처리 완료 표시
        if (!_mullyuData.value!!.isProcess) {
            _mullyuData.value?.let { it.isProcess = true }
            // 처리 완료 물량 + 1
            _ProcessCount.value += 1
        }
        // 다음 물류 표시
        displayNextMullyuData()
    }

    // 모든 물품에 대한 처리 검사
    fun dataProcessCheck(): Boolean {
        if (_ProcessCount.value == _dataList.value.size) return true
        return false
    }

    // 다음 데이터를 보여줌
    fun displayNextMullyuData() {
        _imageIndex.value = (_imageIndex.value + 1) % (_dataList.value.size)
        _mullyuData.value = _dataList.value[_imageIndex.value]
    }

    // 데이터리스트 업데이트
    fun updateDataList(newDataList: List<Mullyu>) {
        _dataList.value = newDataList
        _ProcessCount.value = 0
        // 화면에 보여질 mullyuData는 새로 들어온 데이터리스트의 첫번째 물류
        if (newDataList.isNotEmpty()) {
            _mullyuData.value = newDataList[0]
        }
    }

    // 데이터베이스에서 데이터리스트를 가져옴
    private fun getDatabase(): MullyuRoomDataBase {
        println("DB 생성 시작 ~~ ")
        return database ?: synchronized(this) {
            val instance = Room.databaseBuilder(
                getApplication(),
                MullyuRoomDataBase::class.java,
                "testdb"
            ).apply {
                // 데이터베이스가 처음 생성될 때 초기 데이터를 삽입합니다.
                if (database?.mullyuLogisticsDao()?.getAll().isNullOrEmpty()) {
                    database?.mullyuLogisticsDao()?.insertAll(initialData)
                }
            }.build()
            database = instance
            instance
        }
    }

    // 데이터베이스에서 모든 데이터를 가져와서 출력
    fun printAllData() {
        viewModelScope.launch(Dispatchers.IO) {
            val db = getDatabase()
            val dataList = db.mullyuLogisticsDao().getAll()
            println("전체 데이터:")
            dataList.forEach {
                println("물류이름: ${it.name}, 물류량: ${it.quantity}, 처리상태: ${it.status}")
            }
        }
    }

    // 데이터리스트 백업
    fun dataListBackUp() {
        println("DB 백업 ~ ")
        database!!.mullyuLogisticsDao().deleteAll()
        database!!.mullyuLogisticsDao().insertAll(initialData)
    }
}
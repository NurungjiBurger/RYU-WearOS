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
import kotlinx.coroutines.withContext

// 내부 로직 체계
class MullyuViewModel(application: Application) : AndroidViewModel(application) {

    // 내부 Database
    private var database: MullyuRoomDataBase = Room.databaseBuilder(
        application,
        MullyuRoomDataBase::class.java, "mullyuDatabase"
    ).build()

    // 변경 가능한 val 변수
    private val _imageIndex = MutableStateFlow(0)
    // 외부에서 참조하기 위한 변수들 변경 불가
    val imageIndex: StateFlow<Int> = _imageIndex.asStateFlow()

    private val _dataList = MutableStateFlow<List<MullyuLogistics>>(emptyList())
    val dataList: StateFlow<List<MullyuLogistics>> = _dataList.asStateFlow()

    private val _mullyuData = MutableStateFlow<MullyuLogistics?>(null)
    val mullyuData: StateFlow<MullyuLogistics?> = _mullyuData.asStateFlow()

    private val _processCount = MutableStateFlow(0)
    val processCount: StateFlow<Int> = _processCount.asStateFlow()


    private val _sectorName = MutableStateFlow<String?>("")
    val sectorName: StateFlow<String?> = _sectorName.asStateFlow()
    

    // Confirm 버튼을 누르면 해당 물류에 대한 처리가 완료되었음을 의미
    fun ConfirmMullyuData() {
        val currentData = _mullyuData.value ?: return
        // 처리 완료 표시 및 DB 업데이트
        if (!currentData.isProcess) {
            viewModelScope.launch(Dispatchers.IO) {
                currentData.isProcess = true
                _processCount.value += 1
                database.mullyuLogisticsDao().updateIsProcess(currentData.name)
            }
        }
        //printAllData()
        // 다음 물류
        displayNextMullyuData()
    }

    // 모든 물품에 대한 처리 검사
    fun dataProcessCheck(): Boolean {
        return _processCount.value == _dataList.value.size
    }

    // 다음 데이터를 보여줌
    fun displayNextMullyuData() {
        _imageIndex.value = (_imageIndex.value + 1) % (_dataList.value.size)
        _mullyuData.value = _dataList.value[_imageIndex.value]
    }

    // 데이터리스트 업데이트
    fun updateDataList(newDataList: List<MullyuLogistics>) {
        viewModelScope.launch(Dispatchers.IO) {
            // 기존 DB 삭제
            database.mullyuLogisticsDao().delete()
            val lastInsertedId = database.mullyuLogisticsDao().getLastInsertedId() ?: 0
            // ID가 너무 커지는 것을 방지하기 위해 ID가 너무 커지지 않도록 한번씩 리셋
            if (lastInsertedId >= 1000000) {
                database.mullyuLogisticsDao().deleteAll()
                database.clearAllTables()
            }
            // 데이터를 다 썼을 때는 다시 No Data 표시
            if (newDataList.size == 0) {
                _dataList.value = emptyList()
                _mullyuData.value = null
            }
            else {
                // 새로운 DB 삽입
                database.mullyuLogisticsDao().insertAll(newDataList)
                _dataList.value = newDataList
                _mullyuData.value = newDataList.getOrNull(0)
            }
            // 처리 완료된 데이터의 수 표시
            _processCount.value = _dataList.value.count { it.isProcess }
        }
    }

    // 데이터베이스에서 모든 데이터를 가져오는 suspend 함수
    suspend fun getAllDataFromDatabase(): List<MullyuLogistics> {
        return withContext(Dispatchers.IO) {
            try {
                database.mullyuLogisticsDao().getAll()
            } catch (e: Exception) {
                println("데이터베이스 읽기 오류: ${e.message}")
                emptyList()
            }
        }
    }

    // Database 상태로 받아오기
    private fun getDatabase(): MullyuRoomDataBase {
        return database ?: synchronized(this) {
            val instance = Room.databaseBuilder(
                getApplication(),
                MullyuRoomDataBase::class.java,
                "mullyu-database"
            ).build()
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
                println("번호: ${it.id}, 물류이름: ${it.name}, 물류량: ${it.quantity}, 처리상태: ${it.isProcess}")
            }
        }
    }

    fun setSectorName(name: String) {
        viewModelScope.launch {
            _sectorName.value = name
        }
    }

    // 단일 robotId 가져오는 메서드 추가
    suspend fun getRobotIdFromDatabase(): String {
        return database.mullyuLogisticsDao().getRobotId()
    }
}
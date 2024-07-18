package com.example.mullyu.presentation.data

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

// 내부 로직 체계
class MullyuViewModel : ViewModel() {

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
        _ProcessCount.value = newDataList.size
        // 화면에 보여질 mullyuData는 새로 들어온 데이터리스트의 첫번째 물류
        if (newDataList.isNotEmpty()) {
            _mullyuData.value = newDataList[0]
        }
    }
}
import androidx.lifecycle.ViewModel
import com.example.mullyu.R
import com.example.mullyu.presentation.Mullyu
import com.example.mullyu.presentation.MullyuDataList
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.lang.Thread.State

class MullyuViewModel : ViewModel() {
    private val _imageIndex = MutableStateFlow(0)
    private val _dataList = MutableStateFlow<List<Mullyu>>(emptyList())
    private val _mullyuData = MutableStateFlow<Mullyu?>(null)
    private val _ProcessCount = MutableStateFlow(0)

    // 외부에서 참조하기 위한 변수들 변경 불가
    val imageIndex: StateFlow<Int> = _imageIndex.asStateFlow()
    val mullyuData: StateFlow<Mullyu?> = _mullyuData.asStateFlow()
    val dataList: StateFlow<List<Mullyu>> = _dataList.asStateFlow()
    val ProcessCount: StateFlow<Int> = _ProcessCount.asStateFlow()

    // Confirm 버튼을 누르면 해당 물류에 대한 처리가 완료되었음을 의미
    fun ConfirmMullyuData() {
        // 처리 완료 표시
        if (!_mullyuData.value!!.process) {
            _mullyuData.value?.let { it.process = true }
            _ProcessCount.value += 1
        }
        DisplayNextMullyuData()
    }

    // 모든 물품에 대한 처리 검사
    fun DataProcessCheck(): Boolean {
        if (_ProcessCount.value == _dataList.value.size) return true
        return false
    }

    // 다음 데이터를 보여줌
    fun DisplayNextMullyuData() {
        _imageIndex.value = (_imageIndex.value + 1) % (_dataList.value.size)
        _mullyuData.value = _dataList.value[_imageIndex.value]
    }

    fun updateDataList(newDataList: List<Mullyu>) {
        _dataList.value = newDataList
        _ProcessCount.value = newDataList.size
        if (newDataList.isNotEmpty()) {
            _mullyuData.value = newDataList[0]
        }
    }
}
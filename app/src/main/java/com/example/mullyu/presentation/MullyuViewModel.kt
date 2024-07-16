import androidx.lifecycle.ViewModel
import com.example.mullyu.R
import com.example.mullyu.presentation.Mullyu
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class MullyuViewModel : ViewModel() {

    private val Datas = listOf(
        Mullyu(R.drawable.galaxybook, "갤럭시북", "1", false),
        Mullyu(R.drawable.galaxywatch, "갤럭시워치", "2", false),
        Mullyu(R.drawable.galxys24, "갤럭시S24", "1", false),
        Mullyu(R.drawable.dongwonchamchi, "동원참치", "10", false),
        Mullyu(R.drawable.haribo, "하리보", "20", false),
        Mullyu(R.drawable.chocolate, "초콜릿", "15", false),
        Mullyu(R.drawable.galaxyring, "갤럭시링", "1", false)
    )

    private val _imageIndex = MutableStateFlow(0)
    private val _mullyuData = MutableStateFlow(Datas[0])

    val imageIndex: StateFlow<Int> = _imageIndex.asStateFlow()
    val mullyuData: StateFlow<Mullyu> = _mullyuData.asStateFlow()

    fun nextImage() {
        _imageIndex.value = (_imageIndex.value + 1) % Datas.size
        _mullyuData.value = Datas[_imageIndex.value]
    }
}
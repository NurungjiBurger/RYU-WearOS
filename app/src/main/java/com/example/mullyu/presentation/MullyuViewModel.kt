import androidx.lifecycle.ViewModel
import com.example.mullyu.R
import com.example.mullyu.presentation.Mullyu
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class MullyuViewModel : ViewModel() {

    private val Datas = listOf(
        Mullyu(R.drawable.galaxybook, "Galaxy Book", "1", false),
        Mullyu(R.drawable.galaxywatch, "Galaxy Watch", "2", false),
        Mullyu(R.drawable.galxys24, "Galaxy S24", "1", false),
        Mullyu(R.drawable.dongwonchamchi, "Dongwon Chamchi", "10", false),
        Mullyu(R.drawable.haribo, "Haribo", "20", false),
        Mullyu(R.drawable.chocolate, "Gana Chocolate", "15", false),
        Mullyu(R.drawable.galaxyring, "Galaxy Ring", "1", false)
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
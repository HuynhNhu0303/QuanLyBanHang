import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.quanlych.data.DatabaseHelper

class AdminChartViewModel(application: Application) : AndroidViewModel(application) {
    private val _totalProductsCount = MutableLiveData<Int>()
    val totalProductsCount: LiveData<Int> get() = _totalProductsCount

    private val _totalRevenue = MutableLiveData<Double>()
    val totalRevenue: LiveData<Double> get() = _totalRevenue

    private val _totalAccountCount = MutableLiveData<Int>()
    val totalAccountCount: LiveData<Int> get() = _totalAccountCount

    private val databaseHelper = DatabaseHelper(application)

    fun loadProductsCount(date: String?) {
        _totalProductsCount.value = databaseHelper.getTotalProductsCount(date)
    }

    fun loadTotalRevenue(date: String?) {
        _totalRevenue.value = databaseHelper.getTotalcoinCount(date)
    }

    fun loadAccountCount(date: String?) {
        val counts = databaseHelper.getAccountCount(date)
        _totalAccountCount.value = counts.values.sum()
    }
}
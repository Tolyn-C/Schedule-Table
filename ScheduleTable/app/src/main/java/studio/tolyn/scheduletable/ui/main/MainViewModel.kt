package studio.tolyn.scheduletable.ui.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import java.text.SimpleDateFormat
import java.util.*

class MainViewModel : ViewModel() {

    enum class Operator {
        MINUS, PLUS
    }

    private val _burnDate = MutableLiveData<String>().apply { value = "Today" }
    val burnDate: LiveData<String> = _burnDate

    private val nowByCache = MutableLiveData(Calendar.getInstance())

    init {
        refreshDataForCurrentMode()
    }

    fun minusDate() {
        changeDate(Operator.MINUS)
    }

    fun plusDate() {
        changeDate(Operator.PLUS)
    }

    private fun changeDate(op: Operator) {
        val opValue = when (op) {
            Operator.MINUS -> -1
            Operator.PLUS -> 1
        }
        val opDateRange = Calendar.WEEK_OF_MONTH

        val dayForThisWeek = Calendar.getInstance()
        dayForThisWeek.set(Calendar.DAY_OF_WEEK, dayForThisWeek.firstDayOfWeek)

        if ((dayForThisWeek.get(Calendar.DAY_OF_YEAR) != nowByCache.value!!.get(Calendar.DAY_OF_YEAR)) ||
            (dayForThisWeek.get(Calendar.YEAR) != nowByCache.value!!.get(Calendar.YEAR)) ||
            op == Operator.PLUS
        ) {
            nowByCache.value!!.add(opDateRange, opValue)
            refreshDataForCurrentMode()
        }
    }

    private fun refreshDataForCurrentMode() {
        val startDayFormatter = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val endDayFormatter = SimpleDateFormat("MM-dd", Locale.getDefault())
        nowByCache.value?.let { cacheDate ->
            cacheDate.set(Calendar.DAY_OF_WEEK, cacheDate.firstDayOfWeek)
            val startAt = startDayFormatter.format(cacheDate.time)
            cacheDate.add(Calendar.DAY_OF_MONTH, +6)
            val endAt = endDayFormatter.format(cacheDate.time)
            cacheDate.add(Calendar.DAY_OF_MONTH, -6)
            _burnDate.value = "$startAt - $endAt"
            println("burnDate ${burnDate.value}")
        }
    }

    private fun loadReview() {

    }
}
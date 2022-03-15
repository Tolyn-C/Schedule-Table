package studio.tolyn.scheduletable.ui.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import studio.tolyn.scheduletable.api.ScheduleClient
import studio.tolyn.scheduletable.api.TimePoint
import studio.tolyn.scheduletable.ui.main.Application.Companion.END_FORMATTER
import studio.tolyn.scheduletable.ui.main.Application.Companion.START_FORMATTER
import java.lang.Exception
import java.util.*

class MainViewModel : ViewModel() {

    enum class Operator {
        MINUS, PLUS
    }

    private val _burnDate = MutableLiveData<String>().apply { value = "Today" }
    val burnDate: LiveData<String> = _burnDate

    private val _firstDayOfSearchWeek = MutableLiveData(Calendar.getInstance())
    val firstDayOfSearchWeek: LiveData<Calendar> = _firstDayOfSearchWeek

    private val _scheduleTable: MutableLiveData<List<TimePoint>> = MutableLiveData(null)
    val scheduleTable: LiveData<List<TimePoint>> = _scheduleTable

    val dataUpdatedAt = MutableLiveData(System.currentTimeMillis())

    val errorText: MutableLiveData<String?> = MutableLiveData(null)

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
        //get the first day of this week.
        val dayForThisWeek = Calendar.getInstance()
        dayForThisWeek.set(Calendar.DAY_OF_WEEK, dayForThisWeek.firstDayOfWeek)
        //prevent to get the past week.
        firstDayOfSearchWeek.value?.let { nowByCache ->
            if ((dayForThisWeek.get(Calendar.DAY_OF_YEAR) != nowByCache.get(Calendar.DAY_OF_YEAR)) ||
                (dayForThisWeek.get(Calendar.YEAR) != nowByCache.get(Calendar.YEAR)) ||
                op == Operator.PLUS
            ) {
                nowByCache.add(opDateRange, opValue)
                refreshDataForCurrentMode()
            }
        }
    }

    private fun refreshDataForCurrentMode() {
        firstDayOfSearchWeek.value?.let { cacheDate ->
            cacheDate.set(Calendar.DAY_OF_WEEK, cacheDate.firstDayOfWeek)
            val startAt = START_FORMATTER.format(cacheDate.time)
            cacheDate.add(Calendar.DAY_OF_MONTH, +6)
            val endAt = END_FORMATTER.format(cacheDate.time)
            cacheDate.add(Calendar.DAY_OF_MONTH, -6)
            _burnDate.value = "$startAt - $endAt"
            loadSchedule()
        }
    }

    private fun loadSchedule() {
        val gmtFormatter = Application.GMT_FORMATTER
        gmtFormatter.timeZone = TimeZone.getTimeZone("GMT")
        firstDayOfSearchWeek.value?.let {
            val gmtTimeString: String = gmtFormatter.format(it.time)
            viewModelScope.launch {
                try {
                    _scheduleTable.value = ScheduleClient().getScheduleTimeSlotList(gmtTimeString)
                    dataUpdatedAt.value = System.currentTimeMillis()
                } catch (e: Exception) {
                    errorText.value = e.message
                }
            }
        }
    }

}
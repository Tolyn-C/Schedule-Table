package studio.tolyn.scheduletable.ui.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.GsonBuilder
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import studio.tolyn.scheduletable.BuildConfig
import studio.tolyn.scheduletable.api.ScheduleApi
import studio.tolyn.scheduletable.api.ScheduleResult
import studio.tolyn.scheduletable.ui.main.Application.Companion.END_FORMATTER
import studio.tolyn.scheduletable.ui.main.Application.Companion.START_FORMATTER
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

class MainViewModel : ViewModel() {

    enum class Operator {
        MINUS, PLUS
    }

    private val _burnDate = MutableLiveData<String>().apply { value = "Today" }
    val burnDate: LiveData<String> = _burnDate

    private val _firstDayOfSearchWeek = MutableLiveData(Calendar.getInstance())
    val firstDayOfSearchWeek: LiveData<Calendar> = _firstDayOfSearchWeek

    private val scheduleApiClient: ScheduleApi by lazy {
        getRetrofit(BuildConfig.SCHEDULE_API_URL).create(ScheduleApi::class.java)
    }

    private val _scheduleTable: MutableLiveData<ScheduleResult?> = MutableLiveData(null)
    val scheduleTable: LiveData<ScheduleResult?> = _scheduleTable

    val dataUpdatedAt = MutableLiveData(System.currentTimeMillis())

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
            dataUpdatedAt.value = System.currentTimeMillis()
        }
    }

    private fun loadSchedule() {
        val gmtFormatter = Application.GMT_FORMATTER
        gmtFormatter.timeZone = TimeZone.getTimeZone("GMT")
        firstDayOfSearchWeek.value?.let {
            val gmtTimeString: String = gmtFormatter.format(it.time)
            viewModelScope.launch {
                scheduleApiClient.getTimeSlot(BuildConfig.TEACHER_NAME, gmtTimeString)
                    .body().let { result ->
                        _scheduleTable.value = result
                    }
            }
        }
    }

    private fun getRetrofit(apiBase: String): Retrofit {
        val gson = GsonBuilder()
            .setLenient()
            .create()

        val interceptor = HttpLoggingInterceptor()
        interceptor.level = HttpLoggingInterceptor.Level.NONE

        val client: OkHttpClient = OkHttpClient.Builder()
            .addInterceptor(interceptor)
            .writeTimeout(20, TimeUnit.SECONDS)
            .readTimeout(20, TimeUnit.SECONDS).build()

        return Retrofit.Builder()
            .baseUrl(apiBase)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
    }
}
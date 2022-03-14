package studio.tolyn.scheduletable.api

import android.os.Parcelable
import com.google.gson.GsonBuilder
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import studio.tolyn.scheduletable.BuildConfig
import studio.tolyn.scheduletable.ui.main.Application.Companion.GMT_FORMATTER
import studio.tolyn.scheduletable.ui.main.Application.Companion.TIME_POINT
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.collections.ArrayList

class ScheduleClient {

    private val scheduleApiClient: ScheduleApi by lazy {
        getRetrofit(BuildConfig.SCHEDULE_API_URL).create(ScheduleApi::class.java)
    }

    suspend fun getScheduleTimeSlotList(gmtTimeString: String): List<TimePoint>? {
        return getScheduleTable(gmtTimeString)
    }

    private suspend fun getScheduleTable(gmtTimeString: String): List<TimePoint>? {
        return scheduleApiClient.getTimeSlot(BuildConfig.TEACHER_NAME, gmtTimeString).body().let {
            it?.let {
                val arrayList: ArrayList<TimePoint> = arrayListOf()
                arrayList.addAll(statisticsTimePoint(it.available, true))
                arrayList.addAll(statisticsTimePoint(it.booked, false))
                arrayList.sortBy { timePoint ->
                    timePoint.startTime
                }
                arrayList
            }
        }
    }

    private fun getRetrofit(apiBase: String): Retrofit {
        val gson = GsonBuilder()
            .setLenient()
            .create()

        val interceptor = HttpLoggingInterceptor()
        interceptor.level = HttpLoggingInterceptor.Level.BODY

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

    private fun statisticsTimePoint(
        timeSlotList: List<TimeSlot>?,
        isAvailable: Boolean
    ): ArrayList<TimePoint> {
        val arrayList = arrayListOf<TimePoint>()
        timeSlotList?.forEach { timeSlot ->
            GMT_FORMATTER.timeZone = TimeZone.getTimeZone("UTC")
            val startTime = GMT_FORMATTER.parse(timeSlot.start)
            val endTime = GMT_FORMATTER.parse(timeSlot.end)
            val timePointCount = (endTime.time - startTime.time).div(TIME_POINT) - 1
            for (index in 0..timePointCount) {
                val c: Calendar = Calendar.getInstance()
                c.time = startTime
                c.add(Calendar.MINUTE, (30 * index).toInt())
                arrayList.add(TimePoint(c, isAvailable))
            }
        }
        return arrayList
    }
}

@Parcelize
data class TimePoint(
    @SerializedName("startTime")
    val startTime: Calendar,

    @SerializedName("isAvailable")
    val isAvailable: Boolean
) : Parcelable
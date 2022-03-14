package studio.tolyn.scheduletable.api

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface ScheduleApi {
    @GET("{teacherName}/schedule")
    suspend fun getTimeSlot(
        @Path("teacherName") teacherName: String,
        @Query("started_at") startAt: String
    ): Response<ScheduleResult?>
}

enum class Status {
    Available, Booked
}

data class ScheduleResult(
    @SerializedName("available")
    val available: List<TimeSlot>? = null,
    @SerializedName("booked")
    val booked: List<TimeSlot>? = null
)

@Parcelize
data class TimeSlot(
    /* Created date in ISO8601 date format. */
    @SerializedName("start")
    val start: String? = null,
    @SerializedName("end")
    val ens: String? = null
) : Parcelable
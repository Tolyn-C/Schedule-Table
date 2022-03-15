package studio.tolyn.scheduletable.api

import com.google.gson.annotations.SerializedName
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

data class ScheduleResult(
    @SerializedName("available")
    val available: List<TimeSlot>? = null,
    @SerializedName("booked")
    val booked: List<TimeSlot>? = null
)

data class TimeSlot(
    /* Created date in ISO8601 date format. */
    @SerializedName("start")
    val start: String? = null,
    @SerializedName("end")
    val end: String? = null
)
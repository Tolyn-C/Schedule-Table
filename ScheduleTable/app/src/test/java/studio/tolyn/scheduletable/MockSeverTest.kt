package studio.tolyn.scheduletable

import kotlinx.coroutines.runBlocking
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import okio.buffer
import okio.source
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.core.Is.`is`
import org.junit.After
import org.junit.Before
import org.junit.Test
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import studio.tolyn.scheduletable.api.ScheduleApi

class MockSeverTest {
    private lateinit var service: ScheduleApi
    private lateinit var mockWebServer: MockWebServer

    @Before
    fun setup() {
        mockWebServer = MockWebServer()
        service = Retrofit.Builder()
            .baseUrl(mockWebServer.url("/"))
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ScheduleApi::class.java)
    }

    @After
    fun dropdown() {
        mockWebServer.shutdown()
    }

    @Test
    fun searchRepoThenResponse() {
        val teacherName = "jamie-coleman"
        val startAt = "2022-03-16T16:00:00.000Z"
        val startedAt = startAt.replace(":", "%3A")
        enqueueResponse("search-repo.json")
        val response = runBlocking {
            service.getTimeSlot(teacherName, startAt)
        }
        val request = mockWebServer.takeRequest()
        assertThat(request.path, `is`("/$teacherName/schedule?started_at=$startedAt"))
        val items = response.body()
        items?.available?.let {
            assertThat(it[0].start, `is`("2022-03-17T10:00:00Z"))
            assertThat(it[0].end, `is`("2022-03-17T11:30:00Z"))
        }
        items?.booked?.let {
            assertThat(it[0].start, `is`("2022-03-17T02:30:00Z"))
            assertThat(it[0].end, `is`("2022-03-17T05:30:00Z"))
        }
    }

    private fun enqueueResponse(fileName: String, headers: Map<String, String> = emptyMap()) {
        val classloader = javaClass.classLoader
        val inputStream = classloader.getResourceAsStream("api-response/$fileName")
        val source = inputStream.source().buffer()
        val mockResponse = MockResponse()
        for ((key, value) in headers) {
            mockResponse.addHeader(key, value)
        }
        mockWebServer.enqueue(
            mockResponse
                .setBody(source.readString(Charsets.UTF_8))
        )
    }
}
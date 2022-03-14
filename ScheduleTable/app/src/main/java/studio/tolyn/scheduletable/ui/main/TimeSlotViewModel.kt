package studio.tolyn.scheduletable.ui.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import studio.tolyn.scheduletable.ui.main.Application.Companion.TIME_ZONE_FORMATTER
import java.util.*

class TimeSlotViewModel : ViewModel() {

    val timeZone: LiveData<String> =
        MutableLiveData(TIME_ZONE_FORMATTER.format(Calendar.getInstance().time))
}
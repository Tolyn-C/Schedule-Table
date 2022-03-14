package studio.tolyn.scheduletable.ui.main

import android.annotation.SuppressLint
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.tabs.TabLayoutMediator
import studio.tolyn.scheduletable.R
import studio.tolyn.scheduletable.api.ScheduleResult
import studio.tolyn.scheduletable.api.TimeSlot
import studio.tolyn.scheduletable.databinding.MainFragmentBinding
import studio.tolyn.scheduletable.ui.main.Application.Companion.TAB_FORMATTER
import studio.tolyn.scheduletable.ui.main.Application.Companion.TIME_SLOT_LIST
import java.text.SimpleDateFormat
import java.util.*
import java.util.logging.SimpleFormatter
import kotlin.math.roundToInt

class MainFragment : Fragment() {

    companion object {
        fun newInstance() = MainFragment()
    }

    private lateinit var binding: MainFragmentBinding
    private lateinit var viewModel: MainViewModel
    private lateinit var dateCollectionAdapter: DateCollectionAdapter
    private lateinit var viewPager: ViewPager2

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        viewModel = ViewModelProvider(this)[MainViewModel::class.java]
        binding = MainFragmentBinding.inflate(inflater, container, false)
        binding.vm = viewModel
        binding.lifecycleOwner = viewLifecycleOwner
        binding.fragment = this
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setScheduleContainer()
        dateCollectionAdapter = DateCollectionAdapter(this)
        viewPager = view.findViewById(R.id.timeSlotPager)
        viewPager.adapter = dateCollectionAdapter
        viewModel.dataUpdatedAt.observe(viewLifecycleOwner, androidx.lifecycle.Observer {
            viewModel.firstDayOfSearchWeek.value?.let {
                TabLayoutMediator(binding.dateTab, viewPager) { tab, position ->
                    val pastDayCount =
                        Calendar.getInstance().get(Calendar.DAY_OF_WEEK) + position
                    val cacheTime = it
                    cacheTime.add(Calendar.DAY_OF_MONTH, pastDayCount)
                    tab.text = TAB_FORMATTER.format(cacheTime.time)
                    cacheTime.add(Calendar.DAY_OF_MONTH, pastDayCount * (-1))
                }.attach()
            }
        })
    }

    private fun setScheduleContainer() {
        with(BottomSheetBehavior.from(binding.scheduleContainer)) {
            peekHeight = dpToPx(60)
            state = BottomSheetBehavior.STATE_EXPANDED
            isDraggable = true
        }
    }

    private fun dpToPx(dp: Int): Int {
        val density: Float = requireContext().resources.displayMetrics.density
        return (dp.toFloat() * density).roundToInt()
    }

    private inner class DateCollectionAdapter(fragment: Fragment) : FragmentStateAdapter(fragment) {
        override fun getItemCount(): Int {
            return (7 - Calendar.getInstance().get(Calendar.DAY_OF_WEEK) + 1)
        }

        @SuppressLint("SimpleDateFormat")
        override fun createFragment(position: Int): Fragment {
            val fragment = TimeSlotFragment()

            fragment.arguments = Bundle().apply {
//            putParcelableArrayList(TIME_SLOT_LIST,)
            }
            return fragment
        }

//        private fun getPositionTimeSlotList(position: Int, timeSlotList :List<TimeSlot>): List<TimeSlot>{
//        }
    }

}


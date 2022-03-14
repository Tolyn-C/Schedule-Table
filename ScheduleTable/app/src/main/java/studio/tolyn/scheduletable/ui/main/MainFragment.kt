package studio.tolyn.scheduletable.ui.main

import android.annotation.SuppressLint
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebViewClient
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.tabs.TabLayoutMediator
import studio.tolyn.scheduletable.BuildConfig
import studio.tolyn.scheduletable.R
import studio.tolyn.scheduletable.api.TimePoint
import studio.tolyn.scheduletable.databinding.MainFragmentBinding
import studio.tolyn.scheduletable.ui.main.Application.Companion.START_FORMATTER
import studio.tolyn.scheduletable.ui.main.Application.Companion.TAB_FORMATTER
import studio.tolyn.scheduletable.ui.main.Application.Companion.TIME_POINT_LIST
import java.util.*
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
        openTeacherWebView()
        viewModel.dataUpdatedAt.observe(viewLifecycleOwner) {
            //update the tab layout and viewPager
            dateCollectionAdapter = DateCollectionAdapter(this)
            viewPager = view.findViewById(R.id.timeSlotPager)
            viewPager.adapter = dateCollectionAdapter
            viewModel.firstDayOfSearchWeek.value?.let {
                TabLayoutMediator(binding.dateTab, viewPager) { tab, position ->
                    it.add(Calendar.DAY_OF_MONTH, position)
                    tab.text = TAB_FORMATTER.format(it.time)
                    it.add(Calendar.DAY_OF_MONTH, position * (-1))
                }.attach()
                if (it.time.time < Calendar.getInstance().time.time){
                    viewPager.currentItem = (Calendar.getInstance().get(Calendar.DAY_OF_WEEK)-1)
                }
            }
        }
    }

    private fun setScheduleContainer() {
        with(BottomSheetBehavior.from(binding.scheduleContainer)) {
            peekHeight = dpToPx(60)
            state = BottomSheetBehavior.STATE_EXPANDED
            isDraggable = true
        }
    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun openTeacherWebView() {
        binding.webView.webViewClient = WebViewClient()
        binding.webView.settings.javaScriptEnabled = true
        binding.webView.loadUrl(BuildConfig.ABOUT_TEACHER_URL)
    }

    private fun dpToPx(dp: Int): Int {
        val density: Float = requireContext().resources.displayMetrics.density
        return (dp.toFloat() * density).roundToInt()
    }

    private inner class DateCollectionAdapter(fragment: Fragment) : FragmentStateAdapter(fragment) {
        override fun getItemCount(): Int = 7

        override fun createFragment(position: Int): Fragment {
            val fragment = TimeSlotFragment()
            fragment.arguments = Bundle().apply {
                val timePointList = arrayListOf<TimePoint>()

                //filter the day time point
                viewModel.scheduleTable.value?.let {
                    it.filter { timePoint ->
                        viewModel.firstDayOfSearchWeek.value?.let { firstDayForWeek ->
                            firstDayForWeek.add(Calendar.DAY_OF_MONTH, position)
                            val isTheDay =
                                START_FORMATTER.format(timePoint.startTime.time) ==
                                        START_FORMATTER.format(firstDayForWeek.time)
                            firstDayForWeek.add(Calendar.DAY_OF_MONTH, position * (-1))
                            isTheDay
                        } ?: false
                    }.let { filtered ->
                        timePointList.addAll(filtered)
                    }
                }

                //Put filtered arrayList in Bundle
                putParcelableArrayList(TIME_POINT_LIST, timePointList)
            }
            return fragment
        }
    }

}


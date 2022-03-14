package studio.tolyn.scheduletable.ui.main

import android.content.Context
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.lifecycle.viewModelScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.launch
import studio.tolyn.scheduletable.R
import studio.tolyn.scheduletable.api.TimePoint
import studio.tolyn.scheduletable.databinding.TimeSlotFragmentBinding
import studio.tolyn.scheduletable.ui.main.Application.Companion.TIME_FORMATTER

class TimeSlotFragment : Fragment() {

    companion object {
        fun newInstance() = TimeSlotFragment()
    }

    private lateinit var viewModel: TimeSlotViewModel
    private lateinit var binding: TimeSlotFragmentBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewModel = ViewModelProvider(this)[TimeSlotViewModel::class.java]
        binding = TimeSlotFragmentBinding.inflate(inflater, container, false)
        binding.vm = viewModel
        binding.lifecycleOwner = viewLifecycleOwner
        binding.fragment = this
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
        arguments?.takeIf { it.containsKey(Application.TIME_POINT_LIST) }?.apply {
            this.getParcelableArrayList<TimePoint>(Application.TIME_POINT_LIST)
                ?.let { timePointList ->
                    binding.recyclerView.adapter = TimePointAdapter(requireContext()).also {
                        it.items = timePointList.toList()
                    }
                }
        }
    }

    inner class TimePointAdapter(private val context: Context) :
        RecyclerView.Adapter<TimePointHolder>() {
        var items: List<TimePoint> = emptyList()

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TimePointHolder {
            val root =
                LayoutInflater.from(context).inflate(R.layout.time_point_item, parent, false)
            return TimePointHolder(root)
        }

        override fun onBindViewHolder(holder: TimePointHolder, position: Int) {
            holder.timePoint = items[position]
        }

        override fun getItemCount(): Int {
            return items.size
        }

    }

    inner class TimePointHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val timePointTextView = itemView.findViewById<TextView>(R.id.timePointTextView)
        private val baseLayout = itemView.findViewById<ConstraintLayout>(R.id.base)
        var timePoint: TimePoint? = null
            set(value) {
                field = value
                value?.let {
                    timePointTextView.text = TIME_FORMATTER.format(it.startTime.time)
                    if (it.isAvailable) {
                        timePointTextView.setTextColor(requireContext().getColor(R.color.available))
                        baseLayout.background =
                            requireContext().getDrawable(R.drawable.bg_available)
                    } else {
                        timePointTextView.setTextColor(requireContext().getColor(R.color.black))
                        baseLayout.background = requireContext().getDrawable(R.drawable.bg_booked)
                    }
                }
            }
    }

}
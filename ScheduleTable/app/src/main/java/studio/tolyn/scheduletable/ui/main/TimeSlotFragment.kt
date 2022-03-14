package studio.tolyn.scheduletable.ui.main

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import studio.tolyn.scheduletable.databinding.TimeSlotFragmentBinding

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
        arguments?.takeIf { it.containsKey(Application.TIME_SLOT_LIST) }?.apply {

        }
    }

}
package com.example.parcmarc

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.RecyclerView

class AllParksFragment : Fragment(), ParkAdapter.OnParkListener {

    private val viewModel: ParkViewModel by activityViewModels() {
        ParkViewModelFactory((activity?.application as ParcMarcApplication).repository)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_all_parks, container, false)

        val parkAdapter = ParkAdapter(listOf(),this)
        viewModel.parks.observe(viewLifecycleOwner, { newParks ->
            parkAdapter.setData(newParks)
        })

        // Uncomment to quickly add test data
//        for (i in 0..20) {
//            viewModel.addPark(Park("Disney World", 0.1, 0.2))
//        }

        val recyclerView: RecyclerView = view.findViewById(R.id.recycler_view)
        recyclerView.adapter = parkAdapter
        return view
    }

    override fun onParkClick(position: Int) {
        println(position);
    }
}
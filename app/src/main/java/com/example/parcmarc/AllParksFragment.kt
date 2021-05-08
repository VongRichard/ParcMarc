package com.example.parcmarc

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.PermissionChecker.checkSelfPermission
import android.widget.Button
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.LatLng
import com.google.android.material.floatingactionbutton.FloatingActionButton

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
        for (i in 0..1) {
            val latLng = LatLng(0.1, 0.2)
            viewModel.addPark(Park("Disney World", latLng))
        }

        view.findViewById<FloatingActionButton>(R.id.newPark)?.setOnClickListener {
            findNavController().navigate(R.id.action_allParksFragment_to_createNewParkLocation)
        }

        val recyclerView: RecyclerView = view.findViewById(R.id.recycler_view)
        recyclerView.adapter = parkAdapter
        return view
    }

    override fun onParkClick(position: Int) {
        val park = viewModel.parks.value!![position]
        val navigationController = this.findNavController()
        navigationController.navigate(R.id.action_allParksFragment_to_parkFragment)
    }
}
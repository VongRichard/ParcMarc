package com.example.parcmarc

import android.Manifest
import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.PermissionChecker.checkSelfPermission
import android.widget.Button
import androidx.fragment.app.activityViewModels
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.LatLng
import com.google.android.material.floatingactionbutton.FloatingActionButton
import java.io.File
import java.util.*

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
        viewModel.parks.observe(viewLifecycleOwner, { newParksWithImages ->
            parkAdapter.setData(newParksWithImages)
        })

        view.findViewById<FloatingActionButton>(R.id.newPark)?.setOnClickListener {
            findNavController().navigate(R.id.action_allParksFragment_to_createNewParkLocation)
        }

        val recyclerView: RecyclerView = view.findViewById(R.id.recycler_view)
        recyclerView.adapter = parkAdapter
        return view
    }

    override fun onParkClick(position: Int) {
        val parkWithParkImages = viewModel.parks.value!![position]
        val action = AllParksFragmentDirections.actionAllParksFragmentToParkFragment(parkWithParkImages)
        val navigationController = this.findNavController()
        navigationController.navigate(action)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val toolbar = view.findViewById<androidx.appcompat.widget.Toolbar>(R.id.allParksToolbar)
        setUpToolbar(toolbar)
    }

    private fun setUpToolbar(toolbar: androidx.appcompat.widget.Toolbar) {
        toolbar.inflateMenu(R.menu.all_parks_menu);
        toolbar.title = getString(R.string.app_name)
        toolbar.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.settingsItem -> {
                    //TODO Open the Settings Screen
                    true
                }
                else -> false
            }
        }
    }
}
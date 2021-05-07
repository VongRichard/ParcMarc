package com.example.parcmarc

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.navArgs
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.MarkerOptions

class ParkFragment : Fragment(), OnMapReadyCallback {
    private lateinit var map: GoogleMap
    private lateinit var mapView: MapView
    private val args: ParkFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_park, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (activity as AppCompatActivity).supportActionBar?.title = args.park.name

        mapView = requireView().findViewById<MapView>(R.id.mapView)
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this)
    }

    @SuppressLint("MissingPermission")
    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap
        map.addMarker(
            MarkerOptions()
                .position(args.park.location)
                .title("Location")
        )
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(args.park.location, 15F))

        map.setOnMapLongClickListener {
            // TODO Open in Google Maps?
        }

        val mainActivity = activity as MainActivity
        if (mainActivity.hasLocationPermissions) {
            map.isMyLocationEnabled = true
        }

        mapView.onResume()
    }
}
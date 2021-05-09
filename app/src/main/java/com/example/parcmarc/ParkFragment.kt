package com.example.parcmarc

import android.annotation.SuppressLint
import android.graphics.Matrix
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Toolbar
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.Navigation
import androidx.navigation.fragment.navArgs
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.MarkerOptions

class ParkFragment : Fragment(), OnMapReadyCallback {
    private lateinit var map: GoogleMap
    private lateinit var mapView: MapView
    private val args: ParkFragmentArgs by navArgs()

    fun onImageViewClick(imageView: ImageView) {
        //TODO navigate to ImageFragment
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_park, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val toolbar = view.findViewById<androidx.appcompat.widget.Toolbar>(R.id.toolbar)
        toolbar.inflateMenu(R.menu.park_menu);
        toolbar.title = args.park.name
        toolbar.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.editItem -> {
                    true
                }
                R.id.deleteItem -> {
                    true
                }
                R.id.shareItem -> {
                    true
                }
                else -> false
            }
        }

        mapView = requireView().findViewById<MapView>(R.id.mapView)
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this)

        val imageView1 = requireView().findViewById<ImageView>(R.id.park_image_1)
        imageView1.setOnClickListener { onImageViewClick(imageView1) }
        val imageView2 = requireView().findViewById<ImageView>(R.id.park_image_2)
        imageView2.setOnClickListener { onImageViewClick(imageView2) }
        val imageView3 = requireView().findViewById<ImageView>(R.id.park_image_3)
        imageView3.setOnClickListener { onImageViewClick(imageView3) }
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
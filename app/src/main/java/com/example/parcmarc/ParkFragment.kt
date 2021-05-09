package com.example.parcmarc

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
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
import androidx.fragment.app.activityViewModels
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.MarkerOptions

class ParkFragment : Fragment(), OnMapReadyCallback {
    private val viewModel: ParkViewModel by activityViewModels() {
        ParkViewModelFactory((activity?.application as ParcMarcApplication).repository)
    }
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
        setUpToolbar(toolbar)

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

    fun setUpToolbar(toolbar: androidx.appcompat.widget.Toolbar) {
        toolbar.inflateMenu(R.menu.park_menu);
        toolbar.title = args.park.name
        toolbar.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.editItem -> {
                    //TODO Open the Edit Screen
                    true
                }
                R.id.deleteItem -> {
                    val builder = AlertDialog.Builder(activity)
                    builder.setCancelable(false)
                    builder.setTitle("Are you sure you want to delete this Park?")
                    builder.apply {
                        setPositiveButton("Delete") { dialog, id ->
                            viewModel.removePark(args.park)
                            this@ParkFragment.findNavController().popBackStack();
                        }
                        setNegativeButton("Cancel") { dialog, id ->
                        }
                    }
                    builder.show()
                    true
                }
                R.id.shareItem -> {
                    val intent = Intent(Intent.ACTION_SEND)
                    intent.type = "text/plain"
                    intent.putExtra(Intent.EXTRA_SUBJECT, args.park.name + " Location")
                    intent.putExtra(Intent.EXTRA_TEXT, generateShareBody())
                    startActivity(intent)
                    true
                }
                else -> false
            }
        }
    }

    /**
     * Creates and returns a message body including a Google Maps link to the Park's location.
     */
    private fun generateShareBody(): String {
        return "I've parked my car at these coordinates: " + args.park.location.toString() +
                "\n\n" + "Link:\n" + "https://maps.google.com/?q=" + args.park.location.latitude +
                "," + args.park.location.longitude
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
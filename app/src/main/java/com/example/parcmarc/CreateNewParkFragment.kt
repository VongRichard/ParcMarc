package com.example.parcmarc

import android.Manifest
import android.app.Activity
import android.app.AlarmManager
import android.app.AlertDialog
import android.app.TimePickerDialog
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.net.Uri
import android.os.Bundle
import android.preference.PreferenceManager
import android.provider.MediaStore
import android.text.InputType
import android.view.*
import android.view.inputmethod.EditorInfo
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.core.content.FileProvider
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import nz.ac.canterbury.seng440.backlog.TimePickerFragment
import java.io.File
import java.util.*

private const val REQUEST_CAMERA = 110
private const val REQUEST_GALLERY = 111

class CreateNewParkFragment : Fragment(), TimePickerDialog.OnTimeSetListener {

    private val viewModel: ParkViewModel by activityViewModels() {
        ParkViewModelFactory((activity?.application as ParcMarcApplication).repository)
    }

    val hasLocationPermissions
        get() = context?.checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED

    private var images: MutableList<File> = mutableListOf()
    private lateinit var parkLocation: LatLng

    private lateinit var locationValue: TextView
    private lateinit var nameValue: EditText
    private lateinit var timeLimitValue: TextView
    private lateinit var prefs: SharedPreferences
    private lateinit var imagesLayout: LinearLayout


    private val photoDirectory
        get() = File(context?.getExternalFilesDir(null), "parc")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        prefs = PreferenceManager.getDefaultSharedPreferences(context)
//        updateImageViews()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_create_new_park, container, false)

        view.findViewById<Button>(R.id.addImageButton)?.setOnClickListener {
            if (images.size < 3) {
                promptForAdd()
            }
        }

        view.findViewById<Button>(R.id.saveParkButton)?.setOnClickListener {
            addNewPark()
        }

        view.findViewById<ImageButton>(R.id.updateLocationButton)?.setOnClickListener {
            updateLocation()
        }
        //update location by default
        updateLocation()

        locationValue = view.findViewById(R.id.locationValue)
        nameValue = view.findViewById(R.id.editTextName)
        timeLimitValue = view.findViewById(R.id.timeLimitValue)

        imagesLayout = view.findViewById(R.id.imagesLayout)

        timeLimitValue.setOnClickListener {
            setFinalTime()
        }

        return view
    }
    private fun updateImageViews() {
        imagesLayout.removeAllViews()

        for (image in images) {
            addImageView(image)
        }

    }

    private fun intToSp(integer: Int): Int {
        return (resources.displayMetrics.density*integer + 1f).toInt()
    }

    private fun addImageView(image: File) {
        val imageView = ImageView(context)
        val bitmap = BitmapFactory.decodeFile(image.absolutePath)
        imageView.setImageBitmap(bitmap)
        val layoutParams: LinearLayout.LayoutParams = LinearLayout.LayoutParams(
//                LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.MATCH_PARENT)
            intToSp(120), intToSp(120))
        imageView.layoutParams = layoutParams
        imageView.tag = "${image.nameWithoutExtension}View"
        imagesLayout.addView(imageView)
    }

    private fun addNewPark() {
        var endTime: Date? = Date()
        val hour = prefs.getInt("hour", 0)
        val minutes = prefs.getInt("minute", 0)
        if (hour == minutes && minutes == 0) {
            endTime = null
        } else {
            endTime!!.hours = endTime.hours + hour
            endTime.minutes = endTime.minutes + minutes

        }

        val park = Park(
            nameValue.text.toString(),
            parkLocation,
            endTime
        )

        viewModel.addPark(park, images)
        findNavController().popBackStack()
    }

    private fun setFinalTime() {
        val fragment = TimePickerFragment()
        fragment.listener = this
        fragment.hour = prefs.getInt("hour", 6)
        fragment.minute = prefs.getInt("minute", 0)
        activity?.let { fragment.show(it.supportFragmentManager, null) }
    }

    override fun onTimeSet(picker: TimePicker, hour: Int, minute: Int) {

        prefs.edit().apply {
            putInt("hour", hour)
            putInt("minute", minute)
            apply()
        }
        var duration = "Unlimited"
        if (!(hour == minute && minute == 0)) {
            duration = "$hour hour(s), $minute minute(s)"
        }
        timeLimitValue.text = duration
    }


    private fun promptForAdd() {
        val builder = AlertDialog.Builder(requireContext()).apply {
            setTitle("Choose source")
            setMessage("Where is the photo?")
            setPositiveButton("Camera") { _, _ ->
                takePictureFromCamera()
            }
            setNegativeButton("Gallery") { _, _ ->
                takePictureFromGallery()
            }
        }
        builder.show()
    }


    private fun dayFile(year: Int, month: Int, day: Int, hour: Int, minute: Int, second: Int): File {

        val file = File(photoDirectory, String.format("%02d_%02d_%02d_%02d_%02d_%02d.jpg",
                                        year, month, day, hour, minute, second))
        file.parentFile.mkdirs()
        images.add(file)
        return file
    }

    // Exercise 3 - FileProvider XML

    // Exercise 4
    private fun dayUri(date: Date): Uri {
        val file = dayFile(date.year, date.month, date.day, date.hours, date.minutes, date.seconds)
        val uri = FileProvider.getUriForFile(requireContext(), "com.example.parcmarc.fileprovider", file)
        return uri
    }

    // Exercise 5
    private fun takePictureFromCamera() {
        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        val uri = dayUri(Date())
        takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, uri)
        try {
            startActivityForResult(takePictureIntent, REQUEST_CAMERA)
        } catch (e: ActivityNotFoundException) {
            // display error state to the user
        }
    }

    private fun updateLocation() {
        val locationManager = requireActivity().getSystemService(Context.LOCATION_SERVICE) as LocationManager
        val listener = object : LocationListener {
            override fun onLocationChanged(location: Location) {
                parkLocation = LatLng(location.latitude, location.longitude)
                val locationText = "${location.latitude}, ${location.longitude}"
                locationValue!!.text = locationText
            }

            override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {}
            override fun onProviderDisabled(provider: String) {}
            override fun onProviderEnabled(provider: String) {}
        }
        if (hasLocationPermissions) {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0f, listener)
        }
    }


    // Exercise 6
    private fun takePictureFromGallery() {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.addCategory(Intent.CATEGORY_OPENABLE)
        intent.type = "image/*"
        startActivityForResult(intent, REQUEST_GALLERY)
    }

    // Exercise 7
    private fun copyUriToUri(from: Uri, to: Uri) {
        context?.contentResolver?.openInputStream(from).use { input ->
            context?.contentResolver?.openOutputStream(to).use { output ->
                try {
                    input?.copyTo(output!!)
                } catch (e: NullPointerException) { }
            }
        }
    }


    // Exercise 8
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when (requestCode) {
            REQUEST_CAMERA -> {
                if (resultCode == Activity.RESULT_OK) {
                    updateImageViews()
                }
            }
            REQUEST_GALLERY -> {
                if (resultCode == Activity.RESULT_OK) {

                    data?.data?.let { uri ->
                                        copyUriToUri(uri, dayUri(Date()))
                                        updateImageViews()
                                    }


                }
            }
            else -> {
                super.onActivityResult(requestCode, resultCode, data)
            }
        }
    }

}
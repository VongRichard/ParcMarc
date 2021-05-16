package com.example.parcmarc

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.app.TimePickerDialog
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.net.Uri
import android.os.Bundle
import android.preference.PreferenceManager
import android.provider.MediaStore
import android.util.Log
import android.view.*
import android.widget.*
import androidx.activity.addCallback
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.LatLng
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
        get() = requireContext().checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED

    private lateinit var locationValue: TextView
    private lateinit var nameValue: EditText
    private lateinit var timeLimitValue: TextView
    private lateinit var imagesLayout: LinearLayout
    private val utils: Utilities = Utilities()
    private lateinit var fusedLocationClient: FusedLocationProviderClient


    private val photoDirectory
        get() = File(context?.getExternalFilesDir(null), "parc")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireContext())

        updateLocation()

        requireActivity().onBackPressedDispatcher.addCallback(this) {
            viewModel.clearCreateEditTemps()
            findNavController().popBackStack()
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_create_new_park, container, false)

        view.findViewById<Button>(R.id.addImageButton)?.setOnClickListener {
            if (viewModel.numTempImages.value!! < 3) {
                promptForAdd()
            }
        }

        view.findViewById<Button>(R.id.saveParkButton)?.setOnClickListener {
            addNewPark()
        }

        view.findViewById<ImageButton>(R.id.updateLocationButton)?.setOnClickListener {
            updateLocation()
        }



        locationValue = view.findViewById(R.id.locationValue)
        nameValue = view.findViewById(R.id.editTextName)
        timeLimitValue = view.findViewById(R.id.timeLimitValue)
        imagesLayout = view.findViewById(R.id.imagesLayout)

        timeLimitValue.setOnClickListener {
            setFinalTime()
        }

        updateImageViews()
        updateDurationHelper()

        return view
    }
    private fun updateImageViews() {
        imagesLayout.removeAllViews()

        for (image in viewModel.tempImages.value!!) {
            addImageView(image)
        }
    }


    private fun intToSp(integer: Int): Int {
        return (resources.displayMetrics.density*integer + 1f).toInt()
    }


    private fun addImageView(image: File) {
        val linearLayout = LinearLayout(context)
        linearLayout.orientation = LinearLayout.VERTICAL
        linearLayout.layoutParams = LinearLayout.LayoutParams(
            intToSp(120), LinearLayout.LayoutParams.WRAP_CONTENT)


        val imageView = ImageView(context)
        imageView.setImageBitmap(utils.getRotatedBitmapFromFile(image))
        val layoutParams: LinearLayout.LayoutParams = LinearLayout.LayoutParams(
            intToSp(120), intToSp(120))
        imageView.layoutParams = layoutParams
        imageView.tag = "${image.nameWithoutExtension}View"
        linearLayout.addView(imageView)

        val imageButton = ImageButton(context)
        imageButton.setImageResource(R.drawable.delete_icon_white)
        imageButton.setOnClickListener {
            viewModel.removeAndDeleteTempImage(image)
            updateImageViews()
        }
        val imageButtonLayoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT)
        imageButtonLayoutParams.gravity = Gravity.CENTER_HORIZONTAL
        imageButton.layoutParams = imageButtonLayoutParams
        linearLayout.addView(imageButton)
        imagesLayout.addView(linearLayout)
    }


    private fun addNewPark() {
        var endTime: Date? = Date()
        val duration = viewModel.tempDuration.value!!
        if (duration.first == duration.second && duration.second == 0) {
            endTime = null
        } else {
            endTime!!.hours = endTime.hours + duration.first
            endTime.minutes = endTime.minutes + duration.second
        }

        val park = Park(
            nameValue.text.toString(),
            viewModel.tempLocation.value!!,
            endTime
        )

        viewModel.addPark(park, viewModel.tempImages.value!!)
        viewModel.clearCreateEditTemps()
        findNavController().popBackStack()
    }

    private fun setFinalTime() {
        val fragment = TimePickerFragment()
        fragment.listener = this
        val duration = viewModel.tempDuration.value!!
        fragment.hour = duration.first
        fragment.minute = duration.second
        activity?.let { fragment.show(it.supportFragmentManager, null) }
    }

    private fun updateDurationHelper() {
        val tempDuration = viewModel.tempDuration.value!!
        var duration = "Unlimited"
        if (!(tempDuration.first == tempDuration.second && tempDuration.second == 0)) {
            duration = "${tempDuration.first} hour(s), ${tempDuration.second} minute(s)"
        }
        timeLimitValue.text = duration
    }

    override fun onTimeSet(picker: TimePicker, hour: Int, minute: Int) {
        viewModel.setDuration(Pair(hour, minute))
        updateDurationHelper()

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
        viewModel.addTempImage(file)
        return file
    }


    private fun dayUri(date: Date): Uri {
        val file = dayFile(date.year, date.month, date.day, date.hours, date.minutes, date.seconds)
        val uri = FileProvider.getUriForFile(requireContext(), "com.example.parcmarc.fileprovider", file)
        return uri
    }


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

    @SuppressLint("MissingPermission")
    private fun updateLocation() {
        if (hasLocationPermissions) {
            fusedLocationClient.lastLocation
                .addOnSuccessListener { location: Location? ->
                    if (location != null) updateLocationHelper(location)
                }
        }
    }


    private fun updateLocationHelper(location: Location) {
        viewModel.setLocation(LatLng(location.latitude, location.longitude))
        val locationText = "${location.latitude}, ${location.longitude}"
        locationValue!!.text = locationText
    }


    private fun takePictureFromGallery() {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.addCategory(Intent.CATEGORY_OPENABLE)
        intent.type = "image/*"
        startActivityForResult(intent, REQUEST_GALLERY)
    }


    private fun copyUriToUri(from: Uri, to: Uri) {
        context?.contentResolver?.openInputStream(from).use { input ->
            context?.contentResolver?.openOutputStream(to).use { output ->
                try {
                    input?.copyTo(output!!)
                } catch (e: NullPointerException) { }
            }
        }
    }


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
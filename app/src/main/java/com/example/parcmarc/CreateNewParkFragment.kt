package com.example.parcmarc

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

    private var locationValue: TextView? = view?.findViewById(R.id.locationValue)
    private var nameValue: EditText? = view?.findViewById(R.id.editTextName)
    private var timeValue: TextView? = view?.findViewById(R.id.timeLimitValue)
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
            promptForAdd()
        }

        imagesLayout = view.findViewById(R.id.imagesLayout)

        timeValue?.setOnClickListener {
            println("doo daa")
            setFinalTime()
        }

        return view
    }
    private fun updateImageViews() {
        imagesLayout.removeAllViews()

        val photos: Array<File>? =  photoDirectory
                .listFiles { file, _ -> file.isDirectory }
        if (!photos.isNullOrEmpty()) {
            for (photo in photos) {
                addImageView(photo)
            }
        }

    }

    private fun addImageView(image: File) {
        val imageView = ImageView(context)
        val bitmap = BitmapFactory.decodeFile(image.absolutePath)
        imageView.setImageBitmap(bitmap)
        val layoutParams: LinearLayout.LayoutParams = LinearLayout.LayoutParams(
//                LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.MATCH_PARENT)
                120, 120)
        imageView.layoutParams = layoutParams
        imageView.tag = "${image.nameWithoutExtension}View"
        imagesLayout.addView(imageView)
    }

    private fun addNewPark() {
        TODO("Not yet implemented")
//        val newFriend = Park(
//            nameValue.text.toString(),
//            timeValue.text.toString(),
//            homeBox.text.toString(),
//            phoneBox.text.toString(),
//            emailBox.text.toString()
//        )
//        viewModel.addFriend(newFriend)
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
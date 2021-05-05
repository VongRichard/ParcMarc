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
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.*
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.core.content.FileProvider
import androidx.fragment.app.activityViewModels
import nz.ac.canterbury.seng440.backlog.PermittedFragment
import java.io.File

private const val REQUEST_CAMERA = 110
private const val REQUEST_GALLERY = 111

class CreateNewParkFragment : PermittedFragment(), TimePickerDialog.OnTimeSetListener {

    private val viewModel: ParkViewModel by activityViewModels() {
        ParkViewModelFactory((activity?.application as ParcMarcApplication).repository)
    }
    private var locationValue: TextView? = view?.findViewById(R.id.locationValue)
    private var nameValue: EditText? = view?.findViewById(R.id.editTextName)
    private var timeValue: EditText? = view?.findViewById(R.id.editTextTime)
    private lateinit var prefs: SharedPreferences


    private val photoDirectory
        get() = File(context?.getExternalFilesDir(null), "parc")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val permissions = arrayOf(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
        requestPermissions(permissions, 100, {
//            promptForTodo()
        }, {
            Toast.makeText(requireContext(), "Unable to store photos.", Toast.LENGTH_LONG).show()
        })
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_create_new_park, container, false)

        view.findViewById<Button>(R.id.addImageButton)?.setOnClickListener {
            promptForAdd()
        }

        return view
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

//    private fun loadDayPhotos() {
//        if (photoDirectory.exists()) {
//            val photos = photoDirectory
//                .listFiles { file, _ -> file.isDirectory }
//                .map{ Photo(File(it, String.format("%02d_%02d.jpg", month, day))) }
//                .filter { it.file.exists() }
//
//            photosList.adapter = PhotoAdapter(photos)
//        }
//    }

    // Exercise 2
    private fun dayFile(year: Int, month: Int, day: Int): File {
        val file = File(photoDirectory, String.format("$year/%02d_%02d.jpg", month, day))
        file.parentFile.mkdirs()
        return file
    }

    // Exercise 3 - FileProvider XML

    // Exercise 4
    private fun dayUri(year: Int, month: Int, day: Int): Uri {
        val file = dayFile(year, month, day)
        val uri = FileProvider.getUriForFile(requireContext(), "nz.ac.canterbury.seng440.backlog.fileprovider", file)
        return uri
    }

    // Exercise 5
    private fun takePictureFromCamera() {
//        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
//        intent.resolveActivity(packageManager)?.let {
//            val uri = dayUri(year, month, day)
//            intent.putExtra(MediaStore.EXTRA_OUTPUT, uri)
//            println("camera clicked")
//            startActivityForResult(intent, REQUEST_CAMERA)
//        }

        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
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
//    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
//        when (requestCode) {
//            REQUEST_CAMERA -> {
//                if (resultCode == Activity.RESULT_OK) {
//                    loadDayPhotos()
//                }
//            }
//            REQUEST_GALLERY -> {
//                if (resultCode == Activity.RESULT_OK) {
//                    data?.data?.let { uri ->
//                        copyUriToUri(uri, dayUri(year, month, day))
//                        loadDayPhotos()
//                    }
//                }
//            }
//            else -> {
//                super.onActivityResult(requestCode, resultCode, data)
//            }
//        }
//    }

}
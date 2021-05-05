package com.example.parcmarc

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.fragment.app.activityViewModels


class CreateNewParkFragment : Fragment() {

    private val viewModel: ParkViewModel by activityViewModels() {
        ParkViewModelFactory((activity?.application as ParcMarcApplication).repository)
    }
    private var locationValue: TextView? = view?.findViewById(R.id.locationValue)
    private var nameValue: EditText? = view?.findViewById(R.id.editTextName)
    private var timeValue: EditText? = view?.findViewById(R.id.editTextTime)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_create_new_park, container, false)

        view.findViewById<Button>(R.id.addImageButton)?.setOnClickListener {
            addNewPark()
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

}
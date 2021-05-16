package com.example.parcmarc

import android.R.attr.delay
import android.app.AlertDialog
import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.util.*


private const val DELAY = 30000L


class AllParksFragment : Fragment(), ParkAdapter.OnParkListener {

    private val viewModel: ParkViewModel by activityViewModels() {
        ParkViewModelFactory((activity?.application as ParcMarcApplication).repository)
    }

    private val handler: Handler = Handler()
    private lateinit var runnable: Runnable
    private lateinit var parkAdapter: ParkAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_all_parks, container, false)

        parkAdapter = ParkAdapter(listOf(),this)
        viewModel.parks.observe(viewLifecycleOwner, { newParksWithImages ->
            parkAdapter.setData(newParksWithImages)
        })

        view.findViewById<FloatingActionButton>(R.id.newPark)?.setOnClickListener {
            findNavController().navigate(R.id.action_allParksFragment_to_createNewParkLocation)
        }

        val recyclerView: RecyclerView = view.findViewById(R.id.recycler_view)

        val swipeDeleteHandler = object : SwipeToDeleteCallback(requireContext()) {
            override fun onMove(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, target: RecyclerView.ViewHolder): Boolean {
                return false
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.adapterPosition
                val park: ParkWithParkImages = viewModel.parks.value!![position]
                promptDeletePark(park)
            }
        }

        val itemTouchHelper = ItemTouchHelper(swipeDeleteHandler)
        itemTouchHelper.attachToRecyclerView(recyclerView)


        recyclerView.adapter = parkAdapter
        return view
    }

    private fun promptDeletePark(park: ParkWithParkImages) {
        val builder = AlertDialog.Builder(activity)
        builder.setCancelable(false)
        builder.setTitle("Are you sure you want to delete this Park?")
        builder.apply {
            setPositiveButton("Delete") { dialog, id ->
                viewModel.removePark(park)
            }
            setNegativeButton("Cancel") { dialog, id ->
            }
        }
        builder.show()
    }

    override fun onParkClick(position: Int) {
        val parkWithParkImages = viewModel.parks.value!![position]
        val action = AllParksFragmentDirections.actionAllParksFragmentToParkFragment(parkWithParkImages)
        val navigationController = this.findNavController()
        navigationController.navigate(action)
    }

    override fun onResume() {
        runnable = Runnable {
            handler.postDelayed(runnable, DELAY)
            parkAdapter.notifyDataSetChanged()
            Toast.makeText(
                context, "This method is run every 30 seconds",
                Toast.LENGTH_SHORT
            ).show()
        }.also { runnable = it }

        handler.postDelayed(runnable, DELAY)
        super.onResume()
    }

    override fun onPause() {
        handler.removeCallbacks(runnable)
        super.onPause()
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
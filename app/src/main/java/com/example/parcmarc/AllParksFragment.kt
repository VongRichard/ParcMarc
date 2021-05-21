package com.example.parcmarc

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
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
            val action = AllParksFragmentDirections.actionAllParksFragmentToCreateNewParkLocation(null)
            findNavController().navigate(action)
        }

        val recyclerView: RecyclerView = view.findViewById(R.id.recycler_view)

        val swipeDeleteHandler = object : SwipeToDeleteCallback(requireContext()) {
            override fun onMove(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, target: RecyclerView.ViewHolder): Boolean {
                return false
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.adapterPosition
                val park: ParkWithParkImages = viewModel.parks.value!![position]
                promptDeletePark(park, position)
            }
        }

        val swipeEditHandler = object : SwipeToEditCallback(requireContext()) {
            override fun onMove(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, target: RecyclerView.ViewHolder): Boolean {
                return false
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val park: ParkWithParkImages = viewModel.parks.value!![viewHolder.adapterPosition]
                val action = AllParksFragmentDirections.actionAllParksFragmentToCreateNewParkLocation(park)
                findNavController().navigate(action)
            }
        }

        val itemTouchHelper1 = ItemTouchHelper(swipeDeleteHandler)
        itemTouchHelper1.attachToRecyclerView(recyclerView)

        val itemTouchHelper2 = ItemTouchHelper(swipeEditHandler)
        itemTouchHelper2.attachToRecyclerView(recyclerView)

        recyclerView.adapter = parkAdapter
        return view
    }

    private fun promptDeletePark(park: ParkWithParkImages, position: Int) {
        val builder = AlertDialog.Builder(activity)
        builder.setCancelable(false)
        builder.setTitle("Are you sure you want to delete this Park?")
        builder.apply {
            setPositiveButton("Delete") { dialog, id ->
                GlobalScope.launch {
                    viewModel.removePark(park)
                }
            }
            setNegativeButton("Cancel") { dialog, id ->
                parkAdapter.notifyItemChanged(position)
            }
        }
        builder.show()
    }

    override fun onParkClick(position: Int) {
        val parkWithParkImages = viewModel.parks.value!![position]
        val action = AllParksFragmentDirections.actionAllParksFragmentToParkFragment(position)
        val navigationController = this.findNavController()
        navigationController.navigate(action)
    }

    override fun onResume() {
        runnable = Runnable {
            handler.postDelayed(runnable, DELAY)
            parkAdapter.notifyDataSetChanged()
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
//                    findNavController().navigate(R.id.action_allParksFragment_to_settingsFragment2)
                    val intent = Intent(activity, SettingsActivity::class.java)
                    startActivity(intent)
                    true
                }
                else -> false
            }
        }
    }
}
package com.example.parcmarc

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import java.time.temporal.TemporalUnit

class ParkAdapter(private var parks: List<Park>, private val onParkListener: OnParkListener)
    : RecyclerView.Adapter<ParkAdapter.ParkViewHolder>() {

    class ParkViewHolder(itemView: View, val onParkListener: OnParkListener)
        : RecyclerView.ViewHolder(itemView), View.OnClickListener {

        val textView: TextView
        val remainingTextView: TextView

        init {
            textView = itemView.findViewById(R.id.park_name)
            remainingTextView = itemView.findViewById(R.id.time_remaining)
            itemView.setOnClickListener(this)
        }

        override fun onClick(view: View?) {
            onParkListener.onParkClick(adapterPosition)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ParkViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.park_item, parent, false)
        return ParkViewHolder(view, onParkListener)
    }

    override fun onBindViewHolder(viewHolder: ParkViewHolder, position: Int) {
        viewHolder.textView.text = parks[position].toString()
        val timeLeft = parks[position].timeLeft()
        if (timeLeft == null) {
            val unlimited = "Unlimited"
            viewHolder.remainingTextView.text = unlimited
        } else {
            val hours = timeLeft.toHours(); val minutes = timeLeft.toMinutes() - hours*60
            val timeLeftStr = "$hours:$minutes"
            viewHolder.remainingTextView.text = timeLeftStr
        }
    }

    override fun getItemCount() = parks.size

    fun setData(newParks: List<Park>) {
        parks = newParks
        notifyDataSetChanged()
    }


    interface OnParkListener {
        fun onParkClick(position: Int)
    }
}
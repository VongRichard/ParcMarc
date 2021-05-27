package com.example.parcmarc

import android.content.Context
import android.text.method.ScrollingMovementMethod
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import java.io.File
import java.time.Duration


class ParkAdapter(private var parksWithParkImages: List<ParkWithParkImages>, private val onParkListener: OnParkListener, private val context: Context)
    : RecyclerView.Adapter<ParkAdapter.ParkViewHolder>() {

    private val utils: Utilities = Utilities()

    class ParkViewHolder(itemView: View, val onParkListener: OnParkListener)
        : RecyclerView.ViewHolder(itemView), View.OnClickListener {

        val textView: TextView
        val remainingTextView: TextView
        val imageView: ImageView

        init {
            textView = itemView.findViewById(R.id.park_name)
            remainingTextView = itemView.findViewById(R.id.time_remaining)
            itemView.setOnClickListener(this)
            imageView = itemView.findViewById(R.id.imageView)
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
        viewHolder.textView.text = parksWithParkImages[position].park.toString()
        viewHolder.textView.setSelected(true)
        viewHolder.textView.movementMethod = ScrollingMovementMethod()

        viewHolder.remainingTextView.text = timeLeft(parksWithParkImages[position].park.remainingDuration())

        if (parksWithParkImages[position].images.isNotEmpty()) {
            val image = parksWithParkImages[position].images[0]
            val file = File(image.imageURI)
            if (file.exists()) viewHolder.imageView.setImageBitmap(utils.getRotatedBitmapFromFile(file, true))
        }
    }

    override fun getItemCount() = parksWithParkImages.size

    fun setData(newParks: List<ParkWithParkImages>) {
        parksWithParkImages = newParks
        notifyDataSetChanged()
    }

    fun timeLeft(timeLeft : Duration?): String {
        if (timeLeft != null) {
            return when {
                (timeLeft.toMillis() < 0) -> context.getString(R.string.duration_exceeded)
                (timeLeft.toMinutes() < 1L) -> context.getString(R.string.minute_remaining)
                else -> {
                    val hours = timeLeft.toHours(); val minutes = timeLeft.toMinutes() - hours*60
                    context.getString(R.string.time_remaining, hours, minutes)
                }
            }
        }
        return context.getString(R.string.unlimited)
    }


    interface OnParkListener {
        fun onParkClick(position: Int)
    }
}
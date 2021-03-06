package com.example.parcmarc

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.graphics.*
import android.net.Uri
import android.opengl.Visibility
import android.os.Bundle
import android.os.Handler
import android.os.VibrationEffect
import android.os.Vibrator
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.DecelerateInterpolator
import android.widget.*
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat.getSystemService
import androidx.core.graphics.drawable.toBitmap
import androidx.core.os.bundleOf
import androidx.fragment.app.activityViewModels
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.work.WorkManager
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.MarkerOptions
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.io.File
import java.time.Duration


private const val DELAY = 30000L


class ParkFragment : Fragment(), OnMapReadyCallback {
    private val viewModel: ParkViewModel by activityViewModels() {
        ParkViewModelFactory((activity?.application as ParcMarcApplication).repository)
    }
    private val handler: Handler = Handler()
    private lateinit var runnable: Runnable
    private lateinit var map: GoogleMap
    private lateinit var mapView: MapView
    private var images: List<ParkImage> = listOf()
    private val args: ParkFragmentArgs by navArgs()
    private var currentAnimator: Animator? = null
    private var shortAnimationDuration: Int = 0
    private val utils: Utilities = Utilities()
    private lateinit var parkWithParkImages: ParkWithParkImages
    private var time_remaining_textview: TextView? = null

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
        parkWithParkImages = viewModel.parks.value!![args.position]

        setUpToolbar(toolbar)

        val imageView1 = requireView().findViewById<ImageButton>(R.id.park_image_1)
        val imageView2 = requireView().findViewById<ImageButton>(R.id.park_image_2)
        val imageView3 = requireView().findViewById<ImageButton>(R.id.park_image_3)
        images = parkWithParkImages.images
        requireActivity().runOnUiThread {
            val imageViews = listOf(imageView1, imageView2, imageView3)
            for (i in 0..2) {
                if (i+1 <= images.size) {
                    val bitmap = utils.getRotatedBitmapFromFile(File(images[i].imageURI))

                    imageViews[i].setImageBitmap(bitmap)

                    imageViews[i].visibility = View.VISIBLE

                    imageViews[i].setOnClickListener { zoomImageFromThumb(imageViews[i], bitmap) }
                }
            }
        }

        // Retrieve and cache the system's default "short" animation time.
        shortAnimationDuration = resources.getInteger(android.R.integer.config_shortAnimTime)

        mapView = requireView().findViewById<MapView>(R.id.mapView)
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this)

        time_remaining_textview = view.findViewById(R.id.park_time_remaining)
        updateTimeRemaining()
    }

    override fun onResume() {
        runnable = Runnable {
            handler.postDelayed(runnable, DELAY)
            if (time_remaining_textview != null) updateTimeRemaining()
        }.also { runnable = it }

        handler.postDelayed(runnable, DELAY)
        super.onResume()
    }

    private fun updateTimeRemaining() {
        time_remaining_textview!!.text = timeLeft(parkWithParkImages.park.remainingDuration())
    }

    fun timeLeft(timeLeft : Duration?): String {
        if (timeLeft != null) {
            return when {
                (timeLeft.toMillis() < 0) -> requireContext().getString(R.string.duration_exceeded)
                (timeLeft.toMinutes() < 1L) -> requireContext().getString(R.string.minute_remaining)
                else -> {
                    val hours = timeLeft.toHours(); val minutes = timeLeft.toMinutes() - hours*60
                    requireContext().getString(R.string.time_remaining, hours, minutes)
                }
            }
        }
        return requireContext().getString(R.string.unlimited)
    }

    override fun onPause() {
        handler.removeCallbacks(runnable)
        super.onPause()
    }

    /**
     * Handles image enlargement and shrinking with an animation. Code retrieved from
     * https://developer.android.com/training/animation/zoom. Also added code to dim the background
     * when the image is enlarged.
     */
    private fun zoomImageFromThumb(thumbView: View, bitmap: Bitmap) {
        // If there's an animation in progress, cancel it
        // immediately and proceed with this one.
        currentAnimator?.cancel()

        // Load the high-resolution "zoomed-in" image.
        val expandedImageView: ImageView = requireView().findViewById(R.id.expanded_image)
        expandedImageView.setImageBitmap(bitmap)

        // Calculate the starting and ending bounds for the zoomed-in image.
        // This step involves lots of math. Yay, math.
        val startBoundsInt = Rect()
        val finalBoundsInt = Rect()
        val globalOffset = Point()

        // The start bounds are the global visible rectangle of the thumbnail,
        // and the final bounds are the global visible rectangle of the container
        // view. Also set the container view's offset as the origin for the
        // bounds, since that's the origin for the positioning animation
        // properties (X, Y).
        thumbView.getGlobalVisibleRect(startBoundsInt)
        requireView().findViewById<View>(R.id.container)
            .getGlobalVisibleRect(finalBoundsInt, globalOffset)
        startBoundsInt.offset(-globalOffset.x, -globalOffset.y)
        finalBoundsInt.offset(-globalOffset.x, -globalOffset.y)

        val startBounds = RectF(startBoundsInt)
        val finalBounds = RectF(finalBoundsInt)

        // Adjust the start bounds to be the same aspect ratio as the final
        // bounds using the "center crop" technique. This prevents undesirable
        // stretching during the animation. Also calculate the start scaling
        // factor (the end scaling factor is always 1.0).
        val startScale: Float
        if ((finalBounds.width() / finalBounds.height() > startBounds.width() / startBounds.height())) {
            // Extend start bounds horizontally
            startScale = startBounds.height() / finalBounds.height()
            val startWidth: Float = startScale * finalBounds.width()
            val deltaWidth: Float = (startWidth - startBounds.width()) / 2
            startBounds.left -= deltaWidth.toInt()
            startBounds.right += deltaWidth.toInt()
        } else {
            // Extend start bounds vertically
            startScale = startBounds.width() / finalBounds.width()
            val startHeight: Float = startScale * finalBounds.height()
            val deltaHeight: Float = (startHeight - startBounds.height()) / 2f
            startBounds.top -= deltaHeight.toInt()
            startBounds.bottom += deltaHeight.toInt()
        }

        val dimLayout = requireView().findViewById<LinearLayout>(R.id.dim_layout)
        dimLayout.visibility = View.VISIBLE

        // Hide the thumbnail and show the zoomed-in view. When the animation
        // begins, it will position the zoomed-in view in the place of the
        // thumbnail.
        thumbView.alpha = 0f
        expandedImageView.visibility = View.VISIBLE

        // Set the pivot point for SCALE_X and SCALE_Y transformations
        // to the top-left corner of the zoomed-in view (the default
        // is the center of the view).
        expandedImageView.pivotX = 0f
        expandedImageView.pivotY = 0f

        // Construct and run the parallel animation of the four translation and
        // scale properties (X, Y, SCALE_X, and SCALE_Y).
        currentAnimator = AnimatorSet().apply {
            play(
                ObjectAnimator.ofFloat(
                expandedImageView,
                View.X,
                startBounds.left,
                finalBounds.left)
            ).apply {
                with(ObjectAnimator.ofFloat(expandedImageView, View.Y, startBounds.top, finalBounds.top))
                with(ObjectAnimator.ofFloat(expandedImageView, View.SCALE_X, startScale, 1f))
                with(ObjectAnimator.ofFloat(expandedImageView, View.SCALE_Y, startScale, 1f))
            }
            duration = shortAnimationDuration.toLong()
            interpolator = DecelerateInterpolator()
            addListener(object : AnimatorListenerAdapter() {

                override fun onAnimationEnd(animation: Animator) {
                    currentAnimator = null
                }

                override fun onAnimationCancel(animation: Animator) {
                    currentAnimator = null
                }
            })
            start()
        }

        // Upon clicking the zoomed-in image, it should zoom back down
        // to the original bounds and show the thumbnail instead of
        // the expanded image.
        expandedImageView.setOnClickListener {
            currentAnimator?.cancel()
            dimLayout.visibility = View.INVISIBLE;

            // Animate the four positioning/sizing properties in parallel,
            // back to their original values.
            currentAnimator = AnimatorSet().apply {
                play(ObjectAnimator.ofFloat(expandedImageView, View.X, startBounds.left)).apply {
                    with(ObjectAnimator.ofFloat(expandedImageView, View.Y, startBounds.top))
                    with(ObjectAnimator.ofFloat(expandedImageView, View.SCALE_X, startScale))
                    with(ObjectAnimator.ofFloat(expandedImageView, View.SCALE_Y, startScale))
                }
                duration = shortAnimationDuration.toLong()
                interpolator = DecelerateInterpolator()
                addListener(object : AnimatorListenerAdapter() {

                    override fun onAnimationEnd(animation: Animator) {
                        thumbView.alpha = 1f
                        expandedImageView.visibility = View.GONE
                        currentAnimator = null
                    }

                    override fun onAnimationCancel(animation: Animator) {
                        thumbView.alpha = 1f
                        expandedImageView.visibility = View.GONE
                        currentAnimator = null
                    }
                })
                start()
            }
        }
    }


    private fun setUpToolbar(toolbar: androidx.appcompat.widget.Toolbar) {
        toolbar.inflateMenu(R.menu.park_menu);
        toolbar.title = parkWithParkImages.park.name
        toolbar.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.editItem -> {

                    val action = ParkFragmentDirections.actionParkFragmentToCreateNewParkLocation(parkWithParkImages)
                    findNavController().navigate(action)

                    true
                }
                R.id.deleteItem -> {
                    val builder = AlertDialog.Builder(activity)
                    builder.setCancelable(false)
                    builder.setTitle(R.string.delete_park_prompt)
                    builder.apply {
                        setPositiveButton(R.string.delete) { dialog, id ->
                            WorkManager.getInstance(requireContext()).cancelUniqueWork(
                                getString(R.string.app_name) + " " + parkWithParkImages.park.id)
                            viewModel.removePark(parkWithParkImages)
                            this@ParkFragment.findNavController().popBackStack();
                        }
                        setNegativeButton(R.string.cancel) { dialog, id ->
                        }
                    }
                    builder.show()
                    true
                }
                R.id.shareItem -> {
                    val intent = Intent(Intent.ACTION_SEND)
                    intent.type = "text/plain"
                    intent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.location_share_title, parkWithParkImages.park.name))
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
        return getString(R.string.parked_car_at_coordinates, parkWithParkImages.park.location, parkWithParkImages.park.location.latitude.toString(), parkWithParkImages.park.location.longitude.toString())
    }

    @SuppressLint("MissingPermission")
    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap
        map.addMarker(
            MarkerOptions()
                .position(parkWithParkImages.park.location)
                .title(getString(R.string.location))
        )
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(parkWithParkImages.park.location, 15F))

        map.setOnMapLongClickListener {
            val vibrator = requireActivity().getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
            vibrator.vibrate(VibrationEffect.createOneShot(50, VibrationEffect.DEFAULT_AMPLITUDE))

            val uri = "geo:0,0?q=" + parkWithParkImages.park.location.latitude +
                    "," + parkWithParkImages.park.location.longitude
            val gmmIntentUri = Uri.parse(uri)

            val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
            if (mapIntent.resolveActivity(requireActivity().packageManager) != null) {
                mapIntent.setPackage("com.google.android.apps.maps")
                startActivity(mapIntent)
            } else {
                val toast = Toast.makeText(requireActivity().applicationContext,  context?.getString(R.string.no_google_maps), Toast.LENGTH_SHORT)
                toast.show()
            }
        }

        val mainActivity = activity as MainActivity
        if (mainActivity.hasLocationPermissions) {
            map.isMyLocationEnabled = true
        }

        mapView.onResume()
    }
}
package nz.ac.canterbury.seng440.backlog

import android.app.Dialog
import android.app.TimePickerDialog
import android.os.Bundle
import androidx.fragment.app.DialogFragment

class TimePickerFragment : DialogFragment() {
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = TimePickerDialog(activity, listener, 0, 0, true)
        return dialog
    }

    var listener: TimePickerDialog.OnTimeSetListener? = null
    var hour: Int = 6
    var minute: Int = 0

}
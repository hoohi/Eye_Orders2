package com.eyeorders.screens.datedialog

import android.app.DatePickerDialog
import android.app.Dialog
import android.os.Bundle
import android.os.Parcelable
import android.widget.DatePicker
import androidx.fragment.app.DialogFragment
import com.eyeorders.util.date.DateTimeParser
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.parcelize.Parcelize
import java.util.*
import javax.inject.Inject

@Parcelize
data class DateDialogParams(
        val selectedDate: Date? = null,
        val minDate: Date? = null,
        val maxDate: Date? = null
) : Parcelable

@AndroidEntryPoint
class DateDialog : DialogFragment(),
        DatePickerDialog.OnDateSetListener {

    @Inject
    lateinit var dateParser: DateTimeParser

    private val dateParams by lazy { arguments?.getParcelable<DateDialogParams>(DATE_KEY)!! }

    private var callback: ((Date) -> Unit)? = null

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val yearMonthDayTriple = dateParser.convertToYearMonthDay(dateParams.selectedDate ?: Date())
        val year = yearMonthDayTriple.first
        val month = yearMonthDayTriple.second
        val day = yearMonthDayTriple.third
        val dialog = DatePickerDialog(requireContext(), this, year, month, day)

        dateParams.maxDate?.let {
            dialog.datePicker.maxDate = it.time
        }

        dateParams.minDate?.let {
            dialog.datePicker.minDate = it.time
        }

        return dialog
    }

    override fun onDateSet(datePicker: DatePicker, year: Int, month: Int, day: Int) {
        val date = dateParser.convertToDate(year, month, day)
        callback?.invoke(date)
    }

    companion object {
        private const val DATE_KEY = "date_key"
        fun newInstance(dateDialogParams: DateDialogParams = DateDialogParams(), callback: (Date) -> Unit): DateDialog {
            val args = Bundle()
            args.putParcelable(DATE_KEY, dateDialogParams)
            val fragment = DateDialog()
            fragment.callback = callback
            fragment.arguments = args
            return fragment
        }
    }
}
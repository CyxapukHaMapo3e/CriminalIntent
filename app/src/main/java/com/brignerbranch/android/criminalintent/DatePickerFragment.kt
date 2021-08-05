package com.brignerbranch.android.criminalintent

import android.app.DatePickerDialog
import android.app.Dialog
import android.os.Bundle
import android.widget.DatePicker
import androidx.fragment.app.DialogFragment
import java.util.*

private const val ARG_DATE = "date"

class DatePickerFragment : DialogFragment() {

    /*
    OnDateSetListener используется для получения выбранной пользователем даты. Первый параметр - это DatePicker
    от которого исходит результат. Поскольку в данном случае этот параметр не используется, он имеет имя _. Выбранная дата предоставляется,
    в формате года, месяца и дня, но для отправки обратно в CrimeFragment необходим объект Date. Мы передаем эти значения в GregorianCalendar
    и получаем доступ к свойству time, чтобы получить объект Date.
     */

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val date = arguments?.getSerializable(ARG_DATE) as Date
        val calendar = Calendar.getInstance()
        calendar.time = date
        val initialYear = calendar.get(Calendar.YEAR)
        val initialMonth = calendar.get(Calendar.MONTH)
        val initialDay = calendar.get(Calendar.DAY_OF_MONTH)
        val dateListener = DatePickerDialog.OnDateSetListener{
                _: DatePicker, year:Int, month:Int, day:Int ->
            val resultDate: Date = GregorianCalendar(year,month,day).time
            targetFragment?.let{
                fragment -> (fragment as Callbacks).onDateSelected(resultDate)
            }
        }


        return DatePickerDialog(requireContext(),dateListener,initialYear,initialMonth,initialDay)
    }

    interface Callbacks{
        fun onDateSelected(date: Date)
    }

    companion object{
        fun newInstance(date: Date): DatePickerFragment{
            val args = Bundle().apply {
                putSerializable(ARG_DATE,date)
            }
            return DatePickerFragment().apply {
                arguments = args
            }
        }
    }
}
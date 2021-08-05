package com.brignerbranch.android.criminalintent

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import java.util.*
import androidx.lifecycle.Observer

/* СrimeFragment - контроллер, взаимодействующий с объектами модели и представления. Его задача - выдача подробной информации
о конкретном преступлении и ее обновление при модификации пользователем.
 */

private const val TAG = "CrimeFragment"
private const val ARG_CRIME_ID = "crime_id"
private const val DIALOG_DATE = "DialogDate"
private const val REQUEST_DATE = 0

class CrimeFragment : Fragment(), DatePickerFragment.Callbacks {

    private lateinit var crime: Crime
    private lateinit var titleField: EditText
    private lateinit var dateButton: Button
    private lateinit var solvedCheckBox: CheckBox
    private val crimeDetailViewModel: CrimeDetailViewModel by lazy {
        ViewModelProviders.of(this).get(CrimeDetailViewModel::class.java)
    }

    //Настраивается экземпляр фрагмента
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        crime = Crime()
        val crimeId: UUID = arguments?.getSerializable(ARG_CRIME_ID) as UUID
        crimeDetailViewModel.loadCrime(crimeId)

    }

    override fun onDateSelected(date: Date) {
        crime.date = date
        updateUI()
    }
    //Создание и настройка представления фрагмента
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_crime,container,false)
        titleField = view.findViewById(R.id.crime_title) as EditText
        dateButton = view.findViewById(R.id.crime_date) as Button
        solvedCheckBox = view.findViewById(R.id.crime_solved) as CheckBox
        return view
    }

    /*
    Наблюдение за изменениями. crimeDetailViewModel.crimeLiveDate - данные в том виде, в котором они в настоящее время
    хранятся в базе данных.
     */

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        crimeDetailViewModel.crimeLiveData.observe(viewLifecycleOwner, Observer { crime->
            crime?.let {
                this.crime = crime
                updateUI()
            }
        })
    }

    private fun updateUI(){
        titleField.setText(crime.title)
        dateButton.text = crime.date.toString()
        solvedCheckBox.apply{
            isChecked = crime.isSolved
            jumpDrawablesToCurrentState()
        }
    }

    /*
    TextWatcher это интерфейс слушателя. В функции onTextChanged вызывется toString для объекта
    CharSequence, представляющий ввод пользователя. Эта функция возвращает строку, которая затем
    используется для задания заголовка Crime. Некоторые слушатели срабатывают не только при взаимодействии с ними,
    но и при восстановлении состояния виджета, например при повороте. Слушатели, которые реагируют на ввод
    данных такие как TextWatcher для EditText или OnCheckChangedListener для CheckBox, тоже так работают.
     */
    override fun onStart() {
        super.onStart()

        val titleWatcher = object : TextWatcher
        {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                crime.title = p0.toString()
            }

            override fun afterTextChanged(p0: Editable?) {
            }
        }
        titleField.addTextChangedListener(titleWatcher)

        solvedCheckBox.apply {
            setOnCheckedChangeListener{_, isCheked ->
                crime.isSolved = isCheked
            }
        }

        /*
        this@CrimeFragment необходим для вызова функции requireFragmentManager() из CrimeFragment а не из
        DatePickerFragment внутри блока apply.
         */
        dateButton.setOnClickListener{
            DatePickerFragment.newInstance(crime.date).apply {
                setTargetFragment(this@CrimeFragment, REQUEST_DATE)
                show(this@CrimeFragment.requireFragmentManager(), DIALOG_DATE)
            }
        }
    }

    override fun onStop() {
        super.onStop()
        crimeDetailViewModel.saveCrime(crime)
    }
    /*
    Создает экземпляр фрагмента, упаковывает и задает его аргументы.
     */
    companion object{
        fun newInstance(crimeId: UUID):CrimeFragment{
            val args = Bundle().apply {
                putSerializable(ARG_CRIME_ID, crimeId)
            }
            return CrimeFragment().apply {
                arguments=args
            }
        }
    }
}
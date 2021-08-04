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

/* СrimeFragment - контроллер, взаимодействующий с объектами модели и представления. Его задача - выдача подробной информации
о конкретном преступлении и ее обновление при модификации пользователем.
 */

class CrimeFragment : Fragment() {

    private lateinit var crime: Crime
    private lateinit var titleField: EditText
    private lateinit var dateButton: Button
    private lateinit var solvedCheckBox: CheckBox

    //Настраивается экземпляр фрагмента
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        crime = Crime()
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
        dateButton.apply {
            text = crime.date.toString()
            isEnabled = false
        }
        solvedCheckBox = view.findViewById(R.id.crime_solved) as CheckBox
        return view
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
                TODO("Not yet implemented")
            }
        }
        titleField.addTextChangedListener(titleWatcher)

        solvedCheckBox.apply {
            setOnCheckedChangeListener{_, isCheked ->
                crime.isSolved = isCheked
            }
        }
    }
}
package com.brignerbranch.android.criminalintent

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.content.pm.ResolveInfo
import android.net.Uri
import android.os.Bundle
import android.provider.ContactsContract
import android.text.Editable
import android.text.TextWatcher
import android.text.format.DateFormat
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
private const val REQUSET_CONTACT = 1
private const val DATE_FORMAT = "EEE,MMM,dd"


class CrimeFragment : Fragment(), DatePickerFragment.Callbacks {

    private lateinit var crime: Crime
    private lateinit var titleField: EditText
    private lateinit var dateButton: Button
    private lateinit var solvedCheckBox: CheckBox
    private lateinit var reportButton: Button
    private lateinit var suspectButton: Button
    private val crimeDetailViewModel: CrimeDetailViewModel by lazy {
        ViewModelProviders.of(this).get(CrimeDetailViewModel::class.java)
    }

    private fun getCrimeReport(): String {
        val solvedString = if (crime.isSolved) {
            getString(R.string.crime_report_solved)
        } else {
            getString(R.string.crime_report_unsolved)
        }
        val dateString = DateFormat.format(DATE_FORMAT, crime.date).toString()
        var suspect = if (crime.suspect.isBlank()) {
            getString(R.string.crime_report_no_suspect)
        } else {
            getString(R.string.crime_report_suspect, crime.suspect)
        }
        return getString(R.string.crime_report, crime.title, dateString, solvedString, suspect)
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
        val view = inflater.inflate(R.layout.fragment_crime, container, false)
        titleField = view.findViewById(R.id.crime_title) as EditText
        dateButton = view.findViewById(R.id.crime_date) as Button
        solvedCheckBox = view.findViewById(R.id.crime_solved) as CheckBox
        reportButton = view.findViewById(R.id.crime_report) as Button
        suspectButton = view.findViewById(R.id.crime_suspect) as Button
        return view
    }

    /*
    Наблюдение за изменениями. crimeDetailViewModel.crimeLiveDate - данные в том виде, в котором они в настоящее время
    хранятся в базе данных.
     */

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        crimeDetailViewModel.crimeLiveData.observe(viewLifecycleOwner, Observer { crime ->
            crime?.let {
                this.crime = crime
                updateUI()
            }
        })
    }

    private fun updateUI() {
        titleField.setText(crime.title)
        dateButton.text = crime.date.toString()
        solvedCheckBox.apply {
            isChecked = crime.isSolved
            jumpDrawablesToCurrentState()
        }
        if (crime.suspect.isNotEmpty()) {
            suspectButton.text = crime.suspect
        }
    }

    /*
    Получение имени контакта
    Создается запрос всех отображаемых имен контактов в возвращенных данных. Затем запращивается база даных контактов
    и получается объект Cursor, с которым и работаем. После проверки того, что возвращенный курсор содержит хотя бы одну строку, вызывается
    функция Cursor.moveToFirst() для перемещения курсора в первую строку.Вызывается функция moveToFirst() для перемещения
    курсора в первую строку. Вызывается функция getString(Int) для перемещения содержимого первого столбца в виде строки. Эта
    строка будет именем подозреваемого и мы используем ее для установки подозреваемого в преступлении и текста для кнопки CHOOSE SUSPECT.
     */
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when {
            resultCode != Activity.RESULT_OK -> return
            requestCode == REQUSET_CONTACT && data != null -> {
                val contactUri: Uri? = data.data
                //Указать, для каких полей ваш запрос должен возвращать значения.
                val queryFields = arrayOf(ContactsContract.Contacts.DISPLAY_NAME)
                //Выполняемый здес запрос - contactUri похож на предложение "where"
                val cursor = requireActivity().contentResolver.query(
                    contactUri!!,
                    queryFields,
                    null,
                    null,
                    null
                )
                cursor?.use {
                    //Verify cursor contains at least one result
                    if (it.count == 0) {
                        return
                    }
                    //Первый столбец первой строки данных это имя подозреваемого.
                    it.moveToFirst()
                    val suspect = it.getString(0)
                    crime.suspect = suspect
                    crimeDetailViewModel.saveCrime(crime)
                    suspectButton.text = suspect
                }
            }
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

        val titleWatcher = object : TextWatcher {
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
            setOnCheckedChangeListener { _, isCheked ->
                crime.isSolved = isCheked
            }
        }

        /*
        this@CrimeFragment необходим для вызова функции requireFragmentManager() из CrimeFragment а не из
        DatePickerFragment внутри блока apply.
         */
        dateButton.setOnClickListener {
            DatePickerFragment.newInstance(crime.date).apply {
                setTargetFragment(this@CrimeFragment, REQUEST_DATE)
                show(this@CrimeFragment.requireFragmentManager(), DIALOG_DATE)
            }
        }

        /*Неявный интент
        createChooser Создает список, который будет отображаться каждый раз при использованияя
        неявного интента для запуска активити
        */
        reportButton.setOnClickListener {
            Intent(Intent.ACTION_SEND).apply {
                type = "text/plain"
                putExtra(Intent.EXTRA_TEXT, getCrimeReport())
                putExtra(Intent.EXTRA_SUBJECT, getString(R.string.crime_report_subject))
            }.also { intent ->
                val chooserIntent = Intent.createChooser(intent, getString(R.string.send_report))
                startActivity(chooserIntent)
            }
        }

        suspectButton.apply {
            val pickContactIntent =
                Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI)
            setOnClickListener {
                startActivityForResult(pickContactIntent, REQUSET_CONTACT)
            }
            /*Проверка реагирующих активити
            * Флаг MATCH_DEFAULT_ONLY ограничивает поиск активити с флагом CATEGORY_DEFAULT*/
            val packageManager: PackageManager = requireActivity().packageManager
            val resolvedActivity: ResolveInfo? = packageManager.resolveActivity(
                pickContactIntent,
                PackageManager.MATCH_DEFAULT_ONLY)
            if(resolvedActivity == null){
                isEnabled = false
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
    companion object {
        fun newInstance(crimeId: UUID): CrimeFragment {
            val args = Bundle().apply {
                putSerializable(ARG_CRIME_ID, crimeId)
            }
            return CrimeFragment().apply {
                arguments = args
            }
        }
    }
}
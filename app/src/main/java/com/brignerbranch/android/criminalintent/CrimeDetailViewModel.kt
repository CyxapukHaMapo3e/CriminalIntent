package com.brignerbranch.android.criminalintent

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import java.util.*

/*
 Класс для управления запросов к базе данных. CrimeIdLiveData хранит идентификатор отображаемого в данный
 момент преступления фрагментом. При первом создании CrimeDetailViewModel иднтификатор преступления не устанавлвивается.
 В конце концов CrimeFragment вызовет функцию CrimeDetailViewModel.loadCrime(UUID), чтобы ViewModel понял,
 какое преступление ему нужно загрузить.
 Оператор Transformation
 */

class CrimeDetailViewModel() : ViewModel() {

    private val crimeRepository = CrimeRepository.get()
    private val crimeIdLiveData = MutableLiveData<UUID>()

    var crimeLiveData: LiveData<Crime?> = Transformations.switchMap(crimeIdLiveData){ crimeId ->
        crimeRepository.getCrime(crimeId)
    }

    fun loadCrime(crimeId: UUID){
        crimeIdLiveData.value = crimeId
    }

    fun saveCrime(crime: Crime){
        crimeRepository.updateCrime(crime)
    }

}
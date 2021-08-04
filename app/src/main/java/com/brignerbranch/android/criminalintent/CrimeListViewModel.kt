package com.brignerbranch.android.criminalintent

import androidx.lifecycle.ViewModel

/*
  Хранение списка объектов Crime, который отображается на экране.
 */

class CrimeListViewModel : ViewModel() {
   private val crimeRepository = CrimeRepository.get()
    val crimeListLiveData = crimeRepository.getCrimes()
}
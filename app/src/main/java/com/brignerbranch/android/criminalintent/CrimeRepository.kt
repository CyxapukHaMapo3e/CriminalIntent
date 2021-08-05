package com.brignerbranch.android.criminalintent

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.room.Room
import com.brignerbranch.android.criminalintent.datebase.CrimeDatabase
import com.brignerbranch.android.criminalintent.datebase.migration_1_2
import java.lang.IllegalStateException
import java.util.*
import java.util.concurrent.Executors

private const val DATABASE_NAME = "crime-database"

/*
  Репозиторий - одноэлементный класс(синглтон).
 */

class CrimeRepository private constructor(context: Context){

    private val database: CrimeDatabase = Room.databaseBuilder(
        context.applicationContext,
        CrimeDatabase::class.java,
        DATABASE_NAME
    ).addMigrations(migration_1_2).build()

    private val crimeDao = database.crimeDao()
    private val executor = Executors.newSingleThreadExecutor() /* Функция newSingleThreadExecutor()
    возвращает экземпляр исполнителя, который указывает на новый поток*/

    fun getCrimes(): LiveData<List<Crime>> = crimeDao.getCrimes()
    fun getCrime(id: UUID): LiveData<Crime?> = crimeDao.getCrime(id)

    /*
    Оборачивают вызовы ДАО внутри блока execute{}, выталкивая эти операции из основного потока.
     */

    fun updateCrime(crime: Crime){
        executor.execute{
            crimeDao.updateCrime(crime)
        }
    }

    fun addCrime(crime:Crime){
        executor.execute{
            crimeDao.addCrime(crime)
        }
    }

    companion object {
        private var INSTANCE: CrimeRepository? = null


        fun initialize(context: Context) {
            if (INSTANCE == null) {
                INSTANCE = CrimeRepository(context)
            }
        }

        fun get(): CrimeRepository {
            return INSTANCE ?: throw IllegalStateException("CrimeRepository must be initialized")
        }
    }
}
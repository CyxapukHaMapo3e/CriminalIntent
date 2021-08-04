package com.brignerbranch.android.criminalintent.datebase

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.brignerbranch.android.criminalintent.Crime

/*
  @Database - сообщает Room о том, что этот класс представляет собой баззу данных.
 */

@Database(entities = [Crime::class], version = 1)
@TypeConverters(CrimeTypeConverters::class)

abstract class CrimeDatabase : RoomDatabase() {

    abstract fun crimeDao(): CrimeDao
}
package com.brignerbranch.android.criminalintent.datebase

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.brignerbranch.android.criminalintent.Crime

/*
  @Database - сообщает Room о том, что этот класс представляет собой баззу данных.
 */

@Database(entities = [Crime::class], version = 2)
@TypeConverters(CrimeTypeConverters::class)

abstract class CrimeDatabase : RoomDatabase() {

    abstract fun crimeDao(): CrimeDao
}

/* Расширение класса базы данных и как переносить базу данных между версиями.
 Так как начальная версия базы данныъ равна 1, нужно увеличить ее до 2, а затем создать объект Migration, который содержит инструкции
 по обновлению базы данных. Конструктор класса Migration принимает два параметра.
 Первый - версия базы данных, из которых осуществляется миграция, а второй - версия, в которую осуществляется миграция.
 Команда ALTERTABLE, добавляет новый столбец в таблицу преступлений.
 */

val migration_1_2 = object : Migration(1,2){
    override fun migrate(database: SupportSQLiteDatabase){
        database.execSQL("ALTER TABLE Crime ADD COLUMN suspect TEXT NOT NULL DEFAULT ''")
    }
}
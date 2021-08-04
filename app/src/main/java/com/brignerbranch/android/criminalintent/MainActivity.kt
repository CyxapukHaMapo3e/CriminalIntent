package com.brignerbranch.android.criminalintent

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

/*
 MainActivity - хост для CrimeFragment. FragmentManager.beginTransaction() создает и возвращает экземпляр
 FragmentTransaction. Этот класс использует динамчныйинтерфейс: функции, настраивающие FragmentTransaction,
 возвращают FragmentTransaction вместо Unit, что позволяет объединять их вызовы в цепочку. Функция add(..)
 отвечает за основное содержание транзакции. Она получает два параметра: идентификатор контейнерного представления и надвно
 созданный объект CrimeFragment.
 */

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val currentFragment = supportFragmentManager.findFragmentById(R.id.fragment_container)
        if(currentFragment == null){
            val fragment = CrimeListFragment.newInstance()
            supportFragmentManager.beginTransaction().add(R.id.fragment_container,fragment)
                .commit()
        }

    }
}
package com.brignerbranch.android.criminalintent

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import java.util.*

/*
 MainActivity - хост для CrimeFragment. FragmentManager.beginTransaction() создает и возвращает экземпляр
 FragmentTransaction. Этот класс использует динамчныйинтерфейс: функции, настраивающие FragmentTransaction,
 возвращают FragmentTransaction вместо Unit, что позволяет объединять их вызовы в цепочку. Функция add(..)
 отвечает за основное содержание транзакции. Она получает два параметра: идентификатор контейнерного представления и надвно
 созданный объект CrimeFragment.
 */

private const val TAG = "MainActivity"

class MainActivity : AppCompatActivity(), CrimeListFragment.Callbacks {
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

    /*
     Функция FragmentTransaction.replace(Int,Fragment) заменяет фрагмент, размещенный в активити(в контейнере
     с указанным целочисленным идентификатором ресурса), на новый фрагмент. Если фрагмент еще не размещен в указанном
     контейнере, то добавляется новый фрагмент, как еслибы мы вызвали .add
     */

    override fun onCrimeSelected(crimeId: UUID) {
        val fragment = CrimeFragment.newInstance(crimeId)
        supportFragmentManager.beginTransaction().replace(R.id.fragment_container,fragment).addToBackStack(null).commit()
    }
}
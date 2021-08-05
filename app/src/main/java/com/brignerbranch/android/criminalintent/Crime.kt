package com.brignerbranch.android.criminalintent

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.*

/*  Уровень модели CriminalIntent.
  UUID - вспомогательный класс джава входящий в инфраструктуру Android, -
представляет простой способ генерирования универсально-уникальных идентификаторов. В конструкторе такой идентификатор
генерируется вызовом UUID.randomUUID().
  Иницилизация переменной Date с помощью конструктора по умолчанию задает текущую дату.
  @Entity - указывает что класс определяет структуру таблицы или набора таблиц в базе данных.
  @PrimaryKey - указывает какой столбец в базе данных является первичнымключом.
 */

@Entity
data class Crime(
    @PrimaryKey
    val id: UUID = UUID.randomUUID(),
    var title: String = "",
    var date: Date = Date(),
    var isSolved: Boolean = false,
    var suspect: String = "")


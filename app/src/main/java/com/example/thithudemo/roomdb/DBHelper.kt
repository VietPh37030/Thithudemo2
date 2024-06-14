package com.example.thithudemo.roomdb

import androidx.room.Dao
import androidx.room.Database
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.RoomDatabase
import androidx.room.Update
import com.example.thithudemo.model.TranhModel

@Database(entities = arrayOf(TranhModel::class), version = 1)
abstract class TranhDB : RoomDatabase(){
    abstract fun tranhDAO():TranhDAO
}
@Dao
interface TranhDAO{
    @Query("SELECT * FROM Tranh")
    fun getAll():List<TranhModel>

    @Query("SELECT *FROM Tranh WHERE uid IN(:userIds)")
    fun loadAllByIds(userIds:IntArray):List<TranhModel>
    @Insert
    fun insert(vararg user: TranhModel)

    @Update
    fun update(user: TranhModel)

    @Delete
    fun delete(user: TranhModel)
}


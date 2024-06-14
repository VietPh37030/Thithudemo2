package com.example.thithudemo.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "Tranh")
data class TranhModel(
    @PrimaryKey(autoGenerate = true) var uid:Int =  0,
    @ColumnInfo(name = "nameTranh") var nameTranh:String?,
    @ColumnInfo(name ="price") var price:Float?,
    @ColumnInfo(name = "statusTranh") var statusTranh:Boolean?,
    @ColumnInfo(name = "photoPath") var photoPath:String   ? = null
)
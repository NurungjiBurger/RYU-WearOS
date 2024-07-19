package com.example.mullyu.presentation.data

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [MullyuLogistics::class], version = 2)
abstract class MullyuRoomDataBase : RoomDatabase() {
    abstract fun mullyuLogisticsDao(): MullyuLogisticsDao

}
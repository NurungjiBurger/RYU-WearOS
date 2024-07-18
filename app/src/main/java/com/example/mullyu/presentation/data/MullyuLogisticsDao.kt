package com.example.mullyu.presentation.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface MullyuLogisticsDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(logistics: List<MullyuLogistics>)

    @Query("DELETE FROM MullyuLogistics")
    fun deleteAll()

    @Query("SELECT * FROM MullyuLogistics")
    fun getAll(): List<MullyuLogistics>
}
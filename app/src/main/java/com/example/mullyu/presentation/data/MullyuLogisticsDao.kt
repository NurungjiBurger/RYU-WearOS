package com.example.mullyu.presentation.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update

@Dao
interface MullyuLogisticsDao {
    // 삽입
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(logistics: List<MullyuLogistics>)

    // 데이터 삭제
    @Query("DELETE FROM MullyuLogistics")
    fun delete()

    // 데이터 삭제 및 ID 초기화
    @Query("DELETE FROM sqlite_sequence WHERE name='MullyuLogistics'")
    fun deleteAll()

    // 검색
    @Query("SELECT * FROM MullyuLogistics")
    suspend fun getAll(): List<MullyuLogistics>

    // 업데이트
    @Query("UPDATE MullyuLogistics SET isProcess = true WHERE name = :id")
    suspend fun updateIsProcess(id: String)

    // 마지막 ID 검색
    @Query("SELECT MAX(id) FROM MullyuLogistics")
    suspend fun getLastInsertedId(): Int?

    // 단일 robotId 가져오는 메서드 추가
    @Query("SELECT robotId FROM MullyuLogistics LIMIT 1")
    suspend fun getRobotId(): String
}
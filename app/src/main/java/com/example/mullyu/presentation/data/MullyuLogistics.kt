package com.example.mullyu.presentation.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "MullyuLogistics")
data class MullyuLogistics(
    // 고유 ID
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    // 이미지소스
    val imageName: Int,
    // 물류 이름
    val name: String,
    // 수량
    val quantity: String,
    // 처리 상태
    var isProcess: Boolean,
    // 로봇 ID
    val robotId: String
)

package com.example.mullyu.presentation.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "MullyuLogistics")
data class MullyuLogistics(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val quantity: Int,
    val status: String
)
